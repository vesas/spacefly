package com.vesas.spacefly.world.procedural.corridor;

import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.AbstractGameWorld;
import com.vesas.spacefly.world.procedural.generator.MetaCorridor;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.room.Block1;
import com.vesas.spacefly.world.procedural.room.BlockRight;
import com.vesas.spacefly.world.procedural.room.BlockUp;
import com.vesas.spacefly.world.procedural.room.ExitDir;
import com.vesas.spacefly.world.procedural.room.FeatureConnector;
import com.vesas.spacefly.world.procedural.room.RoomBlock;

import com.vesas.spacefly.visibility.Visibility;

public class CorridorBuilder
{
	private float xpos, ypos;
	private float xsize, ysize;

	public static CorridorBuilder INSTANCE = new CorridorBuilder();
	
	private Array<RoomBlock> blocks = new Array<RoomBlock>();
	
	private AbstractGameWorld world;
	
	private Visibility visib;
	
	private CorridorBuilder() { }
	
	public void setVisib( Visibility visib )
	{
		this.visib = visib;
	}
	
	public void setWorld( AbstractGameWorld world )
	{
		this.world = world;
	}
	
	public Corridor1 buildFrom( MetaCorridor metaCorr )
	{
		this.xpos = metaCorr.getBounds().x;
		this.ypos = metaCorr.getBounds().y;
		
		this.xsize = metaCorr.getBounds().width;
		this.ysize = metaCorr.getBounds().height;
		
		float len = metaCorr.getLength();
		float width = metaCorr.getWidth();
		
		MetaPortal startPortal = metaCorr.getStartPortal();
		MetaPortal endPortal = metaCorr.getEndPortal();
		
		visib.startRoom();

		if( startPortal.exit.equals( ExitDir.N ))
		{
			addBlocksToUp( xpos - 0.5f, ypos, len);
			addBlocksToUp( xpos + width, ypos, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				addBlocksToRight( xpos, ypos, width);
				
				visib.addSegment( xpos , ypos + 0.5f, xpos , ypos + len + 0.5f );
				visib.addSegment( xpos + width , ypos + 0.5f, xpos + width , ypos + len + 0.5f);
				
				visib.addSegment( xpos , ypos + 0.5f, xpos + width ,  ypos + 0.5f);
			}
			else
			{
				visib.addSegment( xpos , ypos - 0.5f, xpos , ypos + len + 0.5f );
				visib.addSegment( xpos + width , ypos - 0.5f , xpos + width , ypos + len + 0.5f );
			}
		}
		
		if( startPortal.exit.equals( ExitDir.S ))
		{
			addBlocksToUp( xpos - 0.5f, ypos, len);
			addBlocksToUp( xpos + width, ypos, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				addBlocksToRight( xpos, ypos + len - 0.5f, width);
				
				visib.addSegment( xpos , ypos - 0.5f, xpos , ypos + len - 0.5f );
				visib.addSegment( xpos + width , ypos - 0.5f, xpos + width , ypos + len - 0.5f );
				
				visib.addSegment( xpos , ypos + len - 0.5f, xpos + width ,  ypos + len - 0.5f);
			}
			else
			{
				visib.addSegment( xpos , ypos - 0.5f , xpos , ypos + len + 0.5f );
				visib.addSegment( xpos + width , ypos - 0.5f , xpos + width , ypos + len + 0.5f );
				
			}
		}
		
		if( startPortal.exit.equals( ExitDir.E ))
		{
			addBlocksToRight( xpos, ypos - 0.5f, len);
			addBlocksToRight( xpos, ypos + width, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				addBlocksToUp( xpos, ypos, width);
				
				visib.addSegment( xpos +0.5f , ypos , xpos + len + 0.5f , ypos );
				visib.addSegment( xpos +0.5f, ypos  + width , xpos + len + 0.5f, ypos  + width );
				
				visib.addSegment( xpos + 0.5f, ypos , xpos + 0.5f, ypos  + width );
			}
			else
			{
				visib.addSegment( xpos -0.5f, ypos , xpos + len + 0.5f , ypos );
				visib.addSegment( xpos -0.5f, ypos  + width , xpos + len + 0.5f, ypos  + width );
				
			}
		}
		
		if( startPortal.exit.equals( ExitDir.W ))
		{
			addBlocksToRight( xpos, ypos - 0.5f, len);
			addBlocksToRight( xpos, ypos + width, len);
			
			// seal off the end 
			if( endPortal == null )
			{
				addBlocksToUp( xpos + len - 0.5f, ypos, width);
				
				visib.addSegment( xpos -0.5f , ypos , xpos + len -0.5f, ypos );
				visib.addSegment( xpos -0.5f, ypos  + width , xpos + len -0.5f, ypos  + width );
				
				visib.addSegment( xpos + len -0.5f , ypos , xpos  + len -0.5f, ypos  + width );
			}
			else
			{
				visib.addSegment( xpos -0.5f, ypos , xpos + len + 0.5f , ypos);
				visib.addSegment( xpos -0.5f, ypos  + width , xpos + len + 0.5f, ypos  + width );
			}
			
		}
		
		visib.finishRoom();
		Corridor1 corr = new Corridor1();
		
		corr.setPosition( this.xpos, this.ypos);
		corr.setDimensions( this.xsize, this.ysize );
		
		corr.addBlocks( blocks );
		
		corr.init();
		return corr;
	}
	
	public void init()
	{

//		float exitStartX = firstRoom.getExitStartX( firstRoomExitDir );
//		float exitStartY = firstRoom.getExitStartY( firstRoomExitDir );
		
		/*
		Portal portal = firstRoom.getPortalFromDir( firstRoomExitDir );
		
		if( portal == null )
		{
			((RectangleRoom)firstRoom).addExit( firstRoomExitDir );
			portal = firstRoom.getPortalFromDir( firstRoomExitDir );
		}
		*/
		
//		Exit exit = firstRoom.getExit( firstRoomExitDir );
		
		FeatureConnector portalStart = new FeatureConnector();
		FeatureConnector portalEnd = new FeatureConnector();
		
		/*
		portalStart.width = portal.width;
		portalStart.center.x = portal.center.x;
		portalStart.center.y = portal.center.y;
		
		// currently the end and start portals are same, ie the corridor is the same width
		portalEnd.width = portal.width;
		
		
		// currently the portal at the other end points always in the same dir as room portal
		portalEnd.dir.x = portal.dir.x;
		portalEnd.dir.y = portal.dir.y;
		
		portals.insert(0, portalStart);
		portals.insert(1, portalEnd);
		
		if( firstRoomExitDir.equals( ExitDir.N ) )
		{
			addBlocksToUp(portal.center.x - portal.width * 0.5f - 0.5f, 	portal.center.y, len);
			addBlocksToUp(portal.center.x + portal.width * 0.5f, 			portal.center.y, len);
			
			portalStart.dir.x = portal.dir.x;
			portalStart.dir.y = -portal.dir.y;
			
			portalEnd.center.x = portal.center.x;
			portalEnd.center.y = portal.center.y + len * 0.5f;
			
		}
		
		if( firstRoomExitDir.equals( ExitDir.S ) )
		{
			addBlocksToUp(portal.center.x - portal.width * 0.5f - 0.5f, portal.center.y - len * 0.5f, len);
			addBlocksToUp(portal.center.x + portal.width * 0.5f, portal.center.y - len * 0.5f, len);
			
			portalStart.dir.x = portal.dir.x;
			portalStart.dir.y = -portal.dir.y;
			
			portalEnd.center.x = portal.center.x;
			portalEnd.center.y = portal.center.y - len * 0.5f;
			
		}
		
		if( firstRoomExitDir.equals( ExitDir.E ) )
		{
			addBlocksToRight(portal.center.x, portal.center.y + portal.width * 0.5f, len);
			addBlocksToRight(portal.center.x, portal.center.y - portal.width * 0.5f - 0.5f, len);
			
			portalStart.dir.x = -portal.dir.x;
			portalStart.dir.y = portal.dir.y;
			
			portalEnd.center.x = portal.center.x + len * 0.5f;
			portalEnd.center.y = portal.center.y;
			
		}
		
		if( firstRoomExitDir.equals( ExitDir.W ) )
		{
			addBlocksToRight(portal.center.x - len * 0.5f, portal.center.y + portal.width * 0.5f, len);
			addBlocksToRight(portal.center.x - len * 0.5f, portal.center.y - portal.width * 0.5f - 0.5f, len);
			
			portalStart.dir.x = -portal.dir.x;
			portalStart.dir.y = portal.dir.y;
			
			portalEnd.center.x = portal.center.x - len * 0.5f;
			portalEnd.center.y = portal.center.y;
			
		}
		*/
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
