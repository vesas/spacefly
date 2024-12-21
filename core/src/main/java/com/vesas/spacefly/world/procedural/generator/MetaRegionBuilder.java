package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public class MetaRegionBuilder {
    
    public static final int REGION_MAX_SIZE = 45;

	private int itemCount = 0;
	private int size;

	public void setSize(int size)
	{
		this.size = size;
	}

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
		this.itemCount++;
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
		if( this.itemCount >= this.size )
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

			// Just try twice more if by random chance can fit
			if(ret== null) {
				ret = createRandomCorridor( region, portal );
			}
			if(ret== null) {
				ret = createRandomCorridor( region, portal );
			}
		}

		if(portal.START_TYPE == MetaPortal.CORRIDOR)
		{
			// TODO: THIS IS TEMPORARY, change this
			if(GenSeed.random.nextDouble() < 0.2999999 )
				ret = generateRandomOctaRoom(region, portal);
			else
				ret = generateRandomRoom( region, portal );
			
			// Just try twice more if by random chance can fit
			if(ret== null) {
				ret = generateRandomRoom( region, portal );
			}
			if(ret== null) {
				ret = generateRandomRoom( region, portal );
			}
		}

		if( ret == null)
		{
			// Cannot build into that direction, close the portal from the source
			portal.getSource().closePortal(portal);
		}
		else
		{
			region.add(ret);
			this.itemCount++;
		}

		return ret;
	}
	
	private Array<MetaFeature> generateFromPortals( Region region, Array<MetaPortal> portals )
	{
		Array<MetaFeature> ret = new Array<MetaFeature>();
		Array<MetaPortal> nextRound = new Array<MetaPortal>();

		for( MetaPortal portal : portals ) {
			MetaFeature feat = generateFromPortal(region, portal);
			
			if( feat != null ) {
				nextRound.addAll(feat.getPortalArray(portal));
				ret.add(feat);
			}
		}

		// and generate further features from the portals in the nextRound array
		if(nextRound.size > 0) {
			Array<MetaFeature> features = generateFromPortals( region, nextRound );
			ret.addAll(features);
		}
		

		return ret;
	}
	
	private MetaCorridor createRandomCorridor( Region region, MetaPortal portal )
	{
		MetaCorridor corr = MetaCorridorBuilder.INSTANCE
			.createFromPortal( portal )
			.setLength( 1 + GenSeed.random.nextInt( 14 ) )
			.build();
		
		portal.setTarget(corr);
		
		if( !region.canAdd( corr ))
		{
			return null;
		}
		
		return corr;
	}
	
	private MetaFeature generateRandomRoom( Region region, MetaPortal fromPortal )
	{
		MetaRectangleRoomBuilder roomBuilder = MetaRectangleRoomBuilder.INSTANCE;
		roomBuilder.init();
		
		int minwidth = 3;
		int minheight = 3;

		float w = minwidth;
		float h = minheight;

		ExitDir excludeDir = null;

		int howManyAdditionalExits = 0;
		boolean []existingExits = new boolean[4];
		
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

			// have to be at least minx/miny long/wide to accommodate the possible corridor
			w = Math.max(minwidth, GenSeed.random.nextInt( 26 ) );
			h = Math.max(minheight, GenSeed.random.nextInt( 26 ) );

			roomBuilder.setSize( w, h );

			excludeDir = fromPortal.getExit().getOpposite();
			roomBuilder.createFromDir(fromPortal.getExit().getOpposite(),fromPortal);

			// 0-3
			howManyAdditionalExits = GenSeed.random.nextInt(4);

			// In the beginning add more exits
			if( IDGenerator.getCurrentId() < 16 && howManyAdditionalExits == 0 )
			{
				howManyAdditionalExits = 2;
			}

			if( excludeDir != null )
				existingExits[excludeDir.ordinal()] = true;
		}
		else {
			// lets set first room explicitly
			w = 5;
			h = 5;
			roomBuilder.setSize( w, h);
			roomBuilder.setPosition( firstRoomCenter.x - w * 0.51f, firstRoomCenter.y - h * 0.51f);

			// for the first room we create exactly one exit
			howManyAdditionalExits = 1;

			// Lets just define so that we have "existing" exists in all directions except north,
			// This forces first direction north
			existingExits[ExitDir.S.ordinal()] = true;
			existingExits[ExitDir.W.ordinal()] = true;
			existingExits[ExitDir.E.ordinal()] = true;
		}

		for(int i = 0; i < howManyAdditionalExits; i++ )
		{
			ExitDir ex = ExitDir.getRandomExcluding( existingExits );

			if(ex == null)
				break;
				
			existingExits[ex.ordinal()] = true;
			
			int portalWidth = 1 + GenSeed.random.nextInt(7);
			
			if( ex.equals(ExitDir.N ) || ex.equals(ExitDir.S ) ) {
				portalWidth = (int) Math.min( portalWidth , w - 2);	
			}
			else {
				portalWidth = (int) Math.min( portalWidth , h - 2);	
			}
			roomBuilder.addPortal( ex, portalWidth );
		}
		
		MetaFeature room = roomBuilder.build();

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

	private MetaFeature generateRandomOctaRoom(Region region, MetaPortal fromPortal) {
		MetaOctaRoomBuilder roomBuilder = MetaOctaRoomBuilder.INSTANCE;
		roomBuilder.init();

		float sidewidth = 0;
		float w, h;

		ExitDir excludeDir = null;

		int howManyAdditionalExits = 0;
		boolean []existingExits = new boolean[4];
		
		if( fromPortal != null )
		{
			sidewidth = fromPortal.getWidth();

			w = sidewidth * (1 + (float)Math.sqrt(2));
			h = w;

			roomBuilder.setSize( w, h );

			excludeDir = fromPortal.getExit().getOpposite();
			roomBuilder.createFromDir(fromPortal.getExit().getOpposite(),fromPortal);

			// 0-3
			howManyAdditionalExits = GenSeed.random.nextInt(4);

			// In the beginning add more exits
			if( IDGenerator.getCurrentId() < 16 && howManyAdditionalExits == 0 )
			{
				howManyAdditionalExits = 2;
			}

			if( excludeDir != null )
				existingExits[excludeDir.ordinal()] = true;
		}

		// https://en.wikipedia.org/wiki/Octagon
		// This is the length one side of the octagon (one of the 8 sides)
		float portalsize = sidewidth;

		for(int i = 0; i < howManyAdditionalExits; i++ )
		{
			ExitDir ex = ExitDir.getRandomExcluding( existingExits );

			if(ex == null)
				break;
				
			existingExits[ex.ordinal()] = true;
			
			if( ex.equals(ExitDir.N ) || ex.equals(ExitDir.S ) )
			{
				roomBuilder.addPortal( ex, portalsize );
			}
			else 
			{
				roomBuilder.addPortal( ex, portalsize );
			}
		}
		
		MetaFeature room = roomBuilder.build();

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
