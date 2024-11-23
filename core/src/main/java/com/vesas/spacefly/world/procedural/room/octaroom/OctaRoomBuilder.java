package com.vesas.spacefly.world.procedural.room.octaroom;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.generator.MetaOctaRoom;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.room.WallBlock;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;
import com.vesas.spacefly.world.procedural.room.rectangleroom.FeatureBuilder;

public class OctaRoomBuilder implements FeatureBuilder<MetaOctaRoom>
{
	// bottom left position
	private float xpos, ypos;

	// xsize to the right, ysize to up
	private float xsize, ysize;

	// 
	private float apersqrttwo;

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

	private void buildNorthWall(MetaPortal portal, OctaRoom room) {

		// will the segment be at the edge of the visibility polygon?
		boolean boundary = portal == null;

		if( portal == null )
		{
			addBlocksToNorth( xpos + apersqrttwo, ypos + ysize, sidelen);

			OctaRoom.WallWedge wedge = new OctaRoom.WallWedge( xpos + apersqrttwo, ypos + ysize, 90+22.5f);
			room.addWedge(wedge);

			OctaRoom.WallWedge wedge2 = new OctaRoom.WallWedge( xpos + xsize - apersqrttwo, ypos + ysize, 90-22.5f);
			room.addWedge(wedge2);
		}

		visib.addSegment( xpos + apersqrttwo, 
						ypos + ysize, 
						xpos + xsize - apersqrttwo, 
						ypos + ysize,
						boundary);

	}
	private void buildSouthWall(MetaPortal portal, OctaRoom room) {
		
		// will the segment be at the edge of the visibility polygon?
		boolean boundary = portal == null;

		if( portal == null ) {
			addBlocksToSouth( xpos + apersqrttwo, ypos, sidelen);

			OctaRoom.WallWedge wedge = new OctaRoom.WallWedge( xpos + apersqrttwo, ypos, -90-22.5f);
			room.addWedge(wedge);

			OctaRoom.WallWedge wedge2 = new OctaRoom.WallWedge( xpos + xsize - apersqrttwo, ypos, -90+22.5f);
			room.addWedge(wedge2);
		}

		visib.addSegment( 
					xpos + xsize - apersqrttwo, 
					ypos,
					xpos + apersqrttwo, 
					ypos,
					boundary
					);
	}
	private void buildWestWall(MetaPortal portal, OctaRoom room) {

		// will the segment be at the edge of the visibility polygon?
		boolean boundary = portal == null;
		
		if( portal == null ) {

			addBlocksToLeftUp( xpos, ypos + apersqrttwo, sidelen);

			OctaRoom.WallWedge wedge = new OctaRoom.WallWedge( xpos, ypos + apersqrttwo, 180+22.5f);
			room.addWedge(wedge);

			OctaRoom.WallWedge wedge2 = new OctaRoom.WallWedge( xpos, ypos + ysize - apersqrttwo, 180-22.5f);
			room.addWedge(wedge2);
		}

		visib.addSegment( xpos, 
			ypos + apersqrttwo,
			xpos, 
			ypos + ysize - apersqrttwo,
			boundary);
		
	}
	private void buildEastWall(MetaPortal portal, OctaRoom room) {

		// will the segment be at the edge of the visibility polygon?
		boolean boundary = portal == null;

		if( portal == null ) {
			addBlocksToRightUp( xpos + xsize, ypos + apersqrttwo, sidelen);

			OctaRoom.WallWedge wedge = new OctaRoom.WallWedge( xpos + xsize, ypos + apersqrttwo, 0-22.5f);
			room.addWedge(wedge);

			OctaRoom.WallWedge wedge2 = new OctaRoom.WallWedge( xpos + xsize, ypos + ysize - apersqrttwo, 0+22.5f);
			room.addWedge(wedge2);
		}

		visib.addSegment( xpos + xsize, 
				ypos + apersqrttwo, 
				xpos + xsize, 
				ypos + ysize - apersqrttwo,
				boundary);
	}

	private void buildNorthEastWall() {

		addBlocksToNorthEast( xpos + apersqrttwo + sidelen, ypos + ysize, sidelen);
			
		visib.addSegment( 
			xpos + xsize, 
			ypos + ysize - apersqrttwo,
			xpos + xsize - apersqrttwo, 
			ypos + ysize);
	}

	private void buildNorthWestWall() {

		addBlocksToNorthWest( xpos, ypos + apersqrttwo + sidelen, sidelen);
			
		visib.addSegment( 
			xpos + apersqrttwo, 
			ypos + ysize,
			xpos, 
			ypos + apersqrttwo + sidelen);
		
	}

	private void buildSouthWestWall() {

		addBlocksToSouthWest( xpos + apersqrttwo, ypos, sidelen);

		visib.addSegment( 
			xpos, 
			ypos + apersqrttwo,
			xpos + apersqrttwo, 
			ypos
			);
		
	}

	private void buildSouthEastWall() {
		
		addBlocksToSouthEast( xpos + apersqrttwo + sidelen, ypos, sidelen);

		visib.addSegment( 
			xpos + xsize - apersqrttwo, 
			ypos,
			xpos + xsize, 
			ypos + apersqrttwo);
	}

	private int trueCount(boolean [] booleans) {
		int count = 0;
		for( boolean b : booleans ) {
			if(b) {
				count++;
			}   
    	}
		return count;
	}
	
	public OctaRoom buildFrom( MetaOctaRoom metaRoom )
	{
		this.xpos = metaRoom.getBounds().x;
		this.ypos = metaRoom.getBounds().y;
		
		this.xsize = metaRoom.getBounds().width;
		this.ysize = metaRoom.getBounds().height;

		this.sidelen = (float)(xsize / (1 + Math.sqrt(2.0)));
		this.apersqrttwo = (float)(sidelen / Math.sqrt(2.0));

		final MetaPortal nPortal = metaRoom.getPortal( ExitDir.N );
		final MetaPortal ePortal = metaRoom.getPortal( ExitDir.E );
		final MetaPortal sPortal = metaRoom.getPortal( ExitDir.S );
		final MetaPortal wPortal = metaRoom.getPortal( ExitDir.W );

		boolean [] exits = new boolean[4];
		exits[ExitDir.N.ordinal()] = nPortal != null;
		exits[ExitDir.S.ordinal()] = sPortal != null;
		exits[ExitDir.W.ordinal()] = wPortal != null;
		exits[ExitDir.E.ordinal()] = ePortal != null;

		OctaRoom room = new OctaRoom(); 

		visib.startConvexArea();
		buildSouthEastWall();
		buildEastWall(ePortal, room);
		buildNorthEastWall();
		visib.finishConvexArea();

		visib.startConvexArea();
		buildSouthWall(sPortal, room);
		buildNorthWall(nPortal, room);
		visib.finishConvexArea();
		
		visib.startConvexArea();
		buildNorthWestWall();
		buildSouthWestWall();
		buildWestWall(wPortal, room);
		visib.finishConvexArea();
		
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
	private void addBlocksToNorthEast(float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f));
		blocks.add(block);
		block.initBottomLeft( xpos, ypos , -45);
	}

	private void addBlocksToSouthEast(float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f));
		blocks.add(block);
		block.initTopLeft( xpos, ypos , 45);
	}

	private void addBlocksToNorthWest(float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f));
		blocks.add(block);
		block.initBottomLeft( xpos, ypos , 45);
	}

	private void addBlocksToSouthWest(float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f));
		blocks.add(block);
		block.initTopRight( xpos, ypos , -45);
	}

	private void addBlocksToLeftUp(float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f));
		blocks.add(block);
		block.initBottomLeft( xpos, ypos , 90);
	}

	private void addBlocksToRightUp( float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f));
		blocks.add(block);
		block.initTopLeft( xpos, ypos , 90);
	}

	private void addBlocksToSouth( float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(sidelen*2.0f));
		blocks.add(block);
		block.initTopLeft( xpos, ypos , 0);
	}
	
	private void addBlocksToNorth( float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f));
		blocks.add(block);
		block.initBottomLeft( xpos, ypos , 0);
	}
}
