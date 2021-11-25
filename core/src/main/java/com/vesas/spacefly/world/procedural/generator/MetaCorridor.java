package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.procedural.corridor.Corridor1;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public class MetaCorridor implements MetaFeature
{
	private Rectangle rect = new Rectangle();
	
	private float length;
	private float width;
	
	private MetaPortal startPortal = new MetaPortal(); 
	private MetaPortal endPortal = new MetaPortal();
	
	public Corridor1 real;
	private int id;

	public MetaCorridor()
	{
		id = IDGenerator.getId();
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
	
	public void setSize( float length, float width )
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
	
	@Override
	public boolean overlaps(Rectangle rect)
	{
		return this.rect.contains(rect) || this.rect.overlaps( rect );
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
