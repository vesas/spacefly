package com.vesas.spacefly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class InventoryScreen
{
	private Color c1 = new Color(0.2f, 0.2f, 0.1f, 0.9f);
	
	public void init()
	{
		
	}
	
	public void draw( SpriteBatch batch )
	{
		
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();
		
		G.shapeRenderer.begin(ShapeType.Filled);
		
		G.shapeRenderer.rect( w * 0.25f, h * 0.25f, w * 0.5f, h * 0.5f, c1, c1, c1, c1);
		G.shapeRenderer.end();
		
		
	}
}
