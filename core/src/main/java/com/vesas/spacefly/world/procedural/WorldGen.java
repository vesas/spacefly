package com.vesas.spacefly.world.procedural;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.monster.ShellMonster;
import com.vesas.spacefly.monster.ShootStickMonster;
import com.vesas.spacefly.monster.SlurgMonster;
import com.vesas.spacefly.monster.ZipperCloud;
import com.vesas.spacefly.monster.ZipperCloudManager;
import com.vesas.spacefly.monster.ZipperMonster;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.AddMonsterCallback;
import com.vesas.spacefly.world.procedural.FloorTheme;
import com.vesas.spacefly.world.procedural.corridor.AxisAlignedCorridor;
import com.vesas.spacefly.world.procedural.corridor.AxisAlignedCorridor.Dir;
import com.vesas.spacefly.world.procedural.corridor.AxisAlignedCorridorBuilder;
import com.vesas.spacefly.world.procedural.generator.MetaCorridor;
import com.vesas.spacefly.world.procedural.generator.MetaFeature;
import com.vesas.spacefly.world.procedural.generator.MetaLoopCloser;
import com.vesas.spacefly.world.procedural.generator.MetaOctaRoom;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;
import com.vesas.spacefly.world.procedural.generator.MetaRegionBuilder;
import com.vesas.spacefly.world.procedural.generator.MetaRoleAssigner;
import com.vesas.spacefly.world.procedural.generator.MetaRoom;
import com.vesas.spacefly.world.procedural.generator.Region;
import com.vesas.spacefly.world.procedural.generator.RoomRole;
import com.vesas.spacefly.world.procedural.room.RoomFeature;
import com.vesas.spacefly.world.procedural.room.octaroom.OctaRoomBuilder;
import com.vesas.spacefly.world.procedural.room.rectangleroom.FeatureBuilder;
import com.vesas.spacefly.world.procedural.room.rectangleroom.RectangleRoom;
import com.vesas.spacefly.world.procedural.room.rectangleroom.RectangleRoomBuilder;

public class WorldGen implements WorldGenInterface
{
	private AddMonsterCallback world;

	MetaRegionBuilder metaRegionBuilder = new MetaRegionBuilder();

	private Vector2 exitPosition = new Vector2();

	private BodyBuilder bodyBuilder = new BodyBuilder();

	private FloorTheme theme;

	private Map<Class<? extends MetaFeature>, FeatureBuilder<?>> builders = new HashMap<>();

	// Current floor number; drives depth scaling. 1 = today's baseline (no scaling).
	private int floor = 1;

	// Vaults recorded during generation so the live world can gate their loot on clearing.
	private Array<VaultInfo> vaults = new Array<VaultInfo>();

	/** Bounds + loot spawn point of a VAULT room, read by the live world for gated reveal. */
	public static class VaultInfo
	{
		public final Rectangle bounds;
		public final Vector2 loot;

		public VaultInfo( Rectangle bounds, Vector2 loot )
		{
			this.bounds = bounds;
			this.loot = loot;
		}
	}

	public Array<VaultInfo> getVaults() {
		return vaults;
	}

	public Vector2 getExitPosition() {
		return exitPosition;
	}

	public Vector2 getFirstRoomCenter() {
		return metaRegionBuilder.getFirstRoomCenter();
	}
	public void setFirstRoomCenter(Vector2 firstRoomCenter) {
		this.metaRegionBuilder.setFirstRoomCenter(firstRoomCenter);
	}

	public WorldGen(AddMonsterCallback world, Visibility visib, FloorTheme theme)
	{
		this.world = world;
		this.theme = theme;

		builders.put(MetaRectangleRoom.class, (FeatureBuilder<?>) new RectangleRoomBuilder(visib, bodyBuilder, theme));
		builders.put(MetaCorridor.class,      (FeatureBuilder<?>) new AxisAlignedCorridorBuilder(visib, bodyBuilder, theme));
		builders.put(MetaOctaRoom.class,      (FeatureBuilder<?>) new OctaRoomBuilder(visib, bodyBuilder, theme));
	}

	@Override
	public Array<Feature> generate(long seed)
	{
		return generate(seed, 1);
	}

	public Array<Feature> generate(long seed, int floor)
	{
		this.floor = floor;

		GenSeed.random.setSeed(seed);
		metaRegionBuilder.setFloor(floor);
		metaRegionBuilder.setSize(44);
		Region metaRegion = metaRegionBuilder.generateMetaRegion();

		// Turn the generated tree into a graph: add a few corridors that bridge nearby
		// rooms, so the player has loops/shortcuts instead of only dead-end branches.
		new MetaLoopCloser(seed, floor).addLoops(metaRegion);

		// Tag a few rooms with a role (vault / arena / sanctuary / boss). Runs after loops so
		// connectivity is final; uses its own RNG so selection doesn't disturb GenSeed.random.
		new MetaRoleAssigner(seed).assign(metaRegion, floor, metaRegionBuilder.getFirstRoomCenter());

		Array<Feature> feats = new Array<Feature>();

		buildFeatures(metaRegion, feats);

		assignFloorPatina(feats);

		recordVaults(metaRegion);

		addMonstersPass( metaRegion, feats );

		findExitPosition(feats);

		return feats;
	}

	private void recordVaults( Region region )
	{
		vaults.clear();
		Array<MetaFeature> list = region.getMetaList();
		for( int i = 0; i < list.size; i++ )
		{
			MetaFeature f = list.get( i );
			if( !(f instanceof MetaRoom) )
				continue;
			if( ((MetaRoom) f).getRole() != RoomRole.VAULT )
				continue;

			Rectangle b = f.getBounds();
			float cx = b.x + b.width * 0.5f;
			float cy = b.y + b.height * 0.5f;
			float lootX = cx, lootY = cy;

			// If the vault has a central column the centre is blocked; drop the loot into the
			// walkable ring just below the column.
			if( f instanceof MetaRectangleRoom && ((MetaRectangleRoom) f).hasColumns() )
				lootY = cy - (((MetaRectangleRoom) f).getHalfColumnWidth() + 0.9f);

			vaults.add( new VaultInfo( new Rectangle( b ), new Vector2( lootX, lootY ) ) );
		}
	}

	// Give every room an organic floor patina. Uses its OWN per-room seed (never GenSeed.random) so
	// the generation stream — and thus monster/layout determinism — is untouched. depth01 is the
	// room's normalized distance from the start, so the material (and, via the room, the ambient)
	// can drift gradually from entrance to exit.
	private void assignFloorPatina(Array<Feature> feats) {
		Vector2 start = metaRegionBuilder.getFirstRoomCenter();

		float maxDist = 1f;
		for (int i = 0; i < feats.size; i++) {
			Feature f = feats.get(i);
			if (!(f instanceof RoomFeature)) continue;
			float cx = f.getXpos() + f.getWidth() * 0.5f;
			float cy = f.getYpos() + f.getHeight() * 0.5f;
			float dist = (float) Math.sqrt((cx - start.x) * (cx - start.x) + (cy - start.y) * (cy - start.y));
			if (dist > maxDist) maxDist = dist;
		}

		long worldSeed = ProceduralGameWorld.worldSeed;
		int floorNumber = ProceduralGameWorld.floorNumber;

		for (int i = 0; i < feats.size; i++) {
			Feature f = feats.get(i);
			if (!(f instanceof RoomFeature)) continue;
			RoomFeature room = (RoomFeature) f;

			float cx = f.getXpos() + f.getWidth() * 0.5f;
			float cy = f.getYpos() + f.getHeight() * 0.5f;
			float dist = (float) Math.sqrt((cx - start.x) * (cx - start.x) + (cy - start.y) * (cy - start.y));
			float depth01 = dist / maxDist;

			long seed = FloorPatina.featureSeed(worldSeed, floorNumber, cx, cy);

			room.setDepth01(depth01);
			room.setPatina(FloorPatina.pick(seed, depth01, theme));
		}
	}

	private void findExitPosition(Array<Feature> feats) {
		Vector2 start = metaRegionBuilder.getFirstRoomCenter();
		float maxDist2 = 0;
		for (int i = 0; i < feats.size; i++) {
			Feature feat = feats.get(i);
			if (feat instanceof AxisAlignedCorridor) continue;
			float cx = feat.getXpos() + feat.getWidth() * 0.5f;
			float cy = feat.getYpos() + feat.getHeight() * 0.5f;
			float dist2 = (cx - start.x) * (cx - start.x) + (cy - start.y) * (cy - start.y);
			if (dist2 > maxDist2) {
				maxDist2 = dist2;
				exitPosition.set(cx, cy);
			}
		}
	}

	
	

	private void buildFeatures( Region region, Array<Feature> features )
	{
		for( MetaFeature metaFeat : region.getMetaList() ) {
			@SuppressWarnings("unchecked")
			FeatureBuilder<MetaFeature> builder = (FeatureBuilder<MetaFeature>) builders.get(metaFeat.getClass());
			if (builder != null) {
				features.add(builder.buildFrom(metaFeat));
			}
		}
	}

	private float[] getMonsterPositions(Feature feat, int index) {

		final float xpos = feat.getXpos();
		final float ypos = feat.getYpos();
		
		final float height = feat.getHeight();
		final float width = feat.getWidth();
		
		if(feat instanceof RectangleRoom) {
			RectangleRoom rectRoom = (RectangleRoom)feat;

			// if the room has columns, adjust the position
			if(rectRoom.hasColumns()) {

				return new float[] {
					xpos + width * 0.25f,
					ypos + height * 0.25f + index * 0.2f
				};
			}
		}
		return new float[] {
			xpos + width * 0.45f + index * 0.2f,
			ypos + height * 0.45f - index * 0.2f
		};
	}

	private void addMonstersPass( Region region, Array<Feature> feats )
	{
		// Depth term: 0 on floor 1, so floor 1 is byte-identical to the pre-scaling behaviour.
		final int d = Math.max( 0, floor - 1 );

		for( int i = 0; i < feats.size; i++) {

			Feature feat = feats.get(i);
			
			final float xpos = feat.getXpos();
			final float ypos = feat.getYpos();
			
			final float height = feat.getHeight();
			final float width = feat.getWidth();

			// Skip first two features (ie. the first room, and the first corridor leading from that)
			if(i >= 2) {


				for( int j = 0, size = GenSeed.random.nextInt(2 + Math.min(d, 3)); j < size; j++)
				{
					float[] positions = getMonsterPositions(feat, j);

					SlurgMonster monster = new SlurgMonster(positions[0], positions[1], bodyBuilder);

					world.addMonster( monster );
				}

				if( GenSeed.random.nextInt(100 ) < Math.min(24, 12 + 3 * d) )
				{
					float[] positions = getMonsterPositions(feat, 3);
					ShellMonster monster = new ShellMonster(positions[0], positions[1], bodyBuilder);
					world.addMonster( monster );
				}

				if( !(feat instanceof AxisAlignedCorridor) )
				{
					// A room with a vent lets a Zipper swarm pour out of it; otherwise the
					// swarm (rarer) forms in the middle of the room as before.
					float[] vent = (feat instanceof RoomFeature) ? ((RoomFeature)feat).randomVentSpawn() : null;
					int chance = Math.min(40, (vent != null ? 24 : 15) + 2 * d);

					if( GenSeed.random.nextInt(100) < chance )
					{
						float cx, cy, jitter;
						if( vent != null )
						{
							// emerge from the vent, nudged off the wall into the room
							float rcx = xpos + width * 0.5f, rcy = ypos + height * 0.5f;
							float dx = rcx - vent[0], dy = rcy - vent[1];
							float len = (float)Math.sqrt(dx * dx + dy * dy);
							if( len < 0.001f ) len = 1f;
							cx = vent[0] + dx / len * 0.45f;
							cy = vent[1] + dy / len * 0.45f;
							jitter = 0.7f;
						}
						else
						{
							cx = xpos + width * 0.5f;
							cy = ypos + height * 0.5f;
							jitter = 1.5f;
						}

						ZipperCloud cloud = new ZipperCloud();
						ZipperCloudManager.add(cloud);

						int count = 6 + GenSeed.random.nextInt(5 + Math.min(4, d));
						for( int z = 0; z < count; z++ )
						{
							float zx = cx + (GenSeed.random.nextFloat() - 0.5f) * jitter;
							float zy = cy + (GenSeed.random.nextFloat() - 0.5f) * jitter;
							ZipperMonster zipper = new ZipperMonster(zx, zy, count, bodyBuilder);
							zipper.addCloud(cloud);
							world.addMonster(zipper);
						}
					}
				}
			}
			

			
			if(feat instanceof AxisAlignedCorridor) {

				AxisAlignedCorridor corr = (AxisAlignedCorridor)feat;

				float tempXPos = 0.0f;
				float tempYPos = 0.0f;
				Vector2 faceDir = new Vector2();
				if(corr.dir == Dir.WE) {
					// south side
					faceDir.x = 0.0f;
					faceDir.y = 1.0f;	

					tempXPos = xpos + width * 0.5f;
					tempYPos = ypos;
				}
				if(corr.dir == Dir.SN && GenSeed.random.nextFloat() > 0.2f) {
					// west side

					if(!GenSeed.random.nextBoolean()) {
						faceDir.x = 1.0f;
						faceDir.y = 0.0f;	

						tempXPos = xpos + 0.38f; // 0.15f is the length of the shooty tube
						tempYPos = ypos + height * 0.5f;
					}
					else {
						faceDir.x = -1.0f;
						faceDir.y = 0.0f;	

						tempXPos = xpos + width - 0.38f; // 0.15f is the length of the shooty tube
						tempYPos = ypos + height * 0.5f;
					}
					
					world.addMonster( new ShootStickMonster(tempXPos, tempYPos, faceDir, bodyBuilder ) );	

					if(height > 3) {
						tempYPos = ypos + height * 0.5f - 1;
						world.addMonster( new ShootStickMonster(tempXPos, tempYPos, faceDir, bodyBuilder ) );	
						tempYPos = ypos + height * 0.5f + 1;
						world.addMonster( new ShootStickMonster(tempXPos, tempYPos, faceDir, bodyBuilder ) );	
					}
					
				}
				
				
			}
		}

	}
	
	
}

