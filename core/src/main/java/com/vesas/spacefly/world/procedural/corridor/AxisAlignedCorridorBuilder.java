package com.vesas.spacefly.world.procedural.corridor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.procedural.Decal;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.FloorTheme;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.PipeSegment;
import com.vesas.spacefly.world.procedural.corridor.AxisAlignedCorridor.Dir;
import com.vesas.spacefly.world.procedural.generator.MetaCorridor;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.room.WallBlock;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;
import com.vesas.spacefly.world.procedural.room.rectangleroom.FeatureBuilder;

public final class AxisAlignedCorridorBuilder implements FeatureBuilder<MetaCorridor>
{
	private static float WALL_WIDTH = 0.5f;

	private float xpos, ypos;
	private float xsize, ysize;
	
	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();
	
	private Visibility visib;
	private BodyBuilder bodyBuilder;
	private final FloorTheme theme;
	private final TextureRegion wallTexRegion;

	public AxisAlignedCorridorBuilder(Visibility visib, BodyBuilder bodyBuilder, FloorTheme theme) {
		this.visib = visib;
		this.bodyBuilder = bodyBuilder;
		this.theme = theme;
		TextureRegion tex = G.getAtlas().findRegion(theme.wallTex);
		this.wallTexRegion = (tex != null) ? tex : G.walls[1];
	}
	
	public AxisAlignedCorridor buildFrom( MetaCorridor metaCorr )
	{
		this.xpos = metaCorr.getBounds().x;
		this.ypos = metaCorr.getBounds().y;
		
		this.xsize = metaCorr.getBounds().width;
		this.ysize = metaCorr.getBounds().height;
		
		float len = metaCorr.getLength();
		float width = metaCorr.getWidth();

		float floorxpos = xpos;
		float floorypos = ypos;

		float floorwidth = 0;
		float floorheight = 0;
		
		MetaPortal startPortal = metaCorr.getStartPortal();
		MetaPortal endPortal = metaCorr.getEndPortal();
		
		AxisAlignedCorridor corr = new AxisAlignedCorridor();
		corr.setTheme(theme);

		visib.startConvexArea();

		// Looking in to the corridor towards NORTH from south. (portals exit is towards north)
		if( startPortal.getExit().equals( ExitDir.N ))
		{
			floorxpos = xpos + WALL_WIDTH;
			floorypos = ypos;
			floorwidth = width;
			floorheight = len;

			corr.dir = Dir.SN;
			
			// west wall
			addBlocksToUp( xpos, ypos, len);
			// east wall
			addBlocksToUp( xpos + width + WALL_WIDTH, ypos, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				floorheight -= WALL_WIDTH;

				// north wall
				addBlocksToRight( xpos + WALL_WIDTH, ypos + len - WALL_WIDTH, width);
				
				// west visib segment
				visib.addSegment( xpos + WALL_WIDTH, ypos, xpos + WALL_WIDTH, ypos + len - WALL_WIDTH );
				// east visib segment
				visib.addSegment( xpos + WALL_WIDTH + width , ypos, xpos + WALL_WIDTH + width , ypos + len - WALL_WIDTH);
				// north visib segment
				visib.addSegment( xpos + WALL_WIDTH, ypos + len - WALL_WIDTH, xpos + WALL_WIDTH + width ,  ypos + len - WALL_WIDTH);
				this.ysize -= WALL_WIDTH;
			}
			else
			{
				// west visib segment
				visib.addSegment( xpos + WALL_WIDTH, ypos, xpos + WALL_WIDTH, ypos + len );
				// east visib segment
				visib.addSegment( xpos + WALL_WIDTH + width , ypos, xpos + WALL_WIDTH + width , ypos + len );
			}
		}
		
		// Looking in to the corridor towards SOUTH from north. (portals exit is towards south)
		if( startPortal.getExit().equals( ExitDir.S ))
		{
			floorxpos = xpos + WALL_WIDTH;
			floorypos = ypos;
			floorwidth = width;
			floorheight = len;

			corr.dir = Dir.SN;

			// west wall
			addBlocksToUp( xpos, ypos, len);

			// east wall
			addBlocksToUp( xpos + width + WALL_WIDTH, ypos, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				floorheight -= WALL_WIDTH;
				floorypos += WALL_WIDTH;

				// south wall
				addBlocksToRight( xpos + WALL_WIDTH, ypos, width);

				// west visib segment
				visib.addSegment( xpos + WALL_WIDTH, ypos + WALL_WIDTH, xpos + WALL_WIDTH, ypos + len );
				// east visib segment
				visib.addSegment( xpos + WALL_WIDTH + width , ypos + WALL_WIDTH, xpos + WALL_WIDTH + width , ypos + len );
				
				// Bottom wall. 
				visib.addSegment( xpos + WALL_WIDTH, ypos + WALL_WIDTH, xpos + WALL_WIDTH + width ,  ypos + WALL_WIDTH);

				this.ysize -= WALL_WIDTH;
				this.ypos += WALL_WIDTH;
			}
			else
			{
				visib.addSegment( xpos + WALL_WIDTH, ypos, xpos + WALL_WIDTH , ypos + len );
				visib.addSegment( xpos + WALL_WIDTH + width , ypos, xpos + WALL_WIDTH + width , ypos + len );
			}
		}
		
		if( startPortal.getExit().equals( ExitDir.E ))
		{
			floorxpos = xpos;
			floorypos = ypos + WALL_WIDTH;
			floorwidth = len;
			floorheight = width;

			corr.dir = Dir.WE;

			addBlocksToRight( xpos, ypos, len);
			addBlocksToRight( xpos, ypos + WALL_WIDTH + width, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				floorwidth -= WALL_WIDTH;
				
				addBlocksToUp( xpos + len - WALL_WIDTH, ypos + WALL_WIDTH, width);
				
				visib.addSegment( xpos , ypos + WALL_WIDTH				, xpos + len - WALL_WIDTH , ypos + WALL_WIDTH );
				visib.addSegment( xpos , ypos + WALL_WIDTH + width 		, xpos + len - WALL_WIDTH, ypos + WALL_WIDTH + width );
				
				visib.addSegment( xpos + len - WALL_WIDTH, ypos + WALL_WIDTH 		, xpos + len - WALL_WIDTH, ypos + WALL_WIDTH + width );

				this.xsize -= WALL_WIDTH;
			}
			else
			{
				visib.addSegment( xpos, ypos + WALL_WIDTH, xpos + len , ypos + WALL_WIDTH);
				visib.addSegment( xpos, ypos + WALL_WIDTH + width , xpos + len, ypos + WALL_WIDTH + width );
				
			}
		}
		
		if( startPortal.getExit().equals( ExitDir.W ))
		{
			floorxpos = xpos;
			floorypos = ypos + WALL_WIDTH;
			floorwidth = len;
			floorheight = width;

			corr.dir = Dir.WE;
			
			addBlocksToRight( xpos, ypos, len);
			addBlocksToRight( xpos, ypos + WALL_WIDTH + width, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				floorwidth -= WALL_WIDTH;
				floorxpos += WALL_WIDTH;

				addBlocksToUp( xpos, ypos + WALL_WIDTH, width);
				
				visib.addSegment( xpos + WALL_WIDTH , ypos + WALL_WIDTH , xpos + len, ypos + WALL_WIDTH );
				visib.addSegment( xpos + WALL_WIDTH , ypos + WALL_WIDTH + width , xpos + len, ypos + WALL_WIDTH + width );
				
				visib.addSegment( xpos + WALL_WIDTH , ypos + WALL_WIDTH, xpos  + WALL_WIDTH, ypos + WALL_WIDTH + width );

			}
			else
			{
				visib.addSegment( xpos, ypos + WALL_WIDTH , xpos + len , ypos + WALL_WIDTH );
				visib.addSegment( xpos, ypos + WALL_WIDTH + width , xpos + len, ypos + WALL_WIDTH + width );
			}
			
		}
		
		visib.finishConvexArea();
		
		corr.setPosition( floorxpos, floorypos);
		corr.setDimensions( floorwidth, floorheight );

		corr.addBlocks( blocks );

		buildPipes( corr, startPortal );

		buildDecor( corr, startPortal );

		corr.init();
		return corr;
	}
	
	public void init()
	{
	}

	private void addBlocksToRight(float xpos, float ypos, float distance) {
		// Pass the exact half-unit length (fractional allowed). A loop corridor's length is
		// derived from room positions (not grid-aligned), so quantising here would leave the
		// wall short of the far room and open a gap at the junction.
		WallBlock block = new WallBlock(distance * 2f, wallTexRegion);
		blocks.add(block);
		block.initBottomLeft(xpos, ypos, 0, bodyBuilder);
	}

	private void addBlocksToUp(float xpos, float ypos, float distance) {
		// Pass the exact half-unit length (fractional allowed). A loop corridor's length is
		// derived from room positions (not grid-aligned), so quantising here would leave the
		// wall short of the far room and open a gap at the junction.
		WallBlock block = new WallBlock(distance * 2f, wallTexRegion);
		blocks.add(block);
		block.initTopLeft(xpos, ypos, 90, bodyBuilder);
	}

	// Each corridor wall rolls independently; runs are short gapped pieces, not the
	// whole corridor length. Free ends turn 90° into the side wall (elbow) or cap.
	private static final float PIPE_CHANCE = 0.35f;
	private static final float SEG_MIN = 0.8f;
	private static final float SEG_MAX = 2.6f;
	private static final float GAP_MIN = 0.7f;
	private static final float GAP_MAX = 2.4f;
	private static final float ELBOW_CHANCE = 0.7f;

	private PipeSegment.End freeEnd() {
		return GenSeed.random.nextFloat() < ELBOW_CHANCE ? PipeSegment.End.ELBOW : PipeSegment.End.CAP;
	}

	// A long corridor wall gets only a couple of pieces, not a chain of them.
	private static final int MAX_PIECES = 2;

	/**
	 * Short, gapped pieces along [a,b]. The two ends of the corridor are mouths that open
	 * onto rooms, so pieces touching them cap (an elbow would poke into the opening);
	 * interior ends turn 90° into the side wall. Gaps scale with span to stay sparse.
	 */
	private void addCorridorRun(AxisAlignedCorridor corr, boolean horizontal, float fixed, float a, float b, int wallSide) {
		float span = b - a;
		if (span < 0.5f) return;
		float pos = a;
		if (span > 3.5f && GenSeed.random.nextFloat() < 0.55f)
			pos += GenSeed.random.nextFloat() * (GAP_MIN + span * 0.12f);
		int placed = 0;
		while (pos < b - 0.3f && placed < MAX_PIECES) {
			float segLen = SEG_MIN + GenSeed.random.nextFloat() * (SEG_MAX - SEG_MIN);
			float segEnd = Math.min(pos + segLen, b);
			PipeSegment.End lo = (pos    <= a + 0.01f) ? PipeSegment.End.CAP : freeEnd();
			PipeSegment.End hi = (segEnd >= b - 0.01f) ? PipeSegment.End.CAP : freeEnd();
			if (horizontal)
				corr.addPipeSegments(makePipeRunH(pos, segEnd, fixed, lo, hi, wallSide));
			else
				corr.addPipeSegments(makePipeRunV(fixed, pos, segEnd, lo, hi, wallSide));
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

	private void buildPipes(AxisAlignedCorridor corr, MetaPortal startPortal) {
		if (startPortal.getExit().equals(ExitDir.N) || startPortal.getExit().equals(ExitDir.S)) {
			// SN corridor: pipes along west (-1) and east (+1) walls, each independent
			if (GenSeed.random.nextFloat() < PIPE_CHANCE)
				addCorridorRun(corr, false, xpos + WALL_WIDTH,                                     ypos, ypos + ysize, -1);
			if (GenSeed.random.nextFloat() < PIPE_CHANCE)
				addCorridorRun(corr, false, xpos + xsize - WALL_WIDTH - PipeSegment.PIPE_DIAMETER, ypos, ypos + ysize, +1);
		} else {
			// WE corridor: pipes along south (-1) and north (+1) walls, each independent
			if (GenSeed.random.nextFloat() < PIPE_CHANCE)
				addCorridorRun(corr, true, ypos + WALL_WIDTH,                                     xpos, xpos + xsize, -1);
			if (GenSeed.random.nextFloat() < PIPE_CHANCE)
				addCorridorRun(corr, true, ypos + ysize - WALL_WIDTH - PipeSegment.PIPE_DIAMETER, xpos, xpos + xsize, +1);
		}
	}

	// One in a while, a single vent or LED junction box on a corridor wall.
	private void buildDecor(AxisAlignedCorridor corr, MetaPortal startPortal) {
		if (GenSeed.random.nextFloat() >= 0.3f) return;
		boolean sn = startPortal.getExit().equals(ExitDir.N) || startPortal.getExit().equals(ExitDir.S);
		float iL = xpos + WALL_WIDTH, iR = xpos + xsize - WALL_WIDTH;
		float iB = ypos + WALL_WIDTH, iT = ypos + ysize - WALL_WIDTH;
		int side;
		float lo, hi;
		if (sn) { side = GenSeed.random.nextBoolean() ? 2 : 3; lo = ypos + 1f; hi = ypos + ysize - 1f; }
		else    { side = GenSeed.random.nextBoolean() ? 0 : 1; lo = xpos + 1f; hi = xpos + xsize - 1f; }
		float along = lo >= hi ? (lo + hi) / 2f : lo + GenSeed.random.nextFloat() * (hi - lo);

		Array<Decal> out = new Array<Decal>();
		if (GenSeed.random.nextBoolean()) {
			out.add(Decal.onWall("vent", side, along, iL, iR, iB, iT, 0.5f, 0.30f, 0.82f, 0.86f, 0.90f, 1f));
		} else {
			float lx = Decal.wallCenterX(side, along, iL, iR);
			float ly = Decal.wallCenterY(side, along, iB, iT);
			float speed = theme == FloorTheme.REACTOR ? 4.4f : 2.2f;
			out.add(Decal.onWall("junction_box", side, along, iL, iR, iB, iT, 0.28f, 0.34f, 0.82f, 0.86f, 0.90f, 1f)
					.led(lx, ly, 0.09f, theme.accent, GenSeed.random.nextFloat() * 6.28f, speed));
		}
		corr.addDecals(out);
	}
}
