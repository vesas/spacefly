package com.vesas.spacefly.world.procedural.room.octaroom;

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
	private BodyBuilder bodyBuilder;
	private final FloorTheme theme;
	private final TextureRegion wallTexRegion;

	public OctaRoomBuilder(Visibility visib, BodyBuilder bodyBuilder, FloorTheme theme) {
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
	}

	private void buildNorthWall(MetaPortal portal, OctaRoom room) {

		// will the segment be at the edge of the visibility polygon?
		boolean boundary = portal == null;

		if( portal == null )
		{
			addBlocksToNorth( xpos + apersqrttwo, ypos + ysize, sidelen);

			OctaRoom.WallWedge wedge = new OctaRoom.WallWedge( xpos + apersqrttwo, ypos + ysize, 90+22.5f, theme);
			room.addWedge(wedge);

			OctaRoom.WallWedge wedge2 = new OctaRoom.WallWedge( xpos + xsize - apersqrttwo, ypos + ysize, 90-22.5f, theme);
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

			OctaRoom.WallWedge wedge = new OctaRoom.WallWedge( xpos + apersqrttwo, ypos, -90-22.5f, theme);
			room.addWedge(wedge);

			OctaRoom.WallWedge wedge2 = new OctaRoom.WallWedge( xpos + xsize - apersqrttwo, ypos, -90+22.5f, theme);
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

			OctaRoom.WallWedge wedge = new OctaRoom.WallWedge( xpos, ypos + apersqrttwo, 180+22.5f, theme);
			room.addWedge(wedge);

			OctaRoom.WallWedge wedge2 = new OctaRoom.WallWedge( xpos, ypos + ysize - apersqrttwo, 180-22.5f, theme);
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

			OctaRoom.WallWedge wedge = new OctaRoom.WallWedge( xpos + xsize, ypos + apersqrttwo, 0-22.5f, theme);
			room.addWedge(wedge);

			OctaRoom.WallWedge wedge2 = new OctaRoom.WallWedge( xpos + xsize, ypos + ysize - apersqrttwo, 0+22.5f, theme);
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
		room.setTheme(theme);
		room.setRole(metaRoom.getRole());

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

		buildPipes( room, nPortal, ePortal, sPortal, wPortal );

		buildDecor( room, nPortal, ePortal, sPortal, wPortal );

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
		WallBlock block = new WallBlock((int)(distance*2.0f), wallTexRegion);
		blocks.add(block);
		block.initBottomLeft( xpos, ypos , -45, bodyBuilder);	
	}

	private void addBlocksToSouthEast(float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f), wallTexRegion);
		blocks.add(block);
		block.initTopLeft( xpos, ypos , 45, bodyBuilder);
	}

	private void addBlocksToNorthWest(float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f), wallTexRegion);
		blocks.add(block);
		block.initBottomLeft( xpos, ypos , 45, bodyBuilder);
	}

	private void addBlocksToSouthWest(float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f), wallTexRegion);
		blocks.add(block);
		block.initTopRight( xpos, ypos , -45, bodyBuilder);
	}

	private void addBlocksToLeftUp(float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f), wallTexRegion);
		blocks.add(block);
		block.initBottomLeft( xpos, ypos , 90, bodyBuilder);
	}

	private void addBlocksToRightUp( float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f), wallTexRegion);
		blocks.add(block);
		block.initTopLeft( xpos, ypos , 90, bodyBuilder);
	}

	private void addBlocksToSouth( float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(sidelen*2.0f), wallTexRegion);
		blocks.add(block);
		block.initTopLeft( xpos, ypos , 0, bodyBuilder);
	}
	
	private void addBlocksToNorth( float xpos, float ypos, float distance ) {
		WallBlock block = new WallBlock((int)(distance*2.0f), wallTexRegion);
		blocks.add(block);
		block.initBottomLeft( xpos, ypos , 0, bodyBuilder);
	}

	// Short gapped pieces — pipes rarely span a whole wall. Ends at the wall span
	// boundary meet the diagonal wedge wall (tuck); interior ends turn 90° toward the
	// cardinal wall (elbow) or cap.
	private static final float SEG_MIN = 0.8f;
	private static final float SEG_MAX = 2.6f;
	private static final float GAP_MIN = 0.7f;
	private static final float GAP_MAX = 2.4f;
	private static final float ELBOW_CHANCE = 0.7f;
	private static final int MAX_PIECES = 3;

	private PipeSegment.End freeEnd() {
		return GenSeed.random.nextFloat() < ELBOW_CHANCE ? PipeSegment.End.ELBOW : PipeSegment.End.CAP;
	}

	private void addWallRun(OctaRoom room, boolean horizontal, float fixed, float a, float b, int wallSide) {
		float span = b - a;
		if (span < 0.5f) return;
		float pos = a;
		if (span > 3.5f && GenSeed.random.nextFloat() < 0.55f)
			pos += GenSeed.random.nextFloat() * (GAP_MIN + span * 0.12f);
		int placed = 0;
		while (pos < b - 0.3f && placed < MAX_PIECES) {
			float segLen = SEG_MIN + GenSeed.random.nextFloat() * (SEG_MAX - SEG_MIN);
			float segEnd = Math.min(pos + segLen, b);
			PipeSegment.End lo = pos    > a + 0.01f ? freeEnd() : PipeSegment.End.TUCK;
			PipeSegment.End hi = segEnd < b - 0.01f ? freeEnd() : PipeSegment.End.TUCK;
			if (horizontal)
				room.addPipeSegments(makePipeRunH(pos, segEnd, fixed, lo, hi, wallSide));
			else
				room.addPipeSegments(makePipeRunV(fixed, pos, segEnd, lo, hi, wallSide));
			placed++;
			pos = segEnd + GAP_MIN + GenSeed.random.nextFloat() * (GAP_MAX - GAP_MIN) + span * 0.15f;
		}
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

	private void buildPipes(OctaRoom room, MetaPortal nPortal, MetaPortal ePortal,
			MetaPortal sPortal, MetaPortal wPortal) {
		// Pipes only on solid (portal-less) cardinal walls; skip diagonals
		float pipeInset = PipeSegment.PIPE_DIAMETER;

		// North wall — inner face at ypos+ysize, pipe strip just inside (wall above: +1)
		if (nPortal == null && GenSeed.random.nextFloat() < 0.21f) {
			addWallRun(room, true, ypos + ysize - pipeInset, xpos + apersqrttwo, xpos + xsize - apersqrttwo, +1);
		}

		// South wall (wall below: -1)
		if (sPortal == null && GenSeed.random.nextFloat() < 0.21f) {
			addWallRun(room, true, ypos, xpos + apersqrttwo, xpos + xsize - apersqrttwo, -1);
		}

		// West wall (wall to the left: -1)
		if (wPortal == null && GenSeed.random.nextFloat() < 0.21f) {
			addWallRun(room, false, xpos, ypos + apersqrttwo, ypos + ysize - apersqrttwo, -1);
		}

		// East wall — pipe strip just inside the east face (wall to the right: +1)
		if (ePortal == null && GenSeed.random.nextFloat() < 0.21f) {
			addWallRun(room, false, xpos + xsize - pipeInset, ypos + apersqrttwo, ypos + ysize - apersqrttwo, +1);
		}
	}

	// ---- Background flair: a vent/junction on a solid cardinal wall + a centre grate ----
	private void buildDecor(OctaRoom room, MetaPortal nPortal, MetaPortal ePortal,
			MetaPortal sPortal, MetaPortal wPortal) {
		float iL = xpos, iR = xpos + xsize, iB = ypos, iT = ypos + ysize;

		Array<Integer> solid = new Array<Integer>();
		if (nPortal == null) solid.add(0);
		if (sPortal == null) solid.add(1);
		if (wPortal == null) solid.add(2);
		if (ePortal == null) solid.add(3);

		Array<Decal> out = new Array<Decal>();

		if (solid.size > 0 && GenSeed.random.nextFloat() < 0.4f) {
			int side = solid.get(GenSeed.random.nextInt(solid.size));
			float lo, hi;
			if (side <= 1) { lo = xpos + apersqrttwo + 0.5f; hi = xpos + xsize - apersqrttwo - 0.5f; }
			else           { lo = ypos + apersqrttwo + 0.5f; hi = ypos + ysize - apersqrttwo - 0.5f; }
			float along = lo >= hi ? (lo + hi) / 2f : lo + GenSeed.random.nextFloat() * (hi - lo);
			if (GenSeed.random.nextBoolean()) {
				out.add(Decal.onWall("vent", side, along, iL, iR, iB, iT, 0.5f, 0.30f, 0.82f, 0.86f, 0.90f, 1f));
				room.addVentSpawn(Decal.wallCenterX(side, along, iL, iR), Decal.wallCenterY(side, along, iB, iT));
			} else {
				float lx = Decal.wallCenterX(side, along, iL, iR);
				float ly = Decal.wallCenterY(side, along, iB, iT);
				float speed = theme == FloorTheme.REACTOR ? 4.4f : 2.2f;
				out.add(Decal.onWall("junction_box", side, along, iL, iR, iB, iT, 0.28f, 0.34f, 0.82f, 0.86f, 0.90f, 1f)
						.led(lx, ly, 0.09f, theme.accent, GenSeed.random.nextFloat() * 6.28f, speed));
			}
		}
		// floor grate near the centre
		if (GenSeed.random.nextFloat() < 0.4f) {
			out.add(new Decal("floor_grate", xpos + xsize / 2f - 0.3f, ypos + ysize / 2f - 0.3f,
					0.6f, 0.6f, 0f, 0.70f, 0.75f, 0.82f, 1f));
		}
		// hazard strips matching each doorway width, just inside the threshold
		float cx = xpos + xsize / 2f, cy = ypos + ysize / 2f;
		if (nPortal != null && GenSeed.random.nextFloat() < 0.5f)
			out.add(hazard(cx - nPortal.getWidth() / 2f, (iT - 0.30f) - 0.095f, nPortal.getWidth(), 0f));
		if (sPortal != null && GenSeed.random.nextFloat() < 0.5f)
			out.add(hazard(cx - sPortal.getWidth() / 2f, (iB + 0.30f) - 0.095f, sPortal.getWidth(), 0f));
		if (wPortal != null && GenSeed.random.nextFloat() < 0.5f)
			out.add(hazard((iL + 0.30f) - wPortal.getWidth() / 2f, cy - 0.095f, wPortal.getWidth(), 90f));
		if (ePortal != null && GenSeed.random.nextFloat() < 0.5f)
			out.add(hazard((iR - 0.30f) - ePortal.getWidth() / 2f, cy - 0.095f, ePortal.getWidth(), 90f));

		room.addDecals(out);
	}

	private Decal hazard(float x, float y, float doorWidth, float rot) {
		return new Decal("hazard_strip", x, y, doorWidth, 0.19f, rot, 1f, 1f, 1f, 0.9f);
	}
}
