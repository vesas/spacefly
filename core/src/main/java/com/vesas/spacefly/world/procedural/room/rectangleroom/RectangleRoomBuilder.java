package com.vesas.spacefly.world.procedural.room.rectangleroom;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;
import com.vesas.spacefly.world.procedural.room.Block1;
import com.vesas.spacefly.world.procedural.room.WallBlock;
import com.vesas.spacefly.world.procedural.room.BlockUp;
import com.vesas.spacefly.world.procedural.room.FeatureConnector;
import com.vesas.spacefly.visibility.Visibility;

public class RectangleRoomBuilder
{
	// these are in world units
	// bottom left position
	private float xpos, ypos;

	// These are in tile units
	// xsize to the right, ysize to up
	private float xsize, ysize;
	
	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();
	
	private Visibility visib;
	
	public static RectangleRoomBuilder INSTANCE = new RectangleRoomBuilder();
	
	public void setVisib( Visibility visib )
	{
		this.visib = visib;
	}
	
	public RectangleRoomBuilder setPos( float xpos, float ypos )
	{
		this.xpos = xpos; 
		this.ypos = ypos;
		
		return INSTANCE;
	}

	private void buildNorthWall(RectangleRoom room, MetaPortal nPortal) {

		if( nPortal == null )
		{
			// add blocks for the whole length
			addBlocksToRight( xpos, ypos + ysize - RectangleRoom.WALL_WIDTH, xsize);
			
			// xpos + RectangleRoom.WALL_WIDTH because: The floor starts at xpos, wall goes from xpos up to xpos + RectangleRoom.WALL_WIDTH
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
		}
		else
		{
			// calculate side size in units, without the exit
			float xsizeWithoutExit = xsize - nPortal.width;
			// then divide by two to get width of either side of the portal
			float sideSize = xsizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = xsize - (beginSize + nPortal.width);

			// y points down
			room.addRoomEntrance(new RoomEntrance(beginSize, 0, nPortal.width, RectangleRoom.WALL_WIDTH));

			// left side
			addBlocksToRight( xpos, ypos + ysize - RectangleRoom.WALL_WIDTH, beginSize);
			// right side
			addBlocksToRight( xpos + (beginSize + nPortal.width), ypos + ysize- RectangleRoom.WALL_WIDTH, endSize);
			
			// left side
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + beginSize, ypos + ysize - RectangleRoom.WALL_WIDTH);

			// right side
			visib.addSegment( xpos + (beginSize + nPortal.width), ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
			
		}
	}

	private void buildSouthWall(RectangleRoom room, MetaPortal sPortal) {
		if( sPortal == null )
		{
			// add blocks for the whole length
			addBlocksToRight( xpos, ypos, xsize);
			
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH);
			
		}
		else
		{
			float xsizeWithoutExit = xsize - sPortal.width;
			float sideSize = xsizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = xsize - (beginSize + sPortal.width);

			// y points down
			room.addRoomEntrance(new RoomEntrance(beginSize, ysize-RectangleRoom.WALL_WIDTH, sPortal.width, RectangleRoom.WALL_WIDTH));
			
			addBlocksToRight( xpos, ypos, beginSize);
			addBlocksToRight( xpos + (beginSize + sPortal.width), ypos, endSize);
			
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + beginSize, ypos + RectangleRoom.WALL_WIDTH);
			visib.addSegment( xpos + (beginSize + sPortal.width), ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH);
		}
	}

	private void buildWestWall(RectangleRoom room, MetaPortal wPortal) {
		if( wPortal == null )
		{
			// add blocks for the whole length
			addBlocksToUp( xpos, ypos + RectangleRoom.WALL_WIDTH,  ysize - 2 * RectangleRoom.WALL_WIDTH);
			
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
		}
		else
		{
			float ysizeWithoutExit = ysize - wPortal.width;
			float sideSize = ysizeWithoutExit / 2;
//			if( sideSize % 2 != 0 )
//				sideSize += RectangleRoom.WALL_WIDTH;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = ysizeWithoutExit - beginSize;

			// y points down
			room.addRoomEntrance(new RoomEntrance(0, 0 + beginSize, RectangleRoom.WALL_WIDTH, wPortal.width));
			
			addBlocksToUp( xpos, ypos + RectangleRoom.WALL_WIDTH,  beginSize - RectangleRoom.WALL_WIDTH);
			addBlocksToUp( xpos, ypos + (beginSize + wPortal.width), endSize - RectangleRoom.WALL_WIDTH);
			
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + RectangleRoom.WALL_WIDTH, ypos + beginSize);
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + (beginSize + wPortal.width), xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
			
		}
	}

	private void buildEastWall(RectangleRoom room, MetaPortal ePortal) {
		if( ePortal == null )
		{
			// add blocks for the whole length
			addBlocksToUp( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH,  ysize - 2 * RectangleRoom.WALL_WIDTH);
			
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
		}
		else
		{
			float ysizeWithoutExit = ysize - ePortal.width;
			float sideSize = ysizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = ysize - (beginSize + ePortal.width);

			// y points down
			room.addRoomEntrance(new RoomEntrance(xsize - RectangleRoom.WALL_WIDTH, 0 + beginSize, RectangleRoom.WALL_WIDTH, ePortal.width));
			
			addBlocksToUp( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH,  beginSize - RectangleRoom.WALL_WIDTH);
			addBlocksToUp( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos+ (beginSize + ePortal.width), endSize - RectangleRoom.WALL_WIDTH);
			
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + beginSize );
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + (beginSize + ePortal.width), xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH );
		
		}
	}
	
	public RectangleRoom buildFrom( MetaRectangleRoom metaRoom )
	{
		this.xpos = metaRoom.getBounds().x;
		this.ypos = metaRoom.getBounds().y;
		
		this.xsize = metaRoom.getBounds().width;
		this.ysize = metaRoom.getBounds().height;
		
		final MetaPortal nPortal = metaRoom.getPortal( ExitDir.N );
		final MetaPortal ePortal = metaRoom.getPortal( ExitDir.E );
		final MetaPortal sPortal = metaRoom.getPortal( ExitDir.S );
		final MetaPortal wPortal = metaRoom.getPortal( ExitDir.W );

		RectangleRoom room = new RectangleRoom(); 

		visib.startConvexArea();
		
		buildNorthWall(room, nPortal);
		buildSouthWall(room, sPortal);
		buildWestWall(room, wPortal);
		buildEastWall(room, ePortal);
		
		visib.finishConvexArea();

		// Do the small entrance areas
		if( nPortal != null )
		{
			visib.startConvexArea();

			float xsizeWithoutExit = xsize - nPortal.width;
			float sideSize = xsizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = xsize - (beginSize + nPortal.width);

			// left side up
			visib.addSegment( xpos + beginSize, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + beginSize, ypos + ysize);

			// right side up
			visib.addSegment( xpos + (beginSize + nPortal.width), ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + beginSize + nPortal.width, ypos + ysize);

			visib.finishConvexArea();
		}
		if( sPortal != null )
		{
			visib.startConvexArea();
			float xsizeWithoutExit = xsize - sPortal.width;
			float sideSize = xsizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = xsize - (beginSize + sPortal.width);
			
			// visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + beginSize, ypos + RectangleRoom.WALL_WIDTH);
			// visib.addSegment( xpos + (beginSize + sPortal.width), ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH);

			// left side up
			visib.addSegment( xpos + beginSize, ypos, xpos + beginSize, ypos + RectangleRoom.WALL_WIDTH);
			// right side up
			visib.addSegment( xpos + (beginSize + sPortal.width), ypos, xpos + beginSize + sPortal.width, ypos + RectangleRoom.WALL_WIDTH);
			visib.finishConvexArea();
		}
		if( wPortal != null )
		{
			visib.startConvexArea();
			float ysizeWithoutExit = ysize - wPortal.width;
			float sideSize = ysizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = ysizeWithoutExit - beginSize;
			
			// top side right
			visib.addSegment( xpos , ypos + (beginSize + wPortal.width), xpos + RectangleRoom.WALL_WIDTH, ypos + (beginSize + wPortal.width));
			// bottom side right
			visib.addSegment( xpos , ypos + beginSize , xpos + RectangleRoom.WALL_WIDTH, ypos + beginSize);
			visib.finishConvexArea();
		}
		if( ePortal != null )
		{
			visib.startConvexArea();
			float ysizeWithoutExit = ysize - ePortal.width;
			float sideSize = ysizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = ysize - (beginSize + ePortal.width);
			
			// top side right
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + (beginSize + ePortal.width), xpos + xsize, ypos + (beginSize + ePortal.width) );
			// bottom side right
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + beginSize, xpos + xsize, ypos + beginSize );

			visib.finishConvexArea();
		}

		room.setPosition( metaRoom.getBounds().x, metaRoom.getBounds().y);
		room.setDimensions( metaRoom.getBounds().width, metaRoom.getBounds().height );
		room.addBlocks( blocks );
		
		buildExits( room, metaRoom );
		
		room.init();
		
		return room;
	}
	
	private void buildExits(RectangleRoom room, MetaRectangleRoom metaRoom )
	{
		ObjectMap<ExitDir, MetaPortal> portals = metaRoom.getPortals();
		
		room.addConnectors( portals );
	}
	
	/*
	public RectangleRoom build()
	{
		RectangleRoom room = new RectangleRoom(); 
		
		room.setPosition( xpos, ypos);
		return room;
	} 
	 */
	

	//
	// BUILDING
	//
	private void addBlocksToRight(float xpos, float ypos, float distance )
	{

		WallBlock block = new WallBlock((int)(distance*2.0f));
		blocks.add(block);
		block.initBottomLeft( xpos, ypos , 0);

		/*
		int intDistance = (int) Math.floor( distance );
		int tens = (int) (distance / 5);
		int fives = (int) ((distance - tens * 5.0f) / 2.5f);
		int twos = (int) ((distance - tens * 5.0f - fives * 2.5f) );
		int ones = (int)  Math.ceil((distance - tens * 5.0f - fives * 2.5f - twos) );
		
		float curpos = 0;
		
		for( int i = 0; i < tens; i++ )
		{
			WallBlock a1 = new WallBlock(10);
			
			a1.initBottomLeft( xpos + curpos, ypos , 0);
			
			blocks.add( a1 );
			curpos += 5;
		}
		
		for( int i = 0; i < fives; i++ )
		{
			WallBlock a1 = new WallBlock(5);
			a1.initBottomLeft( xpos + curpos, ypos , 0);
			
			blocks.add( a1 );
			curpos += 2.5;
		}
		
		for( int i = 0; i < twos; i++ )
		{
			WallBlock a1 = new WallBlock(2);
			a1.initBottomLeft( xpos + curpos, ypos , 0);
			
			blocks.add( a1 );
			curpos += 1;
		}
		
		for( int i = 0; i < ones; i++ )
		{
			WallBlock a1 = new WallBlock(1);
			a1.initBottomLeft( xpos + curpos, ypos , 0);
			
			blocks.add( a1 );
			curpos += 0.5;
		}
		*/
		
	}
	
	//
	// BUILDING
	//
	private void addBlocksToUp(float xpos, float ypos, float distance )
	{
		WallBlock block = new WallBlock((int)(distance*2.0f));
		blocks.add(block);
		block.initTopLeft( xpos, ypos , 90);
		
	}
	
}
