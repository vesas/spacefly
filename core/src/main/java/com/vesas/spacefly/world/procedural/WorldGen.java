package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.monster.ShellMonster;
import com.vesas.spacefly.monster.ShootStickMonster;
import com.vesas.spacefly.monster.SlurgMonster;
import com.vesas.spacefly.monster.ZipperCloud;
import com.vesas.spacefly.monster.ZipperCloudManager;
import com.vesas.spacefly.monster.ZipperMonster;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.AddMonsterCallback;
import com.vesas.spacefly.world.procedural.corridor.AxisAlignedCorridor;
import com.vesas.spacefly.world.procedural.corridor.AxisAlignedCorridor.Dir;
import com.vesas.spacefly.world.procedural.corridor.AxisAlignedCorridorBuilder;
import com.vesas.spacefly.world.procedural.generator.MetaCorridor;
import com.vesas.spacefly.world.procedural.generator.MetaFeature;
import com.vesas.spacefly.world.procedural.generator.MetaHexaRoom;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;
import com.vesas.spacefly.world.procedural.generator.MetaRegionBuilder;
import com.vesas.spacefly.world.procedural.generator.Region;
import com.vesas.spacefly.world.procedural.room.hexaroom.HexaRoom;
import com.vesas.spacefly.world.procedural.room.hexaroom.HexaRoomBuilder;
import com.vesas.spacefly.world.procedural.room.rectangleroom.RectangleRoom;
import com.vesas.spacefly.world.procedural.room.rectangleroom.RectangleRoomBuilder;

public class WorldGen
{	
	private AddMonsterCallback world;

	MetaRegionBuilder metaRegionBuilder = new MetaRegionBuilder();

	public Vector2 getFirstRoomCenter() {
		return metaRegionBuilder.getFirstRoomCenter();
	}
	public void setFirstRoomCenter(Vector2 firstRoomCenter) {
		this.metaRegionBuilder.setFirstRoomCenter(firstRoomCenter);
	}

	public WorldGen( AddMonsterCallback world, Visibility visib )
	{
		this.world = world;
		
		RectangleRoomBuilder.INSTANCE.setVisib( visib );
		AxisAlignedCorridorBuilder.INSTANCE.setVisib( visib );
		HexaRoomBuilder.INSTANCE.setVisib(visib);
	}
	public Array<Feature> generate()
	{
		GenSeed.random.setSeed(16464);
		metaRegionBuilder.setSize(16);
		Region metaRegion = metaRegionBuilder.generateMetaRegion();
		
		Array<Feature> feats = new Array<Feature>();
		
		buildRooms(metaRegion, feats);
		
		addMonstersPass( metaRegion, feats );
		
		return feats;
	}
	
	private void buildRooms( Region region, Array<Feature> feats )
	{
		RectangleRoomBuilder roomBuilder = RectangleRoomBuilder.INSTANCE;
		AxisAlignedCorridorBuilder corrBuilder = AxisAlignedCorridorBuilder.INSTANCE;
		HexaRoomBuilder hexaBuilder = HexaRoomBuilder.INSTANCE;
		
		Array<MetaFeature> metaFeats = region.getMetaList();
		
		for( int i = 0; i < metaFeats.size; i++ )
		{
			MetaFeature metaFeat = metaFeats.get( i );
			
			if( metaFeat instanceof MetaRectangleRoom )
			{
				RectangleRoom room = roomBuilder.buildFrom( ((MetaRectangleRoom)metaFeat));
				feats.add( room );
			}
			if( metaFeat instanceof MetaCorridor )
			{
				AxisAlignedCorridor corr = corrBuilder.buildFrom( (MetaCorridor)metaFeat );
				feats.add( corr );
			}
			if( metaFeat instanceof MetaHexaRoom )
			{
				HexaRoom room = hexaBuilder.buildFrom( ((MetaHexaRoom)metaFeat));
				feats.add( room );
			}
		}
	}

	private void addMonstersPass( Region region, Array<Feature> feats )
	{
		// Skip first two features (ie. the first room, and the first corridor leading from that)

		for( int i = 0; i < feats.size; i++) {

			Feature feat = feats.get(i);
			
			final float xpos = feat.getXpos();
			final float ypos = feat.getYpos();
			
			final float height = feat.getHeight();
			final float width = feat.getWidth();

			
			if(feat instanceof AxisAlignedCorridor) {

				AxisAlignedCorridor corr = (AxisAlignedCorridor)feat;

				float x_pos = 0.0f;
				float y_pos = 0.0f;
				Vector2 faceDir = new Vector2();
				if(corr.dir == Dir.WE) {
					// south side
					faceDir.x = 0.0f;
					faceDir.y = 1.0f;	

					x_pos = xpos + width * 0.5f;
					y_pos = ypos;
				}
				if(corr.dir == Dir.SN && GenSeed.random.nextFloat() > 0.2f) {
					// west side

					if(!GenSeed.random.nextBoolean()) {
						faceDir.x = 1.0f;
						faceDir.y = 0.0f;	

						x_pos = xpos + 0.38f; // 0.15f is the length of the shooty tube
						y_pos = ypos + height * 0.5f;
					}
					else {
						faceDir.x = -1.0f;
						faceDir.y = 0.0f;	

						x_pos = xpos + width - 0.38f; // 0.15f is the length of the shooty tube
						y_pos = ypos + height * 0.5f;
					}
					

					world.addMonster( new ShootStickMonster(x_pos, y_pos, faceDir ) );	

					if(height > 3) {
						y_pos = ypos + height * 0.5f - 1;
						world.addMonster( new ShootStickMonster(x_pos, y_pos, faceDir ) );	
						y_pos = ypos + height * 0.5f + 1;
						world.addMonster( new ShootStickMonster(x_pos, y_pos, faceDir ) );	
					}
					
				}
				
				
			}
		}

		for( int i = 2; i < feats.size; i++ )
		{
			Feature feat = feats.get(i);
			
			final float xpos = feat.getXpos();
			final float ypos = feat.getYpos();
			
			final float height = feat.getHeight();
			final float width = feat.getWidth();

			for( int j = 0, size = GenSeed.random.nextInt(1); j < size; j++)
			{
				SlurgMonster monster = new SlurgMonster(xpos + width * 0.45f, ypos + height * 0.45f );
				world.addMonster( monster );	
			}
			
			if( GenSeed.random.nextInt(100 ) < 12 )
			{
				ShellMonster monster = new ShellMonster(xpos + width * 0.45f, ypos + height * 0.45f );
				world.addMonster( monster );	
			}
			
			/*
			if( GenSeed.random.nextInt(100 ) < 10 )
			{
				ZipperCloud cloud = new ZipperCloud();
				ZipperCloudManager.add(cloud);
				
				int cloudsize = GenSeed.random.nextInt(14) + 2;
				for( int j = 0; j < cloudsize; j++  )
				{
					ZipperMonster monst = new ZipperMonster(xpos + width * 0.35f + GenSeed.random.nextFloat() * 0.5f, ypos + height * 0.4f + GenSeed.random.nextFloat() * 0.5f, cloudsize );
					world.addMonster( monst );
					monst.addCloud( cloud );	
				}
			}
			 */
			
		}
	}
	
	
}

