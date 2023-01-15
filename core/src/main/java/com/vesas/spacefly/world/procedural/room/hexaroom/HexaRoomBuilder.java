package com.vesas.spacefly.world.procedural.room.hexaroom;

import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.procedural.generator.MetaHexaRoom;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;

public class HexaRoomBuilder
{
	// bottom left position
	private float xpos, ypos;

	// xsize to the right, ysize to up
	private float xsize, ysize;

	private Visibility visib;
	
	public static HexaRoomBuilder INSTANCE = new HexaRoomBuilder();
	
	public void setVisib( Visibility visib )
	{
		this.visib = visib;
	}
	
	public HexaRoomBuilder setPos( float xpos, float ypos )
	{
		this.xpos = xpos; 
		this.ypos = ypos;
		
		return INSTANCE;
	}
	
	public HexaRoom buildFrom( MetaHexaRoom metaRoom )
	{
		this.xpos = metaRoom.getBounds().x;
		this.ypos = metaRoom.getBounds().y;
		
		this.xsize = metaRoom.getBounds().width;
		this.ysize = metaRoom.getBounds().height;
		
		visib.startConvexArea();

		float a = (float)(this.xsize / 2.0);
		float vertdistance = (float)((Math.sqrt(3) * a) / 2.0);

		float centerx = this.xpos + a;
		float centery = this.ypos + vertdistance;

		// A (a,0) -> B(a/2, sqrt(3)*a / 2)
		visib.addSegment(centerx + a, centery, (float)(centerx + a / 2.0), centery + vertdistance);
		visib.addSegment((float)(centerx + a / 2.0), centery + vertdistance, (float)(centerx - a / 2.0), centery + vertdistance);

		visib.addSegment((float)(centerx - a / 2.0), centery + vertdistance, centerx + a, centery);

		visib.finishConvexArea();
		
		HexaRoom room = new HexaRoom(); 
		
		room.setPosition( metaRoom.getBounds().x, metaRoom.getBounds().y);
		room.setDimensions( metaRoom.getBounds().width, metaRoom.getBounds().height );
		
		room.init();
		
		return room;
	}
}
