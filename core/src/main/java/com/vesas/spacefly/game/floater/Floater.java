package com.vesas.spacefly.game.floater;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Util;

public final class Floater
{
	//position
	private float x;
	private float y; 
	
	private float lifetime;
	private float maxLifetime;
	
	private boolean active = true;
	
	private String displayString;
	
	Floater() {
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
	
	public void render( GameScreen screen )
	{
		if (!active)
			return;

		tmpColor = G.font.getColor();

		float pos = lifetime / maxLifetime;
		float tempA = Util.floatLerp(maxLifetime, 0, pos) / maxLifetime;
		G.font.setColor(1.0f, 1.0f, 1.0f, tempA);
		G.font.draw(screen.worldBatch, displayString, x, y);
		G.font.setColor(tmpColor);

	}
}
