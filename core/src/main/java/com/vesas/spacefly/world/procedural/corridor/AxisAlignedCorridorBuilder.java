package com.vesas.spacefly.world.procedural.corridor;

import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.generator.MetaCorridor;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.room.Block1;
import com.vesas.spacefly.world.procedural.room.BlockRight;
import com.vesas.spacefly.world.procedural.room.BlockUp;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;
import com.vesas.spacefly.visibility.Visibility;

public class AxisAlignedCorridorBuilder
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
		
		MetaPortal startPortal = metaCorr.getStartPortal();
		MetaPortal endPortal = metaCorr.getEndPortal();
		
		visib.startConvexArea();

		// Looking in to the corridor towards NORTH from south. (portals exit is towards north)
		if( startPortal.getExit().equals( ExitDir.N ))
		{
			addBlocksToUp( xpos - WALL_WIDTH, ypos, len);
			addBlocksToUp( xpos + width, ypos, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				addBlocksToRight( xpos, ypos + len - WALL_WIDTH, width);
				
				visib.addSegment( xpos , ypos, xpos , ypos + len - WALL_WIDTH );
				visib.addSegment( xpos + width , ypos, xpos + width , ypos + len - WALL_WIDTH);
				
				visib.addSegment( xpos , ypos + len - WALL_WIDTH, xpos + width ,  ypos + len - WALL_WIDTH);
			}
			else
			{
				visib.addSegment( xpos , ypos, xpos , ypos + len );
				visib.addSegment( xpos + width , ypos, xpos + width , ypos + len );
			}
		}
		
		// Looking in to the corridor towards SOUTH from north. (portals exit is towards south)
		if( startPortal.getExit().equals( ExitDir.S ))
		{
			// xpos-WALL_WIDTH because the whole wall is outside the corridor floor area
			addBlocksToUp( xpos - WALL_WIDTH, ypos, len);
			addBlocksToUp( xpos + width, ypos, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				addBlocksToRight( xpos, ypos, width);
				// left wall
				visib.addSegment( xpos , ypos + WALL_WIDTH, xpos , ypos + len );
				// right wall
				visib.addSegment( xpos + width , ypos + WALL_WIDTH, xpos + width , ypos + len );
				
				// Bottom wall. The y position is (ypos + WALL_WIDTH) because the sprite wall takes that much space at bottom
				visib.addSegment( xpos , ypos + WALL_WIDTH, xpos + width ,  ypos + WALL_WIDTH);
			}
			else
			{
				visib.addSegment( xpos , ypos, xpos , ypos + len );
				visib.addSegment( xpos + width , ypos, xpos + width , ypos + len );
			}
		}
		
		if( startPortal.getExit().equals( ExitDir.E ))
		{
			addBlocksToRight( xpos, ypos - WALL_WIDTH, len);
			addBlocksToRight( xpos, ypos + width, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				addBlocksToUp( xpos + len - WALL_WIDTH, ypos, width);
				
				visib.addSegment( xpos , ypos , xpos + len - WALL_WIDTH , ypos );
				visib.addSegment( xpos , ypos + width , xpos + len - WALL_WIDTH, ypos  + width );
				
				visib.addSegment( xpos + len - WALL_WIDTH, ypos , xpos + len - WALL_WIDTH, ypos  + width );
			}
			else
			{
				visib.addSegment( xpos, ypos , xpos + len , ypos );
				visib.addSegment( xpos, ypos  + width , xpos + len, ypos  + width );
				
			}
		}
		
		if( startPortal.getExit().equals( ExitDir.W ))
		{
			addBlocksToRight( xpos, ypos - WALL_WIDTH, len);
			addBlocksToRight( xpos, ypos + width, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				addBlocksToUp( xpos, ypos, width);
				
				visib.addSegment( xpos + WALL_WIDTH , ypos , xpos + len, ypos );
				visib.addSegment( xpos + WALL_WIDTH , ypos + width , xpos + len, ypos  + width );
				
				visib.addSegment( xpos + WALL_WIDTH , ypos , xpos  + WALL_WIDTH, ypos  + width );
			}
			else
			{
				visib.addSegment( xpos, ypos , xpos + len , ypos);
				visib.addSegment( xpos, ypos  + width , xpos + len, ypos + width );
			}
			
		}
		
		visib.finishConvexArea();
		AxisAlignedCorridor corr = new AxisAlignedCorridor();
		
		corr.setPosition( this.xpos, this.ypos);
		corr.setDimensions( this.xsize, this.ysize );
		
		corr.addBlocks( blocks );
		
		corr.init();
		return corr;
	}
	
	public void init()
	{
	}

	private void addBlocksToRight(float xpos, float ypos, float distance )
	{
		int intDistance = (int) Math.floor( distance );
		int tens = intDistance / 5;
		int fives = (int) ((intDistance - tens * 5.0f) / 2.5f);
		int twos = (int) ((intDistance - tens * 5.0f - fives * 2.5f) );
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
	
	private void addBlocksToUp(float xpos, float ypos, float distance )
	{
		int intDistance = (int) Math.floor( distance );
		int tens = intDistance / 5;
		int fives = (int) ((intDistance - tens * 5.0f) / 2.5f);
		int twos = (int) ((intDistance - tens * 5.0f - fives * 2.5f) );
		int ones = (int)  Math.ceil((distance - tens * 5.0f - fives * 2.5f - twos) );
		
		float curpos = 0;
		
		for( int i = 0; i < tens; i++ )
		{
			BlockUp a1 = new BlockUp(10);
			a1.init( xpos, ypos + curpos , 0);
			
			blocks.add( a1 );
			curpos += 5;
		}
		
		for( int i = 0; i < fives; i++ )
		{
			BlockUp a1 = new BlockUp(5);
			a1.init( xpos , ypos + curpos, 0);
			
			blocks.add( a1 );
			curpos += 2.5;
		}
		
		for( int i = 0; i < twos; i++ )
		{
			BlockUp a1 = new BlockUp(2);
			a1.init( xpos, ypos + curpos , 0);
			
			blocks.add( a1 );
			curpos += 1;
		}
		

		for( int i = 0; i < ones; i++ )
		{
			Block1 a1 = new Block1();
			a1.init( xpos, ypos  + curpos , 0);
			
			blocks.add( a1 );
			curpos += 0.5;
		}
	}
}
