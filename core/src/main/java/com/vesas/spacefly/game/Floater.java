package com.vesas.spacefly.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

final public class Floater
{
	private float x,y; //position
	
	private float lifetime;
	private float maxLifetime;
	
	private boolean active = true;
	
	private String displayString;
	
	
	private Floater() 
	{
	}
	
	static public Floater newInstance()
	{
		Floater f = new Floater();
		f.init();
		return f;
	}
	
	public void setString( String val )
	{
		displayString = val;
	}
	
	public void setLife( float val )
	{
		lifetime = val;
	}
	
	public void setMaxLife( float val )
	{
		maxLifetime = val;
	}
	
	public void setPos( Vector2 pos )
	{
		x = pos.x;
		y = pos.y;
		
	}
	
	
	
	public void init()
	{
		active = true;
		lifetime = 0.0f;
		
		maxLifetime = 2.0f;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public void tick( float delta )
	{
		lifetime += delta;
		
		if( lifetime > maxLifetime )
		{
			active = false;
		}
		
		y = y + 0.0015f;
		x = x + 0.0005f;
	}
	
	private static Color tmpColor = new Color();
	
	public void render( Screen screen )
	{
		if (!active)
			return;

		tmpColor = G.font.getColor();

		float pos = lifetime / maxLifetime;
		float tempA = G.floatLerp(maxLifetime, 0, pos) / maxLifetime;
		G.font.setColor(1.0f, 1.0f, 1.0f, tempA);
		G.font.draw(screen.worldBatch, displayString, x, y);
		G.font.setColor(tmpColor);

	}
}
