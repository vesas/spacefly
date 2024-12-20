package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public class MetaRectangleRoomBuilder
{
	private MetaPortal startPortal;
	
	private float width, height;
	private float xpos, ypos;
	
	private ObjectMap<ExitDir, MetaPortal> portals = new ObjectMap<ExitDir, MetaPortal>();
	
	public static MetaRectangleRoomBuilder INSTANCE = new MetaRectangleRoomBuilder();

	private MetaRectangleRoom room;

	private MetaRectangleRoomBuilder() { }

	public void init()
	{
		room = new MetaRectangleRoom();
		width = 0.0f;
		height = 0.0f;
		xpos = 0.0f;
		ypos = 0.0f;
		portals.clear();
		startPortal = null;
	}
	
	public MetaRectangleRoom build()
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

		if(this.width > 10 && this.height > 10) {
			room.setHasColumns(true);

			float [] widths = { 2.0f, 3.5f };
			int index = GenSeed.random.nextInt(widths.length);
			
			room.setHalfColumnWidth(widths[index]);
		}
		
		return room;
	}
	
	public MetaRectangleRoomBuilder addPortal( ExitDir dir, float width )
	{
		MetaPortal portal = new MetaPortal(width);
		portal.START_TYPE = MetaPortal.RECTANGLE_ROOM;
		portal.setExit( dir );
		portal.setSource(this.room);
		
		portals.put( dir, portal );

		return this;
	}
	
	public MetaRectangleRoomBuilder setSize( float width, float height )
	{
		this.width = width;
		this.height = height;
		
		return this;
	}
	
	public MetaRectangleRoomBuilder setPosition( float xpos, float ypos )
	{
		this.xpos = xpos;
		this.ypos= ypos;
		
		return this;
	}

	public MetaRectangleRoomBuilder createFromDir( ExitDir dir, MetaPortal portal )
	{
		this.startPortal = portal;
		return this;
	}
	
}
