package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.procedural.corridor.AxisAlignedCorridor;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public class MetaCorridor implements MetaFeature
{
	private Rectangle rect = new Rectangle();
	
	private float length;
	private float width;
	
	private MetaPortal startPortal = new MetaPortal(); 
	private MetaPortal endPortal = new MetaPortal();
	
	public AxisAlignedCorridor real;
	private int id;

	public MetaCorridor()
	{
		id = IDGenerator.getNextId();
	}

	@Override
	public int getId()
	{
		return id;
	}
	
	public void addStartPortal( ExitDir exitDir, MetaPortal portal )
	{
		portal.setExit(exitDir);
		startPortal = portal;
	}
	
	public void addEndPortal( ExitDir exitDir, MetaPortal portal )
	{
		portal.setExit(exitDir);
		endPortal = portal;
	}
	
	public MetaPortal getStartPortal()
	{
		return startPortal;
	}
	
	public MetaPortal getEndPortal()
	{
		return endPortal;
	}
	
	public void shutEnd()
	{
		endPortal = null;
	}

	public void closePortal( MetaPortal port )
	{
		if(startPortal.equals(port))
		{
			startPortal = null;
			return;
		}

		if(endPortal.equals(port))
		{
			endPortal = null;
			return;
		}
	}
	
	public void setSize( float posx, float posy, float w, float h )
	{
		rect.x = posx;
		rect.y = posy;
		
		rect.width = w;
		rect.height = h;
	}
	
	public void setLengthWidth( float length, float width )
	{
		this.length = length;
		this.width = width;
	}
	
	public float getLength()
	{
		return length;
	}
	
	public float getWidth()
	{
		return width;
	}
	
	private static Rectangle testRect = new Rectangle();
	private static float H = 0.00001f;
	private static float H2 = 0.000025f;
	@Override
	public boolean overlaps(Rectangle rect)
	{
		// adjust the test rect to just smaller, so that the corridor does not collide with exact same coordinates of the adjacent room
		testRect.set(this.rect);
		testRect.x += H;
		testRect.y += H;
		testRect.width -= H2;
		testRect.height -= H2;
		return testRect.contains(rect) || testRect.overlaps( rect );
	}

	@Override
	public Rectangle getBounds()
	{
		return rect;
	}

	@Override
	public Array<MetaPortal> getPortalArray(MetaPortal exclude) {

		Array<MetaPortal> ret = new Array<MetaPortal>();

		if(startPortal != null && !startPortal.equals(exclude))
		{
			ret.add(startPortal);
		}

		if(endPortal != null && !endPortal.equals(exclude))
		{
			ret.add(endPortal);
		}

		return ret;
	}
	
}
