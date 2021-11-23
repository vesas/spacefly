package com.vesas.spacefly.world.procedural.room.rectangleroom;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;
import com.vesas.spacefly.world.procedural.room.Block1;
import com.vesas.spacefly.world.procedural.room.BlockRight;
import com.vesas.spacefly.world.procedural.room.BlockUp;
import com.vesas.spacefly.world.procedural.room.FeatureConnector;
import com.vesas.spacefly.world.procedural.room.RoomBlock;
import com.vesas.spacefly.visibility.Visibility;

public class RectangleRoomBuilder
{
	private float xpos, ypos;
	private float xsize, ysize;
	
	private Array<RoomBlock> blocks = new Array<RoomBlock>();
	
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
	
	public RectangleRoom buildFrom( MetaRectangleRoom metaRoom )
	{
		this.xpos = metaRoom.getBounds().x;
		this.ypos = metaRoom.getBounds().y;
		
		this.xsize = metaRoom.getBounds().width;
		this.ysize = metaRoom.getBounds().height;
		
		MetaPortal nPortal = metaRoom.getPortal( ExitDir.N );
		MetaPortal ePortal = metaRoom.getPortal( ExitDir.E );
		MetaPortal sPortal = metaRoom.getPortal( ExitDir.S );
		MetaPortal wPortal = metaRoom.getPortal( ExitDir.W );
		
		visib.startRoom();
		if( nPortal == null )
		{
			// add blocks for the whole length
			addBlocksToRight( xpos, ypos + ysize - 0.5f, xsize);
			
			visib.addSegment( xpos + 0.5f, ypos + ysize - 0.5f, xpos + xsize - 0.5f, ypos + ysize - 0.5f);
		}
		else
		{
			float xsizeWithoutExit = xsize - nPortal.width;
			float sideSize = xsizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = xsize - (beginSize + nPortal.width);
			
			addBlocksToRight( xpos, ypos + ysize - 0.5f, beginSize);
			addBlocksToRight( xpos + (beginSize + nPortal.width), ypos + ysize- 0.5f, endSize);
			
			visib.addSegment( xpos + 0.5f, ypos + ysize - 0.5f, xpos + beginSize, ypos + ysize - 0.5f);
			visib.addSegment( xpos + (beginSize + nPortal.width), ypos + ysize - 0.5f, xpos + xsize - 0.5f, ypos + ysize - 0.5f);
			
		}
		
		if( sPortal == null )
		{
			// add blocks for the whole length
			addBlocksToRight( xpos, ypos, xsize);
			
			visib.addSegment( xpos + 0.5f, ypos + 0.5f, xpos + xsize - 0.5f, ypos + 0.5f);
			
		}
		else
		{
			float xsizeWithoutExit = xsize - sPortal.width;
			float sideSize = xsizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = xsize - (beginSize + sPortal.width);
			
			addBlocksToRight( xpos, ypos, beginSize);
			addBlocksToRight( xpos + (beginSize + sPortal.width), ypos, endSize);
			
			visib.addSegment( xpos + 0.5f, ypos + 0.5f, xpos + beginSize, ypos + 0.5f);
			visib.addSegment( xpos + (beginSize + sPortal.width), ypos + 0.5f, xpos + xsize - 0.5f, ypos + 0.5f);
		}
		
		if( wPortal == null )
		{
			// add blocks for the whole length
			addBlocksToUp( xpos, ypos + 0.5f,  ysize - 2 * 0.5f);
			
			visib.addSegment( xpos + 0.5f, ypos + 0.5f, xpos + 0.5f, ypos + ysize - 0.5f);
		}
		else
		{
			float ysizeWithoutExit = ysize - wPortal.width;
			float sideSize = ysizeWithoutExit / 2;
//			if( sideSize % 2 != 0 )
//				sideSize += 0.5f;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = ysizeWithoutExit - beginSize;
			
			addBlocksToUp( xpos, ypos,  beginSize);
			addBlocksToUp( xpos, ypos + (beginSize + wPortal.width), endSize);
			
			visib.addSegment( xpos + 0.5f, ypos + 0.5f, xpos + 0.5f, ypos + beginSize);
			visib.addSegment( xpos + 0.5f, ypos + (beginSize + wPortal.width), xpos + 0.5f, ypos + ysize - 0.5f);
			
		}
		
		if( ePortal == null )
		{
			// add blocks for the whole length
			addBlocksToUp( xpos + xsize - 0.5f, ypos + 0.5f,  ysize - 2 * 0.5f);
			
			visib.addSegment( xpos + xsize - 0.5f, ypos + 0.5f, xpos + xsize - 0.5f, ypos + ysize - 0.5f);
		}
		else
		{
			float ysizeWithoutExit = ysize - ePortal.width;
			float sideSize = ysizeWithoutExit / 2;
			
			float beginSize = Math.max( 1, sideSize );
			float endSize = ysize - (beginSize + ePortal.width);
			
			addBlocksToUp( xpos + xsize - 0.5f, ypos,  beginSize);
			addBlocksToUp( xpos + xsize - 0.5f, ypos+ (beginSize + ePortal.width), endSize);
			
			visib.addSegment( xpos + xsize - 0.5f, ypos + 0.5f, xpos + xsize - 0.5f, ypos + beginSize );
			visib.addSegment( xpos + xsize - 0.5f, ypos + (beginSize + ePortal.width), xpos + xsize - 0.5f, ypos + ysize - 0.5f );
		
		}
		
		visib.finishRoom();
		
		RectangleRoom room = new RectangleRoom(); 
		
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
	
	//
	// BUILDING
	//
	private void addBlocksToUp(float xpos, float ypos, float distance )
	{
		int intDistance = (int) Math.floor( distance );
		int tens = (int)(distance / 5);
		int fives = (int) ((distance - tens * 5.0f) / 2.5f);
		int twos = (int) ((distance - tens * 5.0f - fives * 2.5f) );
		int ones = (int)  Math.ceil((distance - tens * 5.0f - fives * 2.5f - twos) );
		
		float curpos = 0;
		
		for( int i = 0; i < tens; i++ )
		{
			BlockUp a1 = new BlockUp(10);
			a1.init( xpos, ypos + curpos , 0);
			
			blocks.add( a1 );
			curpos += 5.0f;
		}
		
		for( int i = 0; i < fives; i++ )
		{
			BlockUp a1 = new BlockUp(5);
			a1.init( xpos , ypos + curpos, 0);
			
			blocks.add( a1 );
			curpos += 2.5f;
		}
		
		for( int i = 0; i < twos; i++ )
		{
			BlockUp a1 = new BlockUp(2);
			a1.init( xpos, ypos + curpos , 0);
			
			blocks.add( a1 );
			curpos += 1f;
		}
		
		for( int i = 0; i < ones; i++ )
		{
			Block1 a1 = new Block1();
			a1.init( xpos, ypos + curpos , 0);
			
			blocks.add( a1 );
			curpos += 0.5f;
		}
	}
	
	//
	// BUILDING
	//
	public void init()
	{
	}
	
	//
	// BUILDING
	//
	// at this point we know size and one portal
	public void determinePosition( FeatureConnector p, ExitDir exitDir )
	{
		/*
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
		*/
	}
	
	//
	// BUILDING
	//
	public void setExitSize( int size )
	{
//		exitSize = size;
	}
	
	//
	// BUILDING
	//
	public void addExit( ExitDir dir )
	{
		/*
		Exit ex = new Exit();
		ex.exitDir = dir;
		
		Portal portal = new Portal();
		ex.portal = portal;
		
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
		*/
	}
	
	public void setSize( int xsize, int ysize )
	{
		this.xsize = xsize;
		this.ysize = ysize;
	}
	
	public void setSize( float xsize, float ysize )
	{
		this.xsize = xsize;
		this.ysize = ysize;
	}
	
}
