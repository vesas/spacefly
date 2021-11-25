package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public class MetaRegionBuilder {
    
    static public int REGION_MAX_SIZE = 5;

	private int itemCount = 0;

    private Vector2 firstRoomCenter;
	
	public Vector2 getFirstRoomCenter() {
		return firstRoomCenter;
	}
	public void setFirstRoomCenter(Vector2 firstRoomCenter) {
		this.firstRoomCenter = firstRoomCenter;
	}

	/* Generates metaregion. Region is one area of play */
	public Region generateMetaRegion()
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
		portal.setTarget(corr);
		
		if( !region.canAdd( corr ))
		{
			return null;
		}
		
		return corr;
	}
	
	private MetaRectangleRoom generateRandomRoom( Region region, MetaPortal fromPortal )
	{
		MetaRoomBuilder roomBuilder = MetaRoomBuilder.INSTANCE;
		roomBuilder.init();
		
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
			roomBuilder.setPosition( firstRoomCenter.x - w * 0.5f, firstRoomCenter.y - h * 0.5f);
		}	
		else
		{
			excludeDir = fromPortal.getExit().getOpposite();
			roomBuilder.createFromDir(fromPortal.getExit().getOpposite(),fromPortal);
			
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

		if(fromPortal != null)
		{
			fromPortal.setTarget(room);
		}

		if( !region.canAdd( room ))
		{
			return null;
		}

		return room;
	}
}
