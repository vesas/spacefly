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
import com.vesas.spacefly.world.AbstractGameWorld;
import com.vesas.spacefly.world.procedural.corridor.Corridor1;
import com.vesas.spacefly.world.procedural.corridor.CorridorBuilder;
import com.vesas.spacefly.world.procedural.generator.MetaCorridor;
import com.vesas.spacefly.world.procedural.generator.MetaCorridorBuilder;
import com.vesas.spacefly.world.procedural.generator.MetaFeature;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;
import com.vesas.spacefly.world.procedural.generator.MetaRoomBuilder;
import com.vesas.spacefly.world.procedural.generator.Region;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;
import com.vesas.spacefly.world.procedural.room.rectangleroom.RectangleRoom;
import com.vesas.spacefly.world.procedural.room.rectangleroom.RectangleRoomBuilder;

public class WorldGen
{	
	private AbstractGameWorld world;

	private int itemCount = 0;
	
	public WorldGen( AbstractGameWorld world, Visibility visib )
	{
		this.world = world;
		
		RectangleRoomBuilder.INSTANCE.setVisib( visib );
		CorridorBuilder.INSTANCE.setVisib( visib );
	}
	public Array<Feature> generate()
	{
		Region metaRegion = generateMetaRegion();
		
		Array<Feature> feats = new Array<Feature>();
		
		buildRooms(metaRegion, feats);
		
		addMonstersPass( metaRegion, feats );
		
		
		return feats;
	}
	
	private void buildRooms( Region region, Array<Feature> feats )
	{
		RectangleRoomBuilder roomBuilder = RectangleRoomBuilder.INSTANCE;
		CorridorBuilder corrBuilder = CorridorBuilder.INSTANCE;
		
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
				Corridor1 corr = corrBuilder.buildFrom( (MetaCorridor)metaFeat );
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

			if( G.random.nextInt(100 ) < 25 )
			{
				SlurgMonster monster = new SlurgMonster(xpos + width * 0.45f, ypos + height * 0.45f );
				world.addMonster( monster );	
			}
			
			if( G.random.nextInt(100 ) < 25 )
			{
				SlurgMonster monster = new SlurgMonster(xpos + width * 0.45f, ypos + height * 0.45f );
				world.addMonster( monster );	
			}
			
			if( G.random.nextInt(100 ) < 25 )
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
	
	static public int REGION_MAX_SIZE = 33;

	/* Generates metaregion. Region is one area of play */
	private Region generateMetaRegion()
	{
		Region region = new Region();

		itemCount = 0;
		
		// Create random first room centered on player
		MetaFeature firstRoom = generateRandomRoom( region, null );
		
		region.add( firstRoom );

		// Generate further content from the portals of the first room
		// 
		// Portal is a transition from one feature to another, it is 1 dimensional (line) with center point and width
		// 
		// From first room we want to get all of the portals and generate content from there
		// 
		Array<MetaFeature> features = generateFromPortals( region, firstRoom.getPortalArray(null) );
		
		return region;
	}

	

	private MetaFeature generateFromPortal( Region region, MetaPortal portal )
	{
		if( this.itemCount >= REGION_MAX_SIZE )
		{
			// itemcount reached, close the portal from the source
			portal.getSource().closePortal(portal);
			return null;
		}

		MetaFeature ret = null;

		if(portal.START_TYPE == MetaPortal.RECTANGLE_ROOM)
		{
			// TODO: add probability
			ret = createRandomCorridor( region, portal );
		}

		if(portal.START_TYPE == MetaPortal.CORRIDOR)
		{
			ret = generateRandomRoom( region, portal );
		}

		if( ret == null)
		{
			// Cannot build into that direction, close the portal from the source
			portal.getSource().closePortal(portal);
		}
		else
		{
			region.add(ret);
		}

		this.itemCount++;
		return ret;
	}
	
	private Array<MetaFeature> generateFromPortals( Region region, Array<MetaPortal> portals )
	{
		Array<MetaFeature> ret = new Array<MetaFeature>();

		Array<MetaPortal> nextRound = new Array<MetaPortal>();

		for( MetaPortal portal : portals )
		{
			MetaFeature feat = generateFromPortal(region, portal);
			
			if( feat != null ) 
			{
				nextRound.addAll(feat.getPortalArray(portal));
				ret.add(feat);
			}
				
		}

		// and generate further features from the portals in the nextRound array
		if(nextRound.size > 0) 
		{
			Array<MetaFeature> features = generateFromPortals( region, nextRound );
			ret.addAll(features);
		}
		

		return ret;
	}
	
	private MetaCorridor createRandomCorridor( Region region, MetaPortal portal )
	{
		MetaCorridorBuilder metaCorrBuilder = MetaCorridorBuilder.INSTANCE;
		
		metaCorrBuilder.createFromPortal( portal );
		metaCorrBuilder.setLength( 1 + GenSeed.random.nextInt( 10 ) );
		
		MetaCorridor corr = metaCorrBuilder.build();
		
		if( !region.canAdd( corr ))
		{
			return null;
		}
		
		return corr;
	}
	
	private MetaRectangleRoom generateRandomRoom( Region region, MetaPortal fromPortal )
	{
		MetaRoomBuilder roomBuilder = MetaRoomBuilder.INSTANCE;
		
		int minwidth = 3;
		int minheight = 3;
		
		if( fromPortal != null )
		{
			if( fromPortal.getExit() == ExitDir.N || 
				fromPortal.getExit() == ExitDir.S )
			{
				minwidth = (int) (fromPortal.getWidth() + 2);
			}
			
			if( fromPortal.getExit() == ExitDir.E || 
				fromPortal.getExit() == ExitDir.W )
			{
				minheight = (int) (fromPortal.getWidth() + 2);
			}
		}
		
		// have to be at least minx/miny long/wide to accommodate the possible corridor
		float w = (float) Math.max(minwidth, GenSeed.random.nextInt( 28 ) );
		float h = (float) Math.max(minheight, GenSeed.random.nextInt( 28 ) );
		
		roomBuilder.setSize( w, h );
		
		ExitDir excludeDir = null;
		
		if( fromPortal == null )
		{
			Vector2 playerCenter = Player.INSTANCE.getWorldCenter();
			roomBuilder.setPosition( playerCenter.x - w * 0.5f, playerCenter.y - h * 0.5f);
		}	
		else
		{
			excludeDir = fromPortal.getExit().getOpposite();
			roomBuilder.createFromPortal(fromPortal);
			
		}

		boolean []existingExits = new boolean[4];
		if( excludeDir != null )
			existingExits[excludeDir.ordinal()] = true;

		// 0-3
		int howManyAdditionalExits = GenSeed.random.nextInt(4);

		if( fromPortal == null )
		{
			// for the first room we create exactly one exit
			howManyAdditionalExits = 1;
		}
		
		for(int i = 0; i < howManyAdditionalExits; i++ )
		{
			ExitDir ex = ExitDir.getRandomExcluding( existingExits );

			if(ex == null)
				break;
				
			existingExits[ex.ordinal()] = true;
			
			if( ex.equals(ExitDir.N ) || ex.equals(ExitDir.S ) )
			{
				int portalWidth = (int) Math.max( 1 , 1 + GenSeed.random.nextInt(6) );
				portalWidth = (int) Math.min( portalWidth , w - 2);
				
				roomBuilder.addPortal( ex, portalWidth );
			}
			else 
			{
				int portalWidth = (int) Math.max( 1, 1 + GenSeed.random.nextInt(6) );
				portalWidth = (int) Math.min( portalWidth , h - 2);
				
				roomBuilder.addPortal( ex, portalWidth );
			}
		}
		
		MetaRectangleRoom room = roomBuilder.build();

		if( !region.canAdd( room ))
		{
			return null;
		}

		return room;
	}
}

