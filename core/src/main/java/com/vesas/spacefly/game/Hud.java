package com.vesas.spacefly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.world.AbstractGameWorld;

public class Hud
{
	
	public void draw(GameScreen screen)
	{
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		int health = Player.INSTANCE.getHealth();
		int healthMax = Player.INSTANCE.getHealthMax();
		
		int ammo = Player.INSTANCE.getAmmo();
		int ammoMax = Player.INSTANCE.getAmmoMax();
		
		G.wFont.getData().setScale(0.45f, 0.45f);
		G.wFont.draw( screen.screenBatch, "Ammo" , width - (float)250, 120);
		G.wFont.draw( screen.screenBatch, ""+ ammo + "/" + ammoMax , width - (float)250, 80);
		
		G.wFont.draw( screen.screenBatch, "Health" , 50, 120);
		G.wFont.draw( screen.screenBatch, "" + health + "/" + healthMax, 50, 80);
		
		screen.screenBatch.end();
		
		drawMinimap( screen );
		
		screen.viewport.apply(false);
	}
	
	private void drawMinimap(GameScreen screen)
	{
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		screen.minimapBatch.begin();
		screen.minimapBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		G.shapeRenderer.setProjectionMatrix(screen.screenCamera.combined);
		G.shapeRenderer.begin(ShapeType.Filled);
		G.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		G.shapeRenderer.rect( width - (float)322, height -(float)322, 2, 302);
		G.shapeRenderer.rect( width - (float)322, height -(float)322, 302, 2);
		
		G.shapeRenderer.rect( width - (float)20, height -(float)20, 2, -302);
		G.shapeRenderer.rect( width - (float)18, height -(float)20, -304, 2);
		G.shapeRenderer.end();
		
		G.shapeRenderer.setProjectionMatrix(screen.minimapCamera.combined);
		
		Gdx.gl.glViewport( width - 320, height - 320, 300, 300 );
		
		AbstractGameWorld.INSTANCE.drawMiniMap( screen );
		
		Player.INSTANCE.drawMiniMap( screen );
		
		screen.minimapBatch.end();
		
		screen.screenBatch.begin();
		G.shapeRenderer.setProjectionMatrix(screen.camera.combined);
		
	}
}
