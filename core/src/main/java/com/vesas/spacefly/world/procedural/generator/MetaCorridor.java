package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Rectangle;
import com.vesas.spacefly.world.procedural.corridor.Corridor1;
import com.vesas.spacefly.world.procedural.room.ExitDir;

public class MetaCorridor implements MetaFeature
{
	private Rectangle rect = new Rectangle();
	
	private float length;
	private float width;
	
	private MetaPortal startPortal = new MetaPortal(); // this will be "out" from corridor 
	private MetaPortal endPortal = new MetaPortal(); // this will be "out" from corridor
	
	public Corridor1 real;
	
	
	public void addStartPortal( ExitDir exitDir, MetaPortal portal )
	{	
		portal.target  = null;
		portal.exit = exitDir;
		
		startPortal = portal;
	}
	
	public void addEndPortal( ExitDir exitDir, MetaPortal portal )
	{	
		portal.target  = null;
		portal.exit = exitDir;
		
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
	
	public MetaPortal getPortal( ExitDir exitDir )
	{
		if( startPortal.exit.equals( exitDir ))
			return startPortal;
		
		if( endPortal.exit.equals( exitDir ))
			return endPortal;
		
		return null;
		
	}
	
	public void setSize( float posx, float posy, float w, float h )
	{
		rect.x = posx;
		rect.y = posy;
		
		rect.width = w;
		rect.height = h;
	}
	
	
	public Rectangle getBounds()
	{
		return rect;
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
	public Rectangle getRect()
	{
		return rect;
	}
	
}
