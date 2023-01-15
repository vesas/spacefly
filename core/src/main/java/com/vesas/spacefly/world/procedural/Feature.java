package com.vesas.spacefly.world.procedural;

import com.vesas.spacefly.game.Screen;

// feature on the live map
public interface Feature
{
	public void init();
	
	public void draw(Screen screen);
	public void drawWithVisibility(Screen screen);
	
	
	public float getXpos();
	public float getYpos();
	
	public float getWidth();
	public float getHeight();
	
	public void tick(Screen screen, float delta);
	
}
