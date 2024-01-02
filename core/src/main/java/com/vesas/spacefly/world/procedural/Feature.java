package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.vesas.spacefly.GameScreen;

// feature on the live map
public interface Feature
{
	public void init();
	
	public void draw(SpriteBatch batch);
	public void drawWithVisibility(GameScreen screen);
	
	
	public float getXpos();
	public float getYpos();
	
	public float getWidth();
	public float getHeight();
	
	public void tick(GameScreen screen, float delta);
	
}
