package com.vesas.spacefly.world.procedural.room.rectangleroom;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.FloorTheme;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.PipeSegment;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;
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
		}
	}
	private void buildNorthWall(RectangleRoom room, MetaPortal nPortal) {

		if( nPortal == null ) {
			addBlocksToRight( xpos, ypos + ysize - RectangleRoom.WALL_WIDTH, xsize);
		}
		else {
			// calculate side size in units, without the exit
			float xsizeWithoutExit = xsize - nPortal.getWidth();
			// then divide by two to get width of either side of the portal
			float sideSize = xsizeWithoutExit / 2f;
			float beginSize = Math.max( 1, sideSize );
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
			float xsizeWithoutExit = xsize - sPortal.getWidth();
			float sideSize = xsizeWithoutExit / 2f;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = xsize - (beginSize + sPortal.getWidth());

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
			float xsizeWithoutExit = xsize - sPortal.getWidth();
			float sideSize = xsizeWithoutExit / 2f;
			
			float beginSize = Math.max( 1, sideSize );
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
			float ysizeWithoutExit = ysize - wPortal.getWidth();
			float sideSize = ysizeWithoutExit / 2f;
			float beginSize = Math.max( 1, sideSize );

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
			float ysizeWithoutExit = ysize - wPortal.getWidth();
			float sideSize = ysizeWithoutExit / 2f;
			float beginSize = Math.max( 1, sideSize );
			float endSize = ysizeWithoutExit - beginSize;

			// y points down
			room.addRoomEntrance(new RoomEntrance(0, 0 + beginSize, RectangleRoom.WALL_WIDTH, wPortal.getWidth()));
			
			addBlocksToUp( xpos, ypos + RectangleRoom.WALL_WIDTH,  beginSize - RectangleRoom.WALL_WIDTH);
			addBlocksToUp( xpos, ypos + (beginSize + wPortal.getWidth()), endSize - RectangleRoom.WALL_WIDTH);
		}
	}

	private void buildEastWallVisibility(RectangleRoom room, MetaPortal ePortal) {
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

		}
	}

	private void buildEastWall(RectangleRoom room, MetaPortal ePortal) {
		if( ePortal == null ) {
			addBlocksToUp( xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH,  ysize - 2 * RectangleRoom.WALL_WIDTH);
		}
		else
		{
			float ysizeWithoutExit = ysize - ePortal.getWidth();
			float sideSize = ysizeWithoutExit / 2f;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = ysize - (beginSize + ePortal.getWidth());

			// y points down
			room.addRoomEntrance(new RoomEntrance(xsize - RectangleRoom.WALL_WIDTH, 0 + beginSize, RectangleRoom.WALL_WIDTH, ePortal.getWidth()));
			
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

			float xsizeWithoutExit = xsize - nPortal.getWidth();
			float sideSize = xsizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = xsize - (beginSize + nPortal.getWidth());

			// left side up
			visib.addSegment( xpos + beginSize, ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + beginSize, ypos + ysize);

			// right side up
			visib.addSegment( xpos + (beginSize + nPortal.getWidth()), ypos + ysize - RectangleRoom.WALL_WIDTH, xpos + beginSize + nPortal.getWidth(), ypos + ysize);

			visib.finishConvexArea();
		}
		if( sPortal != null )
		{
			visib.startConvexArea();
			float xsizeWithoutExit = xsize - sPortal.getWidth();
			float sideSize = xsizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = xsize - (beginSize + sPortal.getWidth());
			
			// visib.addSegment( xpos + RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH, xpos + beginSize, ypos + RectangleRoom.WALL_WIDTH);
			// visib.addSegment( xpos + (beginSize + sPortal.width), ypos + RectangleRoom.WALL_WIDTH, xpos + xsize - RectangleRoom.WALL_WIDTH, ypos + RectangleRoom.WALL_WIDTH);

			// left side up
			visib.addSegment( xpos + beginSize, ypos, xpos + beginSize, ypos + RectangleRoom.WALL_WIDTH);
			// right side up
			visib.addSegment( xpos + (beginSize + sPortal.getWidth()), ypos, xpos + beginSize + sPortal.getWidth(), ypos + RectangleRoom.WALL_WIDTH);
			visib.finishConvexArea();
		}
		if( wPortal != null )
		{
			visib.startConvexArea();
			float ysizeWithoutExit = ysize - wPortal.getWidth();
			float sideSize = ysizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = ysizeWithoutExit - beginSize;
			
			// top side right
			visib.addSegment( xpos , ypos + (beginSize + wPortal.getWidth()), xpos + RectangleRoom.WALL_WIDTH, ypos + (beginSize + wPortal.getWidth()));
			// bottom side right
			visib.addSegment( xpos , ypos + beginSize , xpos + RectangleRoom.WALL_WIDTH, ypos + beginSize);
			visib.finishConvexArea();
		}
		if( ePortal != null )
		{
			visib.startConvexArea();
			float ysizeWithoutExit = ysize - ePortal.getWidth();
			float sideSize = ysizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = ysize - (beginSize + ePortal.getWidth());
			
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

		room.init();

		return room;
	}
	
	private void buildExits(RectangleRoom room, MetaRectangleRoom metaRoom ) {
		ObjectMap<ExitDir, MetaPortal> portals = metaRoom.getPortals();
		
		room.addConnectors( portals );
	}
	

	private void addBlocksToRight(float xpos, float ypos, float distance) {
		WallBlock block = new WallBlock((int)(distance * 2.0f), wallTexRegion);
		blocks.add(block);
		block.initBottomLeft(xpos, ypos, 0, bodyBuilder);
	}

	private void addBlocksToUp(float xpos, float ypos, float distance) {
		WallBlock block = new WallBlock((int)(distance * 2.0f), wallTexRegion);
		blocks.add(block);
		block.initTopLeft(xpos, ypos, 90, bodyBuilder);
	}

	private Array<PipeSegment> makePipeRunH(float startX, float endX, float y) {
		Array<PipeSegment> run = new Array<PipeSegment>();
		if (endX - startX > 0.01f)
			run.add(PipeSegment.makeHorizontal(startX, y, endX - startX));
		return run;
	}

	private Array<PipeSegment> makePipeRunV(float x, float startY, float endY) {
		Array<PipeSegment> run = new Array<PipeSegment>();
		if (endY - startY > 0.01f)
			run.add(PipeSegment.makeVertical(x, startY, endY - startY));
		return run;
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

		// North wall — pipe strip just inside the north wall
		if (GenSeed.random.nextFloat() < 0.65f) {
			float pipeY = innerTop - PipeSegment.PIPE_DIAMETER;
			if (nPortal == null) {
				room.addPipeSegments(makePipeRunH(innerLeft, innerRight, pipeY));
			} else {
				float sideSize = (xsize - nPortal.getWidth()) / 2f;
				float beginSize = Math.max(1, sideSize);
				float endSize = xsize - (beginSize + nPortal.getWidth());
				room.addPipeSegments(makePipeRunH(innerLeft, xpos + beginSize, pipeY));
				room.addPipeSegments(makePipeRunH(xpos + beginSize + nPortal.getWidth(), innerRight, pipeY));
			}
		}

		// South wall
		if (GenSeed.random.nextFloat() < 0.65f) {
			float pipeY = innerBottom;
			if (sPortal == null) {
				room.addPipeSegments(makePipeRunH(innerLeft, innerRight, pipeY));
			} else {
				float sideSize = (xsize - sPortal.getWidth()) / 2f;
				float beginSize = Math.max(1, sideSize);
				room.addPipeSegments(makePipeRunH(innerLeft, xpos + beginSize, pipeY));
				room.addPipeSegments(makePipeRunH(xpos + beginSize + sPortal.getWidth(), innerRight, pipeY));
			}
		}

		// West wall
		if (GenSeed.random.nextFloat() < 0.65f) {
			float pipeX = innerLeft;
			if (wPortal == null) {
				room.addPipeSegments(makePipeRunV(pipeX, innerBottom, innerTop));
			} else {
				float sideSize = (ysize - wPortal.getWidth()) / 2f;
				float beginSize = Math.max(1, sideSize);
				room.addPipeSegments(makePipeRunV(pipeX, innerBottom, ypos + beginSize));
				room.addPipeSegments(makePipeRunV(pipeX, ypos + beginSize + wPortal.getWidth(), innerTop));
			}
		}

		// East wall — pipe strip just inside the east wall
		if (GenSeed.random.nextFloat() < 0.65f) {
			float pipeX = innerRight - PipeSegment.PIPE_DIAMETER;
			if (ePortal == null) {
				room.addPipeSegments(makePipeRunV(pipeX, innerBottom, innerTop));
			} else {
				float sideSize = (ysize - ePortal.getWidth()) / 2f;
				float beginSize = Math.max(1, sideSize);
				room.addPipeSegments(makePipeRunV(pipeX, innerBottom, ypos + beginSize));
				room.addPipeSegments(makePipeRunV(pipeX, ypos + beginSize + ePortal.getWidth(), innerTop));
			}
		}
	}

}
