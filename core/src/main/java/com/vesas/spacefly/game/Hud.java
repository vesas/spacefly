package com.vesas.spacefly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.world.AbstractGameWorld;

public class Hud {
	
	public void draw(final GameScreen screen) {
		final int width = Gdx.graphics.getWidth();
		// int height = Gdx.graphics.getHeight();
		
		G.wFont.getData().setScale(0.45f, 0.45f);
		drawAmmo(screen, width);
		drawHealth(screen);
		
		screen.screenBatch.end();
		
		Minimap.draw( screen );
		
		screen.viewport.apply(false);
	}

	private void drawAmmo(final GameScreen screen, final int width) {

		final int ammo = Player.INSTANCE.getAmmo();
		final int ammoMax = Player.INSTANCE.getAmmoMax();

		G.wFont.draw( screen.screenBatch, "Ammo" , width - (float)250, 120);
		G.wFont.draw( screen.screenBatch, ""+ ammo + "/" + ammoMax , width - (float)250, 80);
	}

	private void drawHealth(final GameScreen screen) {

		final int health = Player.INSTANCE.getHealth();
		final int healthMax = Player.INSTANCE.getHealthMax();

		G.wFont.draw( screen.screenBatch, "Health" , 50, 120);
		G.wFont.draw( screen.screenBatch, "" + health + "/" + healthMax, 50, 80);
	}

	private static class Minimap {

		// Constants for minimap sizing
		private static final float MINIMAP_SIZE_PERCENT = 0.3f; // 30% of screen height
		private static final float BORDER_THICKNESS = 3f;
		private static final float PADDING = 20f;

		public static void draw(final GameScreen screen) {
			int width = Gdx.graphics.getWidth();
			int height = Gdx.graphics.getHeight();

			// Calculate minimap dimensions
			int mapSize = (int)(height * MINIMAP_SIZE_PERCENT);
			int mapX = width - mapSize - (int)PADDING;
			int mapY = height - mapSize - (int)PADDING;
			
			screen.minimapBatch.begin();
			screen.minimapBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			Minimap.drawMinimapBorder(screen, mapX, mapY, mapSize);
			
			G.shapeRenderer.setProjectionMatrix(screen.minimapCamera.combined);
			
			Gdx.gl.glViewport(mapX, mapY, mapSize, mapSize);
			
			AbstractGameWorld.INSTANCE.drawMiniMap( screen );
			
			Player.INSTANCE.drawMiniMap( screen );
			
			screen.minimapBatch.end();
			
			screen.screenBatch.begin();
			G.shapeRenderer.setProjectionMatrix(screen.camera.combined);
		}

		private static void drawMinimapBorder(final GameScreen screen, final int x, final int y, final int size) {

			G.shapeRenderer.setProjectionMatrix(screen.screenCamera.combined);
			G.shapeRenderer.begin(ShapeType.Filled);
			G.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);

			// Draw border using calculated positions
			G.shapeRenderer.rect(x - BORDER_THICKNESS, y, BORDER_THICKNESS, size); // left
			G.shapeRenderer.rect(x - BORDER_THICKNESS, y + size, size + BORDER_THICKNESS, BORDER_THICKNESS); // top
			G.shapeRenderer.rect(x + size, y, BORDER_THICKNESS, size); // right
			G.shapeRenderer.rect(x - BORDER_THICKNESS, y, size + BORDER_THICKNESS, BORDER_THICKNESS); // bottom

			G.shapeRenderer.end();
		}
	}
	

	
}
