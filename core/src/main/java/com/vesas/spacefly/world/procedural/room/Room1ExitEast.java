package com.vesas.spacefly.world.procedural.room;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.game.Screen;

public class Room1ExitEast extends Room
{
	private int xsize;
	private int ysize;
	
	private int exitSize = 4;
	
	private Array<RoomBlock> blocks = new Array<RoomBlock>();
	
	private void addBlocksToRight(float xpos, float ypos, int distance )
	{
		int tens = distance / 10;
		int fives = (distance - tens * 10) / 5;
		int twos = (distance - tens *10 - fives * 5) / 2;
		
		int curpos = 0;
		
		for( int i = 0; i < tens; i++ )
		{
			Block10Right a1 = new Block10Right();
			
			a1.init( xpos + curpos * 0.5f, ypos , 0);
			
			blocks.add( a1 );
			curpos += 10;
		}
		
		for( int i = 0; i < fives; i++ )
		{
			Block5Right a1 = new Block5Right();
			a1.init( xpos + 0.5f * curpos, ypos , 0);
			
			blocks.add( a1 );
			curpos += 5;
		}
		
		for( int i = 0; i < twos; i++ )
		{
			Block2Right a1 = new Block2Right();
			a1.init( xpos + 0.5f * curpos, ypos , 0);
			
			blocks.add( a1 );
			curpos += 2;
		}
	}
	
	private void addBlocksToUp(float xpos, float ypos, int distance )
	{
		int tens = distance / 10;
		int fives = (distance - tens * 10) / 5;
		int twos = (distance - tens *10 - fives * 5) / 2;
		
		int curpos = 0;
		
		for( int i = 0; i < tens; i++ )
		{
			Block10Up a1 = new Block10Up();
			a1.init( xpos, ypos + curpos * 0.5f , 0);
			
			blocks.add( a1 );
			curpos += 10;
		}
		
		for( int i = 0; i < fives; i++ )
		{
			Block5Up a1 = new Block5Up();
			a1.init( xpos , ypos + 0.5f * curpos, 0);
			
			blocks.add( a1 );
			curpos += 5;
		}
		
		for( int i = 0; i < twos; i++ )
		{
			Block2Up a1 = new Block2Up();
			a1.init( xpos, ypos + 0.5f * curpos , 0);
			
			blocks.add( a1 );
			curpos += 2;
		}
	}
	
	public void init()
	{
		if( !exitsContain( ExitDir.N ))
		{
			// add blocks for the whole length
			addBlocksToRight( xpos, ypos + ysize * 0.5f - 0.5f, xsize);
		}
		else
		{
			int xsizeWithoutExit = xsize - exitSize;
			int sideSize = xsizeWithoutExit / 2;
			
			int beginSize = Math.max( 2, sideSize );
			int endSize = xsize - (beginSize + exitSize);
			
			addBlocksToRight( xpos, ypos + ysize * 0.5f - 0.5f, beginSize);
			addBlocksToRight( xpos + (beginSize + exitSize)*0.5f, ypos + ysize * 0.5f - 0.5f, endSize);
			
		}
		
		if( !exitsContain( ExitDir.S ))
		{
			// add blocks for the whole length
			addBlocksToRight( xpos, ypos, xsize);
			
		}
		else
		{
			int xsizeWithoutExit = xsize - exitSize;
			int sideSize = xsizeWithoutExit / 2;
			
			int beginSize = Math.max( 2, sideSize );
			int endSize = xsize - (beginSize + exitSize);
			
			addBlocksToRight( xpos, ypos, beginSize);
			addBlocksToRight( xpos + (beginSize + exitSize)*0.5f, ypos, endSize);
		}
		
		if( !exitsContain( ExitDir.W ))
		{
			// add blocks for the whole length
			addBlocksToUp( xpos, ypos + 0.5f,  ysize - 2);
		
		}
		else
		{
			int ysizeWithoutExit = ysize - exitSize - 2;
			int sideSize = ysizeWithoutExit / 2;
			
			int beginSize = Math.max( 2, sideSize );
			int endSize = ysizeWithoutExit - beginSize;
			
			addBlocksToUp( xpos, ypos + 0.5f,  beginSize);
			addBlocksToUp( xpos, ypos + 0.5f + (beginSize + exitSize)*0.5f, endSize);
		}
		
		if( !exitsContain( ExitDir.E ))
		{
			// add blocks for the whole length
			addBlocksToUp( xpos + xsize * 0.5f - 0.5f, ypos + 0.5f,  ysize - 2);
		}
		else
		{
			int ysizeWithoutExit = ysize - exitSize - 2;
			int sideSize = ysizeWithoutExit / 2;
			
			int beginSize = Math.max( 2, sideSize );
			int endSize = ysizeWithoutExit - beginSize;
			
			addBlocksToUp( xpos + xsize * 0.5f - 0.5f, ypos + 0.5f,  beginSize);
			addBlocksToUp( xpos + xsize * 0.5f - 0.5f, ypos + 0.5f + (beginSize + exitSize)*0.5f, endSize);
		}
		
		
	}
	
	// at this point we know size and one portal
	public void determinePosition( Portal p, ExitDir exitDir )
	{
		Vector2 portalCenter = p.center;
		
		if( exitDir.equals( ExitDir.S ) )
		{
			this.xpos = portalCenter.x - this.xsize * 0.5f * 0.5f;
			this.ypos = portalCenter.y;
		}
		if( exitDir.equals( ExitDir.N ) )
		{
			this.xpos = portalCenter.x - this.xsize * 0.5f * 0.5f;
			this.ypos = portalCenter.y - this.xsize * 0.5f;
		}
		if( exitDir.equals( ExitDir.E ) )
		{
			this.xpos = portalCenter.x - this.xsize * 0.5f;
			this.ypos = portalCenter.y - this.xsize * 0.5f * 0.5f;
		}
		if( exitDir.equals( ExitDir.W ) )
		{
			this.xpos = portalCenter.x;
			this.ypos = portalCenter.y - this.xsize * 0.5f * 0.5f;
		}
	}
	
	public void setExitSize( int size )
	{
		exitSize = size;
	}
	
	public void addExit( ExitDir dir )
	{
		Exit ex = new Exit();
		ex.exitDir = dir;
		
		Portal portal = new Portal();
//		ex.portal = portal;
		
		if( dir.equals( ExitDir.N ) || 
			dir.equals( ExitDir.S ) )
		{

			int xsizeWithoutExit = xsize - exitSize;
			int sideSize = xsizeWithoutExit / 2;
			
			int beginSize = Math.max( 2, sideSize );
			int endSize = xsize - (beginSize + exitSize);
			
			ex.exitStart = beginSize;
			ex.exitWidth = exitSize;
			
			if( dir.equals( ExitDir.S ) )
            {
            	ex.exitX = this.xpos + ex.exitStart * 0.5f;
            	ex.exitY = this.ypos;
            	
            	portal.dir.x = 0;
            	portal.dir.y = -1.0f;
            	
            	portal.center.x = ex.exitX + exitSize * 0.5f * 0.5f; 
            	portal.center.y = this.ypos;
            	
            	portal.width = exitSize * 0.5f;
            	
            }
            else
            {
            	ex.exitX = this.xpos + ex.exitStart * 0.5f;
            	ex.exitY = this.ypos + this.ysize * 0.5f - 0.5f;
            	
            	portal.dir.x = 0;
            	portal.dir.y = 1.0f;
            	
            	portal.center.x = ex.exitX + exitSize * 0.5f * 0.5f; 
            	portal.center.y = this.ypos + this.ysize * 0.5f;
            	
            	portal.width = exitSize * 0.5f;
            }
		}
		
		if( dir.equals( ExitDir.W ) || 
				dir.equals( ExitDir.E ) )
		{

			int ysizeWithoutExit = ysize - exitSize - 2;
			int sideSize = ysizeWithoutExit / 2;
			
			int beginSize = Math.max( 2, sideSize );
			int endSize = ysizeWithoutExit - beginSize;
			
			ex.exitStart = beginSize;
			ex.exitWidth = exitSize;
			
			if( dir.equals( ExitDir.E ) )
			{
				ex.exitX = this.xpos + this.xsize * 0.5f - 0.5f;
				ex.exitY = this.ypos + ex.exitStart * 0.5f + 0.5f;
				
				portal.dir.x = 1.0f;
            	portal.dir.y = 0.0f;
            	
            	portal.center.x = this.xpos + this.xsize * 0.5f; 
            	portal.center.y = ex.exitY + exitSize * 0.5f * 0.5f; ;
            	
            	portal.width = exitSize * 0.5f;
			}
			else
			{
				ex.exitX = this.xpos;
				ex.exitY = this.ypos + ex.exitStart * 0.5f;
				
				portal.dir.x = -1.0f;
            	portal.dir.y = 0.0f;
            	
            	portal.center.x = this.xpos; 
            	portal.center.y = ex.exitY + exitSize * 0.5f * 0.5f;
            	
            	portal.width = exitSize * 0.5f;
			}
			
		}
			
		exits.add( ex );
	}
	
	public void setSize( int xsize, int ysize )
	{
		this.xsize = xsize;
		this.ysize = ysize;
	}
	
	public void draw(Screen screen)
	{
		for( int i = 0; i < blocks.size; i++ )
		{
			RoomBlock block = blocks.get( i );
			
			block.draw( screen );
		}
	}

	public void tick(Screen screen, float delta)
	{
	}

}


