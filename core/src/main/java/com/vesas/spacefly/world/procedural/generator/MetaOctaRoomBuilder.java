package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public final class MetaOctaRoomBuilder
{
	private MetaPortal startPortal;
	
	private float radius;
	private float width, height;
	private float xpos, ypos;
	
	private ObjectMap<ExitDir, MetaPortal> portals = new ObjectMap<ExitDir, MetaPortal>();
	
	public static MetaOctaRoomBuilder INSTANCE = new MetaOctaRoomBuilder();

	private MetaOctaRoom room;

	private MetaOctaRoomBuilder() { }

	public void init()
	{
		room = new MetaOctaRoom();
		width = 0.0f;
		height = 0.0f;
		xpos = 0.0f;
		ypos = 0.0f;
		portals.clear();
		startPortal = null;
	}
	
	public MetaOctaRoom build()
	{
		
		
		if( startPortal == null )
		{
			// Explicit placement
			room.setSize(xpos, ypos, width, height);	
		}
		else
		{
			// The startportal is a portal from another feature. The exit points away from that feature towards this new room.
			// We do Automatic placement of the room from that portal.
			ExitDir exit = startPortal.getExit();
			
			// https://en.wikipedia.org/wiki/Octagon
			// space for portal is a
			// rest of the side is 2 * (a / sqrt(2)), so one side is a / sqrt(2)
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
			
			portals.put( startPortal.getExit().getOpposite(), startPortal );
			
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

			if(metaPortalN.getSource()== null )
				metaPortalN.setSource(room);
			room.addPortal( ExitDir.N, metaPortalN );
		}
		if( metaPortalS != null )
		{
			metaPortalS.centerX = xpos + width * 0.5f;
			metaPortalS.centerY = ypos;
			if(metaPortalS.getSource()== null )
				metaPortalS.setSource(room);
			room.addPortal( ExitDir.S, metaPortalS );
		}
		
		if( metaPortalE != null )
		{
			metaPortalE.centerX = xpos + width;
			metaPortalE.centerY = ypos + height * 0.5f;
			if(metaPortalE.getSource()== null )
				metaPortalE.setSource(room);
			room.addPortal( ExitDir.E, metaPortalE );
		}
		
		if( metaPortalW != null )
		{
			metaPortalW.centerX = xpos;
			metaPortalW.centerY = ypos + height * 0.5f;
			if(metaPortalW.getSource()== null )
				metaPortalW.setSource(room);
			room.addPortal( ExitDir.W, metaPortalW );
		}
		
		return room;
	}
	
	public MetaOctaRoomBuilder addPortal( ExitDir dir, float width )
	{
		MetaPortal portal = new MetaPortal();
		portal.START_TYPE = MetaPortal.RECTANGLE_ROOM;
		portal.setExit( dir );
		portal.width = width;
		portal.setSource(this.room);
		
		portals.put( dir, portal );

		return this;
	}
	
	public MetaOctaRoomBuilder setSize( float width, float height )
	{
		this.width = width;
		this.height = height;
		
		return this;
	}
	
	public MetaOctaRoomBuilder setPosition( float xpos, float ypos )
	{
		this.xpos = xpos;
		this.ypos= ypos;
		
		return this;
	}

	public MetaOctaRoomBuilder createFromDir( ExitDir dir, MetaPortal portal )
	{
		this.startPortal = portal;
		return this;
	}
	
}
