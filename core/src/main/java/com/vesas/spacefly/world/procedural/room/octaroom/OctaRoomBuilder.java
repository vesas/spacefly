package com.vesas.spacefly.world.procedural.room.octaroom;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.generator.MetaOctaRoom;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;
import com.vesas.spacefly.world.procedural.room.Block1;
import com.vesas.spacefly.world.procedural.room.BlockRight;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;
import com.vesas.spacefly.world.procedural.room.rectangleroom.RectangleRoom;

public class OctaRoomBuilder
{
	// bottom left position
	private float xpos, ypos;

	// xsize to the right, ysize to up
	private float xsize, ysize;

	// the side segment length (a) https://en.wikipedia.org/wiki/Octagon
	private float sidelen;

	public float getSidelen() {
		return sidelen;
	}

	public void setSidelen(float sidelen) {
		this.sidelen = sidelen;
	}

	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();

	private Visibility visib;
	
	public static OctaRoomBuilder INSTANCE = new OctaRoomBuilder();
	
	public void setVisib( Visibility visib )
	{
		this.visib = visib;
	}
	
	public OctaRoomBuilder setPos( float xpos, float ypos )
	{
		this.xpos = xpos; 
		this.ypos = ypos;
		
		return INSTANCE;
	}

	private void buildNorthWall(OctaRoom room, MetaPortal nPortal) {
	}
	private void buildSouthWall(OctaRoom room, MetaPortal sPortal) {
	}
	private void buildWestWall(OctaRoom room, MetaPortal wPortal) {
	}
	private void buildEastWall(OctaRoom room, MetaPortal ePortal) {
	}

	private void buildNorthEastWall(OctaRoom room) {

		addBlocksToNorthEast( xpos, ypos + ysize * 0.5f + sidelen * 0.5f, sidelen);
			
		// xpos + RectangleRoom.WALL_WIDTH because: The floor starts at xpos, wall goes from xpos up to xpos + RectangleRoom.WALL_WIDTH
		visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
	}

	private void buildNorthWestWall(OctaRoom room) {
		
	}

	private void buildSouthWestWall(OctaRoom room) {
		
	}

	private void buildSouthEastWall(OctaRoom room) {
		
	}
	
	public OctaRoom buildFrom( MetaOctaRoom metaRoom )
	{
		this.xpos = metaRoom.getBounds().x;
		this.ypos = metaRoom.getBounds().y;
		
		this.xsize = metaRoom.getBounds().width;
		this.ysize = metaRoom.getBounds().height;

		this.sidelen = (float)(xsize / (1 + Math.sqrt(2)));

		final MetaPortal nPortal = metaRoom.getPortal( ExitDir.N );
		final MetaPortal ePortal = metaRoom.getPortal( ExitDir.E );
		final MetaPortal sPortal = metaRoom.getPortal( ExitDir.S );
		final MetaPortal wPortal = metaRoom.getPortal( ExitDir.W );

		OctaRoom room = new OctaRoom(); 
		visib.startConvexArea();

		buildNorthWall(room, nPortal);
		buildSouthWall(room, sPortal);
		buildWestWall(room, wPortal);
		buildEastWall(room, ePortal);
		buildNorthEastWall(room);
		buildNorthWestWall(room);
		buildSouthEastWall(room);
		buildSouthWestWall(room);

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

	private void buildExits(OctaRoom room, MetaOctaRoom metaRoom )
	{
		ObjectMap<ExitDir, MetaPortal> portals = metaRoom.getPortals();
		
		room.addConnectors( portals );
	}

	//
	// BUILDING
	//
	private void addBlocksToNorthEast(float xpos, float ypos, float distance )
	{
		int intDistance = (int) Math.floor( distance );
		int tens = (int) (distance / 5);
		int fives = (int) ((distance - tens * 5.0f) / 2.5f);
		int twos = (int) ((distance - tens * 5.0f - fives * 2.5f) );
		int ones = (int)  Math.ceil((distance - tens * 5.0f - fives * 2.5f - twos) );
		
		float curpos = 0;
		
		for( int i = 0; i < tens; i++ )
		{
			BlockRight a1 = new BlockRight(10);
			
			a1.init( xpos + curpos, ypos , 0);
			
			blocks.add( a1 );
			curpos += 5;
		}
		
		for( int i = 0; i < fives; i++ )
		{
			BlockRight a1 = new BlockRight(5);
			a1.init( xpos + curpos, ypos , 0);
			
			blocks.add( a1 );
			curpos += 2.5;
		}
		
		for( int i = 0; i < twos; i++ )
		{
			BlockRight a1 = new BlockRight(2);
			a1.init( xpos + curpos, ypos , 0);
			
			blocks.add( a1 );
			curpos += 1;
		}
		
		for( int i = 0; i < ones; i++ )
		{
			Block1 a1 = new Block1();
			a1.init( xpos + curpos, ypos , 0);
			
			blocks.add( a1 );
			curpos += 0.5;
		}
		
		
	}
}
