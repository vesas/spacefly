package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public class MetaRoomBuilder
{
	private MetaPortal startPortal;
	
	private float width, height;
	private float xpos, ypos;
	
	private ObjectMap<ExitDir, MetaPortal> portals = new ObjectMap<ExitDir, MetaPortal>();
	
	public static MetaRoomBuilder INSTANCE = new MetaRoomBuilder();
	
	private MetaRoomBuilder() { }
	
	public MetaRectangleRoom build()
	{
		MetaRectangleRoom room = new MetaRectangleRoom();
		
		if( startPortal == null )
		{
			// Explicit placement
			room.setSize(xpos, ypos, width, height);	
		}
		else
		{
			// The startportal is a portal from another feature. The exit points away from that feature towards this new room.
			// We do Automatic placement of the room from that portal.
			ExitDir exit = startPortal.exit;
			
			if( exit.equals( ExitDir.N ))
			{
				xpos = startPortal.centerX - width / 2;
				ypos = startPortal.centerY;
			}
			if( exit.equals( ExitDir.S ))
			{
				xpos = startPortal.centerX - width / 2;
				ypos = startPortal.centerY - height;
			}
			if( exit.equals( ExitDir.E ))
			{
				xpos = startPortal.centerX;
				ypos = startPortal.centerY - height / 2;
			}
			if( exit.equals( ExitDir.W ))
			{
				xpos = startPortal.centerX - width;
				ypos = startPortal.centerY - height / 2;
			}
			
			startPortal.exit = startPortal.exit.getOpposite();
			portals.put( startPortal.exit, startPortal );
			
			room.setSize(xpos, ypos, width, height);
		}
		
		MetaPortal metaPortalE = portals.get( ExitDir.E );
		MetaPortal metaPortalW = portals.get( ExitDir.W );
		MetaPortal metaPortalN = portals.get( ExitDir.N );
		MetaPortal metaPortalS = portals.get( ExitDir.S );
		
		if( metaPortalN != null )
		{
			metaPortalN.centerX = xpos + width * 0.5f;
			metaPortalN.centerY = ypos + height;
			room.addPortal( ExitDir.N, metaPortalN );
		}
		if( metaPortalS != null )
		{
			metaPortalS.centerX = xpos + width * 0.5f;
			metaPortalS.centerY = ypos;
			room.addPortal( ExitDir.S, metaPortalS );
		}
		
		if( metaPortalE != null )
		{
			metaPortalE.centerX = xpos + width;
			metaPortalE.centerY = ypos + height * 0.5f;
			room.addPortal( ExitDir.E, metaPortalE );
		}
		
		if( metaPortalW != null )
		{
			metaPortalW.centerX = xpos;
			metaPortalW.centerY = ypos + height * 0.5f;
			room.addPortal( ExitDir.W, metaPortalW );
		}
		
		init();
		return room;
	}
	
	public void init()
	{
		 width = 0.0f;
		 height = 0.0f;
		 xpos = 0.0f;
		 ypos = 0.0f;
		 
		 portals.clear();
		 startPortal = null;
	}
	
	public MetaRoomBuilder addPortal( ExitDir dir, float width )
	{
		MetaPortal portal = new MetaPortal();
		portal.exit = dir;
		portal.width = width;
		
		portals.put( dir, portal );

		return this;
	}
	
	public MetaRoomBuilder setSize( float width, float height )
	{
		this.width = width;
		this.height = height;
		
		return this;
	}
	
	public MetaRoomBuilder setPosition( float xpos, float ypos )
	{
		this.xpos = xpos;
		this.ypos= ypos;
		
		return this;
	}

	public MetaRoomBuilder createFromPortal( MetaPortal portal )
	{
		this.startPortal = portal;
		return this;
	}
	
}
