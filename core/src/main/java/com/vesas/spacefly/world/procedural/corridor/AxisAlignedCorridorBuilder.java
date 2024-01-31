package com.vesas.spacefly.world.procedural.corridor;

import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.corridor.AxisAlignedCorridor.Dir;
import com.vesas.spacefly.world.procedural.generator.MetaCorridor;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.room.Block1;
import com.vesas.spacefly.world.procedural.room.WallBlock;
import com.vesas.spacefly.world.procedural.room.BlockUp;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public final class AxisAlignedCorridorBuilder
{
	private static float WALL_WIDTH = 0.5f;

	private float xpos, ypos;
	private float xsize, ysize;

	public static AxisAlignedCorridorBuilder INSTANCE = new AxisAlignedCorridorBuilder();
	
	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();
	
	private Visibility visib;
	
	private AxisAlignedCorridorBuilder() { }
	
	public void setVisib( Visibility visib )
	{
		this.visib = visib;
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
		
		corr.init();
		return corr;
	}
	
	public void init()
	{
	}

	private void addBlocksToRight(float xpos, float ypos, float distance )
	{
		WallBlock block = new WallBlock((int)distance*2);
		blocks.add(block);
		block.initBottomLeft( xpos, ypos , 0);
		
	}
	
	private void addBlocksToUp(float xpos, float ypos, float distance )
	{
		WallBlock block = new WallBlock((int)distance*2);
		blocks.add(block);
		block.initTopLeft( xpos, ypos , 90);

	}
}
