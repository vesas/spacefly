package com.vesas.spacefly.world.procedural.room.rectangleroom;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.procedural.Decal;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.FloorTheme;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.PipeSegment;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;
import com.vesas.spacefly.world.procedural.generator.RoomRole;
import com.vesas.spacefly.world.procedural.room.WallBlock;

public class RectangleRoomBuilder implements FeatureBuilder<MetaRectangleRoom>
{
	// these are in world units
	// bottom left position
	private float xpos, ypos;

	// These are in tile units
	// xsize to the right, ysize to up
	private float xsize, ysize;
	
	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();
	
	private Visibility visib;
	private BodyBuilder bodyBuilder;
	private final FloorTheme theme;
	private final TextureRegion wallTexRegion;

	public RectangleRoomBuilder(Visibility visib, BodyBuilder bodyBuilder, FloorTheme theme) {
		this.visib = visib;
		this.bodyBuilder = bodyBuilder;
		this.theme = theme;
		TextureRegion tex = G.getAtlas().findRegion(theme.wallTex);
		this.wallTexRegion = (tex != null) ? tex : G.walls[1];
	}
	
	public void setPos( float xpos, float ypos )
	{
		this.xpos = xpos; 
		this.ypos = ypos;

		xsize = 0;
		ysize = 0;
		blocks.clear();
		
	}

	private void buildColumn(MetaRectangleRoom metaRoom, RectangleRoom room, MetaPortal sPortal) {
		
		// make a square column in the center
		float centerX = xpos + xsize / 2.0f;
		float centerY = ypos + ysize / 2.0f;
		float centerSize = metaRoom.getHalfColumnWidth();

		addBlocksToRight( centerX - centerSize, centerY - centerSize, centerSize * 2f);
		addBlocksToRight( centerX - centerSize, centerY + centerSize - RectangleRoom.WALL_WIDTH, centerSize * 2f);
		addBlocksToUp( centerX - centerSize, centerY - centerSize + RectangleRoom.WALL_WIDTH, centerSize * 2f - RectangleRoom.WALL_WIDTH * 2.0f);
		addBlocksToUp( centerX + centerSize - RectangleRoom.WALL_WIDTH, centerY - centerSize + RectangleRoom.WALL_WIDTH, centerSize * 2f - RectangleRoom.WALL_WIDTH * 2.0f);
		
	}

	private void buildNorthColumnVisibility(MetaRectangleRoom metaRoom, RectangleRoom room, MetaPortal nPortal) {

		visib.startConvexArea();

		// column values
		float centerX = xpos + xsize / 2f;
		float centerY = ypos + ysize / 2f;
		float centerSize = metaRoom.getHalfColumnWidth();

		if( nPortal == null ) {
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
			
		}
		else {

			// calculate side size in units, without the exit
			float xsizeWithoutExit = xsize - nPortal.getWidth();
			// then divide by two to get width of either side of the portal
			float sideSize = xsizeWithoutExit / 2f;
			
			float beginSize = Math.max( 1, sideSize );

			// left side
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + beginSize, ypos + ysize - RectangleRoom.WALL_WIDTH);

			// door
			visib.addSegment( xpos + beginSize, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + (beginSize + nPortal.getWidth()), ypos + ysize - RectangleRoom.WALL_WIDTH, false);

			// right side
			visib.addSegment( xpos + (beginSize + nPortal.getWidth()), ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);

			// column left corner to door left corner
			visib.addSegment( centerX - centerSize, centerY + centerSize, xpos + beginSize, ypos + ysize - RectangleRoom.WALL_WIDTH, false);

			// column right corner to door right corner
			visib.addSegment( centerX + centerSize, centerY + centerSize, xpos + (beginSize + nPortal.getWidth()), ypos + ysize - RectangleRoom.WALL_WIDTH, false);
		}

		// column top line
		visib.addSegment( centerX - centerSize, centerY + centerSize, centerX + centerSize, centerY + centerSize);
			// left corner to corner
		visib.addSegment( centerX - centerSize, centerY + centerSize, xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH, false);
		// right corner to corner
		visib.addSegment( centerX + centerSize, centerY + centerSize, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH, false);

		visib.finishConvexArea();
	}

	private void buildWestColumnVisibility(MetaRectangleRoom metaRoom, RectangleRoom room, MetaPortal wPortal) {
		visib.startConvexArea();

		// column values
		float centerX = xpos + xsize / 2f;
		float centerY = ypos + ysize / 2f;
		float centerSize = metaRoom.getHalfColumnWidth();

		if( wPortal == null ) {
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
		}
		else {
			float ysizeWithoutExit = ysize - wPortal.getWidth();
			float sideSize = ysizeWithoutExit / 2f;
			float beginSize = Math.max( 1, sideSize );

			// bottom wall
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + RectangleRoom.WALL_WIDTH, ypos + beginSize);

			// doorway
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + beginSize, xpos + RectangleRoom.WALL_WIDTH, ypos + (beginSize + wPortal.getWidth()), false);

			// upper wall
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + (beginSize + wPortal.getWidth()), xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);

			// bottom left corner to door bottom corner
			visib.addSegment( centerX - centerSize, centerY - centerSize, xpos + RectangleRoom.WALL_WIDTH, ypos + beginSize, false);
			// top left corner to door top corner
			visib.addSegment( centerX - centerSize, centerY + centerSize, xpos + RectangleRoom.WALL_WIDTH, ypos + (beginSize + wPortal.getWidth()), false);
		}

		visib.addSegment( centerX - centerSize, centerY - centerSize, centerX - centerSize, centerY + centerSize);
		// bottom left corner to corner
		visib.addSegment( centerX - centerSize, centerY - centerSize, xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, false);
		// top left corner to corner
		visib.addSegment( centerX - centerSize, centerY + centerSize, xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH, false);

		visib.finishConvexArea();
	}

	private void buildEastColumnVisibility(MetaRectangleRoom metaRoom, RectangleRoom room, MetaPortal ePortal) {
		visib.startConvexArea();

		// column values
		float centerX = xpos + xsize / 2f;
		float centerY = ypos + ysize / 2f;
		float centerSize = metaRoom.getHalfColumnWidth();

		if( ePortal == null ) {
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
		}
		else {
			float ysizeWithoutExit = ysize - ePortal.getWidth();
			float sideSize = ysizeWithoutExit / 2f;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = ysize - (beginSize + ePortal.getWidth());

			// bottom wall
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + beginSize );

			// door
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + beginSize, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + (beginSize + ePortal.getWidth()), false );

			// upper wall
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + (beginSize + ePortal.getWidth()), xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH );

			// bottom right corner to door bottom corner
			visib.addSegment( centerX + centerSize, centerY - centerSize, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + beginSize, false);
			// top right corner to door top corner
			visib.addSegment( centerX + centerSize, centerY + centerSize, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + (beginSize + ePortal.getWidth()), false);

		}

		visib.addSegment( centerX + centerSize, centerY - centerSize, centerX + centerSize, centerY + centerSize);
		// bottom right corner to corner
		visib.addSegment( centerX + centerSize, centerY - centerSize, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, false);
		// top right corner to corner
		visib.addSegment( centerX + centerSize, centerY + centerSize, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH, false);

		visib.finishConvexArea();
	}

	private void buildSouthColumnVisibility(MetaRectangleRoom metaRoom, RectangleRoom room, MetaPortal sPortal) {

		visib.startConvexArea();

		// column values
		float centerX = xpos + xsize / 2f;
		float centerY = ypos + ysize / 2f;
		float centerSize = metaRoom.getHalfColumnWidth();

		if( sPortal == null ) {
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH);
			
		}
		else {

			// calculate side size in units, without the exit
			float xsizeWithoutExit = xsize - sPortal.getWidth();
			// then divide by two to get width of either side of the portal
			float sideSize = xsizeWithoutExit / 2f;
			
			float beginSize = Math.max( 1, sideSize );

			// left side
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + beginSize, ypos + RectangleRoom.WALL_WIDTH);

			// door
			visib.addSegment( xpos + beginSize, ypos + RectangleRoom.WALL_WIDTH, xpos + (beginSize + sPortal.getWidth()), ypos + RectangleRoom.WALL_WIDTH, false);

			// right side
			visib.addSegment( xpos + (beginSize + sPortal.getWidth()), ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH);

			// column left corner to door left corner
			visib.addSegment( centerX - centerSize, centerY - centerSize, xpos + beginSize, ypos + RectangleRoom.WALL_WIDTH, false);
			// column right corner to door right corner
			visib.addSegment( centerX + centerSize, centerY - centerSize, xpos + (beginSize + sPortal.getWidth()), ypos + RectangleRoom.WALL_WIDTH, false);
		}

		visib.addSegment( centerX - centerSize, centerY - centerSize, centerX + centerSize, centerY - centerSize);
		// left corner to corner
		visib.addSegment( centerX - centerSize, centerY - centerSize, xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, false);
		// right corner to corner
		visib.addSegment( centerX + centerSize, centerY - centerSize, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, false);

		visib.finishConvexArea();
	}

	private void buildNorthWallVisibility(RectangleRoom room, MetaPortal nPortal) {
		if( nPortal == null ) {
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
		}
		else {
			float beginSize = doorBegin( nPortal, true );

			// left side
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + beginSize, ypos + ysize - RectangleRoom.WALL_WIDTH);

			// door
			visib.addSegment( xpos + beginSize, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + (beginSize + nPortal.getWidth()), ypos + ysize - RectangleRoom.WALL_WIDTH, false);

			// right side
			visib.addSegment( xpos + (beginSize + nPortal.getWidth()), ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
		}
	}
	private void buildNorthWall(RectangleRoom room, MetaPortal nPortal) {

		if( nPortal == null ) {
			addBlocksToRight( xpos, ypos + ysize - RectangleRoom.WALL_WIDTH, xsize);
		}
		else {
			float beginSize = doorBegin( nPortal, true );
			float endSize = xsize - (beginSize + nPortal.getWidth());

			// y points down
			room.addRoomEntrance(new RoomEntrance(beginSize, 0, nPortal.getWidth(), RectangleRoom.WALL_WIDTH));

			// left side
			addBlocksToRight( xpos, ypos + ysize - RectangleRoom.WALL_WIDTH, beginSize);
			// right side
			addBlocksToRight( xpos + (beginSize + nPortal.getWidth()), ypos + ysize- RectangleRoom.WALL_WIDTH, endSize);
		}
	}

	private void buildSouthWallVisibility(RectangleRoom room, MetaPortal sPortal) {
		if( sPortal == null ) {
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH);
		}
		else {
			float beginSize = doorBegin( sPortal, true );

			// left side
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + beginSize, ypos + RectangleRoom.WALL_WIDTH);

			// doorway
			visib.addSegment( xpos + beginSize, ypos + RectangleRoom.WALL_WIDTH, xpos + (beginSize + sPortal.getWidth()), ypos + RectangleRoom.WALL_WIDTH, false);

			// right side
			visib.addSegment( xpos + (beginSize + sPortal.getWidth()), ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH);
		}
	}

	private void buildSouthWall(RectangleRoom room, MetaPortal sPortal) {
		if( sPortal == null ) {
			addBlocksToRight( xpos, ypos, xsize);
		}
		else
		{
			float beginSize = doorBegin( sPortal, true );
			float endSize = xsize - (beginSize + sPortal.getWidth());

			// y points down
			room.addRoomEntrance(new RoomEntrance(beginSize, ysize-RectangleRoom.WALL_WIDTH, sPortal.getWidth(), RectangleRoom.WALL_WIDTH));
			
			addBlocksToRight( xpos, ypos, beginSize);
			addBlocksToRight( xpos + (beginSize + sPortal.getWidth()), ypos, endSize);
		}
	}

	private void buildWestWallVisibility(RectangleRoom room, MetaPortal wPortal) {
		if( wPortal == null ) {
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
		}
		else {
			float beginSize = doorBegin( wPortal, false );

			// bottom wall
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + RectangleRoom.WALL_WIDTH, ypos + beginSize);

			// doorway
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + beginSize, xpos + RectangleRoom.WALL_WIDTH, ypos + (beginSize + wPortal.getWidth()), false);

			// upper wall
			visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + (beginSize + wPortal.getWidth()), xpos + RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
		}
	}

	private void buildWestWall(RectangleRoom room, MetaPortal wPortal) {
		if( wPortal == null ) {
			addBlocksToUp( xpos, ypos + RectangleRoom.WALL_WIDTH,  ysize - 2 * RectangleRoom.WALL_WIDTH);
		}
		else
		{
			float beginSize = doorBegin( wPortal, false );
			float endSize = ysize - (beginSize + wPortal.getWidth());

			// The floor pixmap's Y points down (world-north is pixmap-top), so the door
			// cutout's Y must be mirrored. For a centred door this equals beginSize; for an
			// off-centre (loop) door it lands the floor patch at the real doorway.
			room.addRoomEntrance(new RoomEntrance(0, ysize - beginSize - wPortal.getWidth(), RectangleRoom.WALL_WIDTH, wPortal.getWidth()));

			addBlocksToUp( xpos, ypos + RectangleRoom.WALL_WIDTH,  beginSize - RectangleRoom.WALL_WIDTH);
			addBlocksToUp( xpos, ypos + (beginSize + wPortal.getWidth()), endSize - RectangleRoom.WALL_WIDTH);
		}
	}

	private void buildEastWallVisibility(RectangleRoom room, MetaPortal ePortal) {
		if( ePortal == null ) {
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH);
		}
		else {
			float beginSize = doorBegin( ePortal, false );

			// bottom wall
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + beginSize );

			// door
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + beginSize, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + (beginSize + ePortal.getWidth()), false );

			// upper wall
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + (beginSize + ePortal.getWidth()), xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + ysize - RectangleRoom.WALL_WIDTH );

		}
	}

	private void buildEastWall(RectangleRoom room, MetaPortal ePortal) {
		if( ePortal == null ) {
			addBlocksToUp( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH,  ysize - 2 * RectangleRoom.WALL_WIDTH);
		}
		else
		{
			float beginSize = doorBegin( ePortal, false );
			float endSize = ysize - (beginSize + ePortal.getWidth());

			// The floor pixmap's Y points down (world-north is pixmap-top), so the door
			// cutout's Y must be mirrored. For a centred door this equals beginSize; for an
			// off-centre (loop) door it lands the floor patch at the real doorway.
			room.addRoomEntrance(new RoomEntrance(xsize - RectangleRoom.WALL_WIDTH, ysize - beginSize - ePortal.getWidth(), RectangleRoom.WALL_WIDTH, ePortal.getWidth()));

			addBlocksToUp( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH,  beginSize - RectangleRoom.WALL_WIDTH);
			addBlocksToUp( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos+ (beginSize + ePortal.getWidth()), endSize - RectangleRoom.WALL_WIDTH);
			
		}
	}

	/**
	 * For testing purposes, we can override this to return a mock room
	 */
	protected RectangleRoom createRoom() {
		RectangleRoom room = new RectangleRoom();
		room.setTheme(theme);
		return room;
	}
	
	@Override
    public RectangleRoom buildFrom(MetaRectangleRoom metaRoom) {
    
		this.xpos = metaRoom.getBounds().x;
		this.ypos = metaRoom.getBounds().y;
		
		this.xsize = metaRoom.getBounds().width;
		this.ysize = metaRoom.getBounds().height;
		
		final MetaPortal nPortal = metaRoom.getPortal( ExitDir.N );
		final MetaPortal ePortal = metaRoom.getPortal( ExitDir.E );
		final MetaPortal sPortal = metaRoom.getPortal( ExitDir.S );
		final MetaPortal wPortal = metaRoom.getPortal( ExitDir.W );

		RectangleRoom room = createRoom();
		room.setRole( metaRoom.getRole() );
		room.setHasColumns( metaRoom.hasColumns() );
		room.setHalfColumnWidth( metaRoom.getHalfColumnWidth() );

		buildNorthWall(room, nPortal);
		buildWestWall(room, wPortal);
		buildEastWall(room, ePortal);
		buildSouthWall(room, sPortal);

		if(!metaRoom.hasColumns()) {
			// just plain room
			visib.startConvexArea();
			buildNorthWallVisibility(room, nPortal);
			buildWestWallVisibility(room, wPortal);
			visib.finishConvexArea();

			visib.startConvexArea();
			buildEastWallVisibility(room, ePortal);
			buildSouthWallVisibility(room, sPortal);
			visib.finishConvexArea();
		}
		else {
			// columns, lets first try just one column

			buildColumn(metaRoom, room, sPortal);

			buildNorthColumnVisibility(metaRoom, room, nPortal);
			buildSouthColumnVisibility(metaRoom, room, sPortal);
			buildEastColumnVisibility(metaRoom, room, ePortal);
			buildWestColumnVisibility(metaRoom, room, wPortal);
		
		}

		// Do the small entrance areas
		if( nPortal != null )
		{
			visib.startConvexArea();

			float beginSize = doorBegin( nPortal, true );

			// left side up
			visib.addSegment( xpos + beginSize, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + beginSize, ypos + ysize);

			// right side up
			visib.addSegment( xpos + (beginSize + nPortal.getWidth()), ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + beginSize + nPortal.getWidth(), ypos + ysize);

			visib.finishConvexArea();
		}
		if( sPortal != null )
		{
			visib.startConvexArea();
			float beginSize = doorBegin( sPortal, true );

			// left side up
			visib.addSegment( xpos + beginSize, ypos, xpos + beginSize, ypos + RectangleRoom.WALL_WIDTH);
			// right side up
			visib.addSegment( xpos + (beginSize + sPortal.getWidth()), ypos, xpos + beginSize + sPortal.getWidth(), ypos + RectangleRoom.WALL_WIDTH);
			visib.finishConvexArea();
		}
		if( wPortal != null )
		{
			visib.startConvexArea();
			float beginSize = doorBegin( wPortal, false );

			// top side right
			visib.addSegment( xpos , ypos + (beginSize + wPortal.getWidth()), xpos + RectangleRoom.WALL_WIDTH, ypos + (beginSize + wPortal.getWidth()));
			// bottom side right
			visib.addSegment( xpos , ypos + beginSize , xpos + RectangleRoom.WALL_WIDTH, ypos + beginSize);
			visib.finishConvexArea();
		}
		if( ePortal != null )
		{
			visib.startConvexArea();
			float beginSize = doorBegin( ePortal, false );

			// top side right
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + (beginSize + ePortal.getWidth()), xpos + xsize, ypos + (beginSize + ePortal.getWidth()) );
			// bottom side right
			visib.addSegment( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + beginSize, xpos + xsize, ypos + beginSize );

			visib.finishConvexArea();
		}

		room.setPosition( metaRoom.getBounds().x, metaRoom.getBounds().y);
		room.setDimensions( metaRoom.getBounds().width, metaRoom.getBounds().height );
		room.addBlocks( blocks );

		buildExits( room, metaRoom );

		buildPipes( room, metaRoom );

		buildDecor( room, metaRoom );

		room.init();

		return room;
	}
	
	private void buildExits(RectangleRoom room, MetaRectangleRoom metaRoom ) {
		ObjectMap<ExitDir, MetaPortal> portals = metaRoom.getPortals();
		
		room.addConnectors( portals );
	}
	

	/**
	 * Distance from a wall's start corner (xpos for N/S walls, ypos for E/W walls) to the
	 * near jamb of its doorway. Derived from the portal's actual centre, so a door can sit
	 * anywhere along the wall — this is what lets the loop-closing pass punch an off-centre
	 * opening. For a normally placed, centred portal this equals the old
	 * max(1, (wallLength - doorWidth)/2), so existing rooms render byte-identically.
	 */
	private float doorBegin(MetaPortal portal, boolean horizontalWall) {
		float wallLen = horizontalWall ? xsize : ysize;
		float origin  = horizontalWall ? xpos  : ypos;
		float center  = horizontalWall ? portal.centerX : portal.centerY;
		float w = portal.getWidth();

		float begin = center - origin - w * 0.5f;
		float maxBegin = wallLen - w - 1f;
		if (maxBegin < 1f) maxBegin = 1f;
		if (begin < 1f) begin = 1f;
		if (begin > maxBegin) begin = maxBegin;
		return begin;
	}

	private void addBlocksToRight(float xpos, float ypos, float distance) {
		// Pass the exact half-unit length (fractional allowed). Off-centre loop doorways
		// derive their wall-segment lengths from room positions, which are not on the 0.5
		// grid, so quantising here would leave a gap next to the opening.
		WallBlock block = new WallBlock(distance * 2.0f, wallTexRegion);
		blocks.add(block);
		block.initBottomLeft(xpos, ypos, 0, bodyBuilder);
	}

	private void addBlocksToUp(float xpos, float ypos, float distance) {
		// Pass the exact half-unit length (fractional allowed). Off-centre loop doorways
		// derive their wall-segment lengths from room positions, which are not on the 0.5
		// grid, so quantising here would leave a gap next to the opening.
		WallBlock block = new WallBlock(distance * 2.0f, wallTexRegion);
		blocks.add(block);
		block.initTopLeft(xpos, ypos, 90, bodyBuilder);
	}

	// Per-wall chance a pipe run appears. Kept low so pipes read as sparse
	// background plumbing rather than a border around every room.
	private static final float PIPE_CHANCE = 0.21f;

	// Short pipe pieces with gaps between them — pipes rarely span a whole wall.
	private static final float SEG_MIN = 0.8f;
	private static final float SEG_MAX = 2.6f;
	private static final float GAP_MIN = 0.7f;
	private static final float GAP_MAX = 2.4f;
	// Free ends mostly turn 90° toward the wall (so pipes look routed); the rest cap.
	private static final float ELBOW_CHANCE = 0.7f;

	private PipeSegment.End freeEnd() {
		return GenSeed.random.nextFloat() < ELBOW_CHANCE ? PipeSegment.End.ELBOW : PipeSegment.End.CAP;
	}

	private Array<PipeSegment> makePipeRunH(float startX, float endX, float y,
			PipeSegment.End lo, PipeSegment.End hi, int wallSide) {
		Array<PipeSegment> run = new Array<PipeSegment>();
		if (endX - startX > 0.01f)
			run.add(PipeSegment.makeHorizontal(startX, y, endX - startX).ends(lo, hi, wallSide));
		return run;
	}

	private Array<PipeSegment> makePipeRunV(float x, float startY, float endY,
			PipeSegment.End lo, PipeSegment.End hi, int wallSide) {
		Array<PipeSegment> run = new Array<PipeSegment>();
		if (endY - startY > 0.01f)
			run.add(PipeSegment.makeVertical(x, startY, endY - startY).ends(lo, hi, wallSide));
		return run;
	}

	// At most this many pieces per wall, so long walls don't fill with pipe.
	private static final int MAX_PIECES = 3;

	/**
	 * Lays up to MAX_PIECES short, gapped pipe pieces along the span [a,b] of one wall.
	 * A piece touching boundary a/b takes aEnd/bEnd (TUCK at a corner, CAP at a portal
	 * jamb — never an elbow, which would poke into the opening); interior ends turn 90°
	 * toward the wall (elbow) or cap. Gaps scale with span so long walls stay sparse.
	 */
	private void addWallRun(RectangleRoom room, boolean horizontal, float fixed,
			float a, float b, PipeSegment.End aEnd, PipeSegment.End bEnd, int wallSide) {
		float span = b - a;
		if (span < 0.5f) return;
		float pos = a;
		// sometimes start with a gap so pieces don't always begin at the corner
		if (span > 3.5f && GenSeed.random.nextFloat() < 0.55f)
			pos += GenSeed.random.nextFloat() * (GAP_MIN + span * 0.12f);
		int placed = 0;
		while (pos < b - 0.3f && placed < MAX_PIECES) {
			float segLen = SEG_MIN + GenSeed.random.nextFloat() * (SEG_MAX - SEG_MIN);
			float segEnd = Math.min(pos + segLen, b);
			PipeSegment.End lo = (pos    <= a + 0.01f) ? aEnd : freeEnd();
			PipeSegment.End hi = (segEnd >= b - 0.01f) ? bEnd : freeEnd();
			if (horizontal)
				room.addPipeSegments(makePipeRunH(pos, segEnd, fixed, lo, hi, wallSide));
			else
				room.addPipeSegments(makePipeRunV(fixed, pos, segEnd, lo, hi, wallSide));
			placed++;
			pos = segEnd + GAP_MIN + GenSeed.random.nextFloat() * (GAP_MAX - GAP_MIN) + span * 0.15f;
		}
	}

	protected void buildPipes(RectangleRoom room, MetaRectangleRoom metaRoom) {
		final MetaPortal nPortal = metaRoom.getPortal(ExitDir.N);
		final MetaPortal sPortal = metaRoom.getPortal(ExitDir.S);
		final MetaPortal wPortal = metaRoom.getPortal(ExitDir.W);
		final MetaPortal ePortal = metaRoom.getPortal(ExitDir.E);

		float innerLeft  = xpos + RectangleRoom.WALL_WIDTH;
		float innerRight = xpos + xsize - RectangleRoom.WALL_WIDTH;
		float innerBottom = ypos + RectangleRoom.WALL_WIDTH;
		float innerTop    = ypos + ysize - RectangleRoom.WALL_WIDTH;

		// Each wall lays a few short gapped pieces. A piece reaching a corner tucks into
		// the side wall; a piece ending at a portal jamb caps (no elbow — an elbow would
		// turn into the doorway); interior ends turn 90° toward the wall.
		final PipeSegment.End TUCK = PipeSegment.End.TUCK;
		final PipeSegment.End CAP  = PipeSegment.End.CAP;

		// North wall (wall above the run: wallSide = +1)
		if (GenSeed.random.nextFloat() < PIPE_CHANCE) {
			float pipeY = innerTop - PipeSegment.PIPE_DIAMETER;
			if (nPortal == null) {
				addWallRun(room, true, pipeY, innerLeft, innerRight, TUCK, TUCK, +1);
			} else {
				float beginSize = doorBegin(nPortal, true);
				addWallRun(room, true, pipeY, innerLeft, xpos + beginSize, TUCK, CAP, +1);
				addWallRun(room, true, pipeY, xpos + beginSize + nPortal.getWidth(), innerRight, CAP, TUCK, +1);
			}
		}

		// South wall (wall below: wallSide = -1)
		if (GenSeed.random.nextFloat() < PIPE_CHANCE) {
			float pipeY = innerBottom;
			if (sPortal == null) {
				addWallRun(room, true, pipeY, innerLeft, innerRight, TUCK, TUCK, -1);
			} else {
				float beginSize = doorBegin(sPortal, true);
				addWallRun(room, true, pipeY, innerLeft, xpos + beginSize, TUCK, CAP, -1);
				addWallRun(room, true, pipeY, xpos + beginSize + sPortal.getWidth(), innerRight, CAP, TUCK, -1);
			}
		}

		// West wall (wall to the left: wallSide = -1)
		if (GenSeed.random.nextFloat() < PIPE_CHANCE) {
			float pipeX = innerLeft;
			if (wPortal == null) {
				addWallRun(room, false, pipeX, innerBottom, innerTop, TUCK, TUCK, -1);
			} else {
				float beginSize = doorBegin(wPortal, false);
				addWallRun(room, false, pipeX, innerBottom, ypos + beginSize, TUCK, CAP, -1);
				addWallRun(room, false, pipeX, ypos + beginSize + wPortal.getWidth(), innerTop, CAP, TUCK, -1);
			}
		}

		// East wall (wall to the right: wallSide = +1)
		if (GenSeed.random.nextFloat() < PIPE_CHANCE) {
			float pipeX = innerRight - PipeSegment.PIPE_DIAMETER;
			if (ePortal == null) {
				addWallRun(room, false, pipeX, innerBottom, innerTop, TUCK, TUCK, +1);
			} else {
				float beginSize = doorBegin(ePortal, false);
				addWallRun(room, false, pipeX, innerBottom, ypos + beginSize, TUCK, CAP, +1);
				addWallRun(room, false, pipeX, ypos + beginSize + ePortal.getWidth(), innerTop, CAP, TUCK, +1);
			}
		}
	}

	// ---- Background flair: vents, junction boxes, floor grates, hazard strips ----
	// Placement is context-aware: utility items cluster on one wall, counts scale with
	// room size, grates dodge the column and each other, hazards match doorway width.

	private Decal wallSteel(String region, int side, float along,
			float iL, float iR, float iB, float iT, float al, float dp) {
		return Decal.onWall(region, side, along, iL, iR, iB, iT, al, dp, 0.82f, 0.86f, 0.90f, 1f);
	}

	private boolean grateBlocked(Array<float[]> rects, float gx, float gy,
			float cs, float ccx, float ccy) {
		if (cs > 0f && gx + 0.6f > ccx - cs && gx < ccx + cs && gy + 0.6f > ccy - cs && gy < ccy + cs)
			return true; // overlaps the central column
		for (float[] r : rects)
			if (gx + 0.9f > r[0] && gx < r[0] + 0.9f && gy + 0.9f > r[1] && gy < r[1] + 0.9f)
				return true; // too close to another grate (0.6 size + 0.3 margin)
		return false;
	}

	private void buildDecor(RectangleRoom room, MetaRectangleRoom metaRoom) {
		float iL = xpos + RectangleRoom.WALL_WIDTH;
		float iR = xpos + xsize - RectangleRoom.WALL_WIDTH;
		float iB = ypos + RectangleRoom.WALL_WIDTH;
		float iT = ypos + ysize - RectangleRoom.WALL_WIDTH;
		float area = (iR - iL) * (iT - iB);

		final MetaPortal nP = metaRoom.getPortal(ExitDir.N);
		final MetaPortal sP = metaRoom.getPortal(ExitDir.S);
		final MetaPortal wP = metaRoom.getPortal(ExitDir.W);
		final MetaPortal eP = metaRoom.getPortal(ExitDir.E);

		// A vault reads as "secured, instrumented" with one extra grate and one extra utility
		// fitting. Only non-STANDARD rooms take this branch, so ordinary rooms are unchanged.
		final boolean vault = metaRoom.getRole() == RoomRole.VAULT;

		Array<Integer> solid = new Array<Integer>();
		if (nP == null) solid.add(0);
		if (sP == null) solid.add(1);
		if (wP == null) solid.add(2);
		if (eP == null) solid.add(3);

		Array<Decal> out = new Array<Decal>();

		// Utility wall: cluster a run of vents + junction boxes on ONE solid wall, spaced
		// so they never overlap. A bigger room reads as a hall with more gear; a small
		// room gets a single fitting. Each vent is fed by a pipe; each box gets an LED.
		if (solid.size > 0 && GenSeed.random.nextFloat() < 0.7f) {
			int wall = solid.get(GenSeed.random.nextInt(solid.size));
			boolean horiz = wall <= 1;
			float lo = (horiz ? iL : iB) + 0.7f;
			float hi = (horiz ? iR : iT) - 0.7f;
			int maxItems = (area > 34f ? 3 : area > 16f ? 2 : 1) + (vault ? 1 : 0);
			float pos = lo + GenSeed.random.nextFloat() * 0.6f;
			int placed = 0;
			while (placed < maxItems && pos < hi) {
				boolean vent = GenSeed.random.nextBoolean();
				float w = vent ? 0.5f : 0.28f;
				if (pos + w > hi) break;
				float along = pos + w / 2f;
				if (vent) {
					out.add(wallSteel("vent", wall, along, iL, iR, iB, iT, 0.5f, 0.30f));
					ventFeedPipe(room, wall, along, iL, iR, iB, iT);
					room.addVentSpawn(Decal.wallCenterX(wall, along, iL, iR),
							Decal.wallCenterY(wall, along, iB, iT));
				} else {
					float lx = Decal.wallCenterX(wall, along, iL, iR);
					float ly = Decal.wallCenterY(wall, along, iB, iT);
					out.add(wallSteel("junction_box", wall, along, iL, iR, iB, iT, 0.28f, 0.34f)
							.led(lx, ly, 0.09f, theme.accent, GenSeed.random.nextFloat() * 6.28f, ledSpeed()));
				}
				placed++;
				pos = along + w / 2f + 0.55f + GenSeed.random.nextFloat() * 0.9f;
			}
		}

		// Floor grates: count scales with floor area; each dodges the column and others.
		int grateTarget = (area > 34f ? 2 : area > 14f ? 1 : 0) + (vault ? 1 : 0);
		if (grateTarget > 0 && iR - iL > 2.4f && iT - iB > 2.4f) {
			float cs = metaRoom.hasColumns() ? metaRoom.getHalfColumnWidth() + 0.4f : 0f;
			float ccx = xpos + xsize / 2f, ccy = ypos + ysize / 2f;
			Array<float[]> rects = new Array<float[]>();
			for (int gi = 0; gi < grateTarget; gi++) {
				if (GenSeed.random.nextFloat() >= 0.55f) continue; // not every slot fills
				for (int tries = 0; tries < 8; tries++) {
					float gx = iL + 0.9f + GenSeed.random.nextFloat() * (iR - iL - 2.4f);
					float gy = iB + 0.9f + GenSeed.random.nextFloat() * (iT - iB - 2.4f);
					if (grateBlocked(rects, gx, gy, cs, ccx, ccy)) continue;
					out.add(new Decal("floor_grate", gx, gy, 0.6f, 0.6f, 0f, 0.70f, 0.75f, 0.82f, 1f));
					rects.add(new float[] { gx, gy });
					break;
				}
			}
		}
		// hazard strip spanning the actual doorway width, just inside the threshold
		if (nP != null && GenSeed.random.nextFloat() < 0.5f)
			out.add(hazard(nP.centerX - nP.getWidth() / 2f, (iT - 0.30f) - 0.095f, nP.getWidth(), 0f));
		if (sP != null && GenSeed.random.nextFloat() < 0.5f)
			out.add(hazard(sP.centerX - sP.getWidth() / 2f, (iB + 0.30f) - 0.095f, sP.getWidth(), 0f));
		if (wP != null && GenSeed.random.nextFloat() < 0.5f)
			out.add(hazard((iL + 0.30f) - wP.getWidth() / 2f, wP.centerY - 0.095f, wP.getWidth(), 90f));
		if (eP != null && GenSeed.random.nextFloat() < 0.5f)
			out.add(hazard((iR - 0.30f) - eP.getWidth() / 2f, eP.centerY - 0.095f, eP.getWidth(), 90f));

		room.addDecals(out);
	}

	private Decal hazard(float x, float y, float doorWidth, float rot) {
		return new Decal("hazard_strip", x, y, doorWidth, 0.19f, rot, 1f, 1f, 1f, 0.9f);
	}

	private float ledSpeed() {
		return theme == FloorTheme.REACTOR ? 4.4f : 2.2f;
	}

	// Runs a short pipe leg up to a wall vent, ending in an elbow that turns into it,
	// so the vent looks like it is fed by the plumbing rather than standing alone.
	private void ventFeedPipe(RectangleRoom room, int side, float along,
			float iL, float iR, float iB, float iT) {
		final float leg = 1.3f;
		final PipeSegment.End CAP = PipeSegment.End.CAP, ELB = PipeSegment.End.ELBOW;
		switch (side) {
			case 0: { // N wall — horizontal pipe just below the top wall
				float y = iT - PipeSegment.PIPE_DIAMETER;
				if (along - leg >= iL + 0.3f) room.addPipeSegments(makePipeRunH(along - leg, along, y, CAP, ELB, +1));
				else                          room.addPipeSegments(makePipeRunH(along, along + leg, y, ELB, CAP, +1));
				break;
			}
			case 1: { // S wall
				float y = iB;
				if (along - leg >= iL + 0.3f) room.addPipeSegments(makePipeRunH(along - leg, along, y, CAP, ELB, -1));
				else                          room.addPipeSegments(makePipeRunH(along, along + leg, y, ELB, CAP, -1));
				break;
			}
			case 2: { // W wall — vertical pipe
				float x = iL;
				if (along - leg >= iB + 0.3f) room.addPipeSegments(makePipeRunV(x, along - leg, along, CAP, ELB, -1));
				else                          room.addPipeSegments(makePipeRunV(x, along, along + leg, ELB, CAP, -1));
				break;
			}
			default: { // E wall
				float x = iR - PipeSegment.PIPE_DIAMETER;
				if (along - leg >= iB + 0.3f) room.addPipeSegments(makePipeRunV(x, along - leg, along, CAP, ELB, +1));
				else                          room.addPipeSegments(makePipeRunV(x, along, along + leg, ELB, CAP, +1));
				break;
			}
		}
	}

}
