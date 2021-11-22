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
import com.vesas.spacefly.world.AbstractGameWorld;
import com.vesas.spacefly.world.procedural.corridor.Corridor1;
import com.vesas.spacefly.world.procedural.corridor.CorridorBuilder;
import com.vesas.spacefly.world.procedural.generator.MetaCorridor;
import com.vesas.spacefly.world.procedural.generator.MetaCorridorBuilder;
import com.vesas.spacefly.world.procedural.generator.MetaFeature;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.MetaRoom;
import com.vesas.spacefly.world.procedural.generator.MetaRoomBuilder;
import com.vesas.spacefly.world.procedural.generator.Region;
import com.vesas.spacefly.world.procedural.room.ExitDir;
import com.vesas.spacefly.world.procedural.room.RectangleRoom;
import com.vesas.spacefly.world.procedural.room.RectangleRoomBuilder;
import com.vesas.spacefly.visibility.*;

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
		Region metaRegion = generateMeta();
		
		Array<Feature> feats = new Array<Feature>();
		
		buildRooms(metaRegion, feats);
		
		addMonstersPass( metaRegion, feats );
		
		
		return feats;
	}
	
	private void buildRooms( Region region, Array<Feature> feats )
	{
		RectangleRoomBuilder roomBuilder = RectangleRoomBuilder.INSTANCE;
		CorridorBuilder corrBuilder = CorridorBuilder.INSTANCE;
		
		roomBuilder.setWorld( world );
		corrBuilder.setWorld( world );
		
		Array<MetaFeature> metaFeats = region.getMetaList();
		
		for( int i = 0; i < metaFeats.size; i++ )
		{
			
			MetaFeature metaFeat = metaFeats.get( i );
			
			
			if( metaFeat instanceof MetaRoom )
			{
				RectangleRoom room = roomBuilder.buildFrom( ((MetaRoom)metaFeat));
				
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
	
	static public int REGION_MAX_SIZE = 1;

	private Region generateMeta()
	{
		GenSeed.random.setSeed( 7353757 );
		
		Region region = new Region();
		
		int size = 0;
		
		MetaRoom currentRoom = generateRandomRoom( region, null );
		
		region.add( currentRoom );
		
		generateForOneRoom( region, currentRoom );
			
		return region;
	}
	
	
	
	private boolean generateForOneRoom( Region region, MetaRoom currentRoom )
	{
		if( region.getSize() >= REGION_MAX_SIZE )
		{
			return false;
		}
			
//		if( region.getSize() > 7 )
			
		
		Array<MetaCorridor> cors = createCorridorsFrom( region, currentRoom );
		
		for( int i = 0; i< cors.size; i++ )
		{
			MetaCorridor cor = cors.get( i );
			
			if( region.getSize() < REGION_MAX_SIZE )
			{
				MetaRoom r = generateRandomRoom( region, cor );
				
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
	
	private Array<MetaCorridor> createCorridorsFrom( Region region, MetaRoom currentRoom )
	{
		Array<MetaCorridor> ret = new Array<MetaCorridor>();
		
		MetaPortal nPortal = currentRoom.getPortal( ExitDir.N );
		MetaPortal ePortal = currentRoom.getPortal( ExitDir.E );
		MetaPortal wPortal = currentRoom.getPortal( ExitDir.W );
		MetaPortal sPortal = currentRoom.getPortal( ExitDir.S);
		
		int qwe = 0;
		if( ePortal != null )
		{
			MetaCorridor cor = createCorridorFrom( region, ePortal );
			
			if( cor == null )
				qwe = 0;
//				currentRoom.shutPortal( ePortal );
			else
				ret.add( cor );
		}
		
		if( wPortal != null )
		{
			MetaCorridor cor = createCorridorFrom( region, wPortal );
			
			
			if( cor == null )
				qwe = 0;
//				currentRoom.shutPortal( wPortal );
			else
				ret.add( cor );
		}
		
		if( sPortal != null )
		{
			MetaCorridor cor = createCorridorFrom( region, sPortal );
			
			if( cor == null )
				qwe = 0;
//				currentRoom.shutPortal( sPortal );
			else
				ret.add( cor );
		}
		
		if( nPortal != null )
		{
			MetaCorridor cor = createCorridorFrom( region, nPortal );
			
			if( cor == null )
				qwe = 0;
//				currentRoom.shutPortal( nPortal );
			else
				ret.add( cor );
		}
		
		return ret;
	}
	
	private MetaCorridor createCorridorFrom( Region region, MetaPortal portal )
	{
		MetaCorridorBuilder corrBuilder = MetaCorridorBuilder.INSTANCE;
		
		int len = 1 + GenSeed.random.nextInt( 10 );
		corrBuilder.createFromPortal( portal );
		corrBuilder.setLength( len );
		
		MetaCorridor corr = corrBuilder.build();
		
		if( region.canAdd( corr ))
		{
			region.add( corr );
			return corr;
		}
		
		return null;
	}
	
	private MetaRoom generateRandomRoom( Region region, MetaCorridor fromCorridor )
	{
		MetaRoomBuilder roomBuilder = MetaRoomBuilder.INSTANCE;
		
		int valx = GenSeed.random.nextInt( 15 );
		int valy = GenSeed.random.nextInt( 15 );
		
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
		
		float w = (float) (minx + valx);
		float h = (float) (miny + valy);
		
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
		boolean createThirdPortal = ((region.getSize() + 3) < WorldGen.REGION_MAX_SIZE ) && (GenSeed.random.nextFloat() > 0.9 );
		
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
		
		
		MetaRoom room1 = roomBuilder.build();
		
		return room1;
	}
	
	
	/*
	
	private MetaRoom generateStartRoom()
	{
		MetaRoomBuilder roomBuilder = MetaRoomBuilder.INSTANCE;
		
		int valx = random.nextInt( 5 );
		int valy = random.nextInt( 5 );
		if( valy == 3 )
			valy++;
		
		float w = (float) (7.0f + valx);
		float h = (float) (7.0f + valy);
		
		roomBuilder.setSize( w, h );
		roomBuilder.setPosition( 0, 0 );
		roomBuilder.addPortal( Exits.EXIT_NORTH, 2.0f );
		roomBuilder.addPortal( Exits.EXIT_EAST, 4.0f );
		
		MetaRoom room1 = roomBuilder.build();
		return room1;
	}
	
	private Region generateMeta()
	{
		MetaRoomBuilder roomBuilder = MetaRoomBuilder.INSTANCE;
		
		Region region = new Region();
		
		MetaRoom firstRoom = generateStartRoom();
		region.add( firstRoom );
		
		MetaCorridorBuilder corrBuilder = MetaCorridorBuilder.INSTANCE;
		
		corrBuilder.createFromPortal( firstRoom.getPortal( Exits.EXIT_NORTH ) );
		corrBuilder.setLength( 2 );
		
		MetaCorridor corr = corrBuilder.build();
		
		if( region.canAdd( corr ))
			region.add( corr );
		
		corrBuilder.createFromPortal( firstRoom.getPortal( Exits.EXIT_EAST ) );
		corrBuilder.setLength( 8 );
		
		MetaCorridor corr2 = corrBuilder.build();
		
		if( region.canAdd( corr2 ))
			region.add( corr2 );
		
		roomBuilder.setSize( 6.0f, 6.0f );
		roomBuilder.createFromPortal( corr2.getPortal( Exits.EXIT_EAST ) );
		MetaRoom room1a = roomBuilder.build();
		
		if( region.canAdd( room1a ) )
			region.add( room1a );
		
		roomBuilder.setSize( 9.0f, 7.0f );
		roomBuilder.createFromPortal( corr.getPortal( Exits.EXIT_NORTH ) );
		roomBuilder.addPortal( Exits.EXIT_WEST, 2.0f );
		
		MetaRoom room2 = roomBuilder.build();
	 	
		if( region.canAdd( room2 ) )
			region.add( room2 );
		
		
		corrBuilder.createFromPortal( room2.getPortal( Exits.EXIT_WEST ) );
		corrBuilder.setLength( 12 );
		
		MetaCorridor corr3 = corrBuilder.build();
		
		if( region.canAdd( corr3 ))
			region.add( corr3 );
		
		
		
		roomBuilder.setSize( 9.0f, 27.0f );
		roomBuilder.createFromPortal( corr3.getPortal( Exits.EXIT_WEST ) );
		roomBuilder.addPortal( Exits.EXIT_NORTH, 3.0f );
		
		MetaRoom room3 = roomBuilder.build();
	 	
		if( region.canAdd( room3 ) )
			region.add( room3 );
		
		corrBuilder.createFromPortal( room3.getPortal( Exits.EXIT_NORTH ) );
		corrBuilder.setLength( 42 );
		
		MetaCorridor corr4 = corrBuilder.build();
		
		if( region.canAdd( corr4 ))
			region.add( corr4 );

	
		roomBuilder.setSize( 15.0f, 17.0f );
		roomBuilder.createFromPortal( corr4.getPortal( Exits.EXIT_NORTH ) );
//		roomBuilder.addPortal( Exits.EXIT_NORTH, 3.0f );
		
		MetaRoom room4 = roomBuilder.build();
		if( region.canAdd( room4 ))
			region.add( room4 );
		
		return region;
	}
	*/

}

