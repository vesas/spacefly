package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.monster.ShellMonster;
import com.vesas.spacefly.monster.SlurgMonster;
import com.vesas.spacefly.monster.ZipperCloud;
import com.vesas.spacefly.monster.ZipperCloudManager;
import com.vesas.spacefly.monster.ZipperMonster;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.AddMonsterCallback;
import com.vesas.spacefly.world.procedural.corridor.AxisAlignedCorridor;
import com.vesas.spacefly.world.procedural.corridor.AxisAlignedCorridorBuilder;
import com.vesas.spacefly.world.procedural.generator.MetaCorridor;
import com.vesas.spacefly.world.procedural.generator.MetaCorridorBuilder;
import com.vesas.spacefly.world.procedural.generator.MetaFeature;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;
import com.vesas.spacefly.world.procedural.generator.MetaRegionBuilder;
import com.vesas.spacefly.world.procedural.generator.MetaRoomBuilder;
import com.vesas.spacefly.world.procedural.generator.Region;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;
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
	}
	public Array<Feature> generate()
	{
		metaRegionBuilder.setSize(35);
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
		}
	}

	private void addMonstersPass( Region region, Array<Feature> feats )
	{
		// Skip first two features (ie. the first room, and the first corridor leading from that)
		for( int i = 2; i < feats.size; i++ )
		{
			Feature feat = feats.get(i);
			
			final float xpos = feat.getXpos();
			final float ypos = feat.getYpos();
			
			final float height = feat.getHeight();
			final float width = feat.getWidth();

			for( int j = 0, size = G.random.nextInt(3); j < size; j++)
			{
				SlurgMonster monster = new SlurgMonster(xpos + width * 0.45f, ypos + height * 0.45f );
				world.addMonster( monster );	
			}
			
			if( G.random.nextInt(100 ) < 18 )
			{
				ShellMonster monster = new ShellMonster(xpos + width * 0.45f, ypos + height * 0.45f );
				world.addMonster( monster );	
			}
			
			if( G.random.nextInt(100 ) < 30 )
			{
				ZipperCloud cloud = new ZipperCloud();
				ZipperCloudManager.add(cloud);
				
				int cloudsize = G.random.nextInt(14) + 2;
				for( int j = 0; j < cloudsize; j++  )
				{
					ZipperMonster monst = new ZipperMonster(xpos + width * 0.35f + G.random.nextFloat() * 0.5f, ypos + height * 0.4f + G.random.nextFloat() * 0.5f, cloudsize );
					world.addMonster( monst );
					monst.addCloud( cloud );	
				}
			}
			
		}
	}
	
	
}

