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
import com.vesas.spacefly.world.procedural.room.ExitDir;
import com.vesas.spacefly.world.procedural.room.RectangleRoom;
import com.vesas.spacefly.world.procedural.room.RectangleRoomBuilder;

public class WorldGen
{	
	private AbstractGameWorld world;
	
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
	
	static public int REGION_MAX_SIZE = 26;

	/* Generates metaregion. Region is one area of play */
	private Region generateMetaRegion()
	{
		GenSeed.random.setSeed( 24 );
		
		Region region = new Region();
		
		MetaRectangleRoom currentRoom = generateRandomRoom( region, null );
		
		region.add( currentRoom );
		
		generateForOneRoom( region, currentRoom );
			
		return region;
	}
	
	private boolean generateForOneRoom( Region region, MetaRectangleRoom currentRoom )
	{
		if( region.getSize() >= REGION_MAX_SIZE )
		{
			return false;
		}
		
		Array<MetaCorridor> cors = createCorridorsFrom( region, currentRoom );
		
		for( int i = 0; i< cors.size; i++ )
		{
			MetaCorridor cor = cors.get( i );
			
			if( region.getSize() < REGION_MAX_SIZE )
			{
				MetaRectangleRoom r = generateRandomRoom( region, cor );
				
				if( region.canAdd( r ))
				{
					region.add( r );
					
					generateForOneRoom( region, r );
				}
				else
				{
					cor.shutEnd();
				}
			}
			else
			{
				cor.shutEnd();
			}
			
		}
		
		return true;
	}
	
	private Array<MetaCorridor> createCorridorsFrom( Region region, MetaRectangleRoom currentRoom )
	{
		Array<MetaCorridor> ret = new Array<MetaCorridor>();
		
		Array<MetaPortal> portals = currentRoom.getPortals().values().toArray();
		
		for(MetaPortal portal : portals ) 
		{
			MetaCorridor cor = createCorridorFrom( region, portal );

			if( cor != null )
				ret.add( cor );
		}

		return ret;
	}
	
	private MetaCorridor createCorridorFrom( Region region, MetaPortal portal )
	{
		MetaCorridorBuilder corrBuilder = MetaCorridorBuilder.INSTANCE;
		
		corrBuilder.createFromPortal( portal );
		corrBuilder.setLength( 1 + GenSeed.random.nextInt( 10 ) );
		
		MetaCorridor corr = corrBuilder.build();
		
		if( region.canAdd( corr ))
		{
			region.add( corr );
			return corr;
		}
		
		return null;
	}
	
	private MetaRectangleRoom generateRandomRoom( Region region, MetaCorridor fromCorridor )
	{
		MetaRoomBuilder roomBuilder = MetaRoomBuilder.INSTANCE;
		
		int minx = 3;
		int miny = 3;
		
		if( fromCorridor != null )
		{
			if( fromCorridor.getEndPortal().exit == ExitDir.N || 
				fromCorridor.getEndPortal().exit == ExitDir.S )
			{
				minx = (int) (fromCorridor.getWidth() + 2);
			}
			
			if( fromCorridor.getEndPortal().exit == ExitDir.E || 
					fromCorridor.getEndPortal().exit == ExitDir.W )
			{
				miny = (int) (fromCorridor.getWidth() + 2);
			}
		}
		
		// have to be at least minx/miny long/wide to accommodate the possible corridor
		float w = (float) Math.max(minx, GenSeed.random.nextInt( 28 ) );
		float h = (float) Math.max(miny, GenSeed.random.nextInt( 28 ) );
		
		roomBuilder.setSize( w, h );
		
		ExitDir excludeDir = null;
		
		if( fromCorridor == null )
		{
			Vector2 playerCenter = Player.INSTANCE.getWorldCenter();
			roomBuilder.setPosition( playerCenter.x - w * 0.5f, playerCenter.y - h * 0.5f);
//			roomBuilder.setPosition(-46.5f, -24.096667f);
		}	
		else
		{
			excludeDir = fromCorridor.getEndPortal().exit.getOpposite();
			roomBuilder.createFromPortal(fromCorridor.getEndPortal());
			
		}
		
		boolean createFirstPortal = ((region.getSize() + 1) < WorldGen.REGION_MAX_SIZE ) &&  (GenSeed.random.nextFloat() > 0.1 || (region.getSize() < 8) );
		boolean createSecondPortal = ((region.getSize() + 2) < WorldGen.REGION_MAX_SIZE ) && (GenSeed.random.nextFloat() > 0.6 );
		boolean createThirdPortal = ((region.getSize() + 3) < WorldGen.REGION_MAX_SIZE ) && (GenSeed.random.nextFloat() > 0.8 );
		
		// first room
		if( fromCorridor == null )
		{
			createFirstPortal = true;
			createSecondPortal = false;
			createThirdPortal = false;
		}
		
		boolean []exits = new boolean[4];
		if( excludeDir != null )
			exits[excludeDir.ordinal()] = true;
		
		if( createFirstPortal )
		{
			ExitDir ex = ExitDir.getRandomExcluding( excludeDir );
			exits[ex.ordinal()] = true;
			
			if( ex.equals(ExitDir.N ) || ex.equals(ExitDir.S ) )
			{
				int NSportalWidth = (int) Math.max( 1 , 1 + GenSeed.random.nextInt(6) );
				NSportalWidth = (int) Math.min( NSportalWidth , w - 2);
				
				roomBuilder.addPortal( ex, NSportalWidth );
			}
			else 
			{
				int EWportalWidth = (int) Math.max( 1, 1 + GenSeed.random.nextInt(6) );
				EWportalWidth = (int) Math.min( EWportalWidth , h - 2);
				
				roomBuilder.addPortal( ex, EWportalWidth );
			}
		}
		
		if( createSecondPortal )
		{
			ExitDir ex = ExitDir.getRandomExcluding( exits );
			
			if( ex != null )
			{
				exits[ex.ordinal()] = true;
				
				if( ex.equals(ExitDir.N ) || ex.equals(ExitDir.S ) )
				{
					int NSportalWidth = (int) Math.max( 1 , 1 + GenSeed.random.nextInt(5) );
					NSportalWidth = (int) Math.min( NSportalWidth , w - 2);
					
					roomBuilder.addPortal( ex, NSportalWidth );
				}
				else 
				{
					int EWportalWidth = (int) Math.max( 1, 1 + GenSeed.random.nextInt(5) );
					EWportalWidth = (int) Math.min( EWportalWidth , h - 2);
					
					roomBuilder.addPortal( ex, EWportalWidth );
				}
			}
		}
		
		if( createThirdPortal )
		{
			ExitDir ex = ExitDir.getRandomExcluding( exits );
			
			if( ex != null )
			{
				exits[ex.ordinal()] = true;
				
				if( ex.equals(ExitDir.N ) || ex.equals(ExitDir.S ) )
				{
					int NSportalWidth = (int) Math.max( 1 , 1 + GenSeed.random.nextInt(5) );
					NSportalWidth = (int) Math.min( NSportalWidth , w - 2);
					
					roomBuilder.addPortal( ex, NSportalWidth );
				}
				else 
				{
					int EWportalWidth = (int) Math.max( 1, 1 + GenSeed.random.nextInt(5) );
					EWportalWidth = (int) Math.min( EWportalWidth , h - 2);
					
					roomBuilder.addPortal( ex, EWportalWidth );
				}
			}
		}
		
		
		MetaRectangleRoom room1 = roomBuilder.build();
		
		return room1;
	}
}

