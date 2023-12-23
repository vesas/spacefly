package com.vesas.spacefly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.vesas.spacefly.GameScreen;

public class PlayerInput implements InputProcessor
{
	private GameScreen screen;

	private int zoom = 250;
	
	public PlayerInput( GameScreen screen )
	{
		this.screen = screen;
	}

	@Override
	public boolean keyDown(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY)
	{
		final float worldH = screen.viewport.getWorldHeight();
		final float worldW = screen.viewport.getWorldWidth();
		
		final int screenW = Gdx.graphics.getWidth();
		final int screenH = Gdx.graphics.getHeight();
		
		screen.viewport.setWorldHeight(worldH + (worldH * amountY * 0.05f) );
		screen.viewport.setWorldWidth(worldW + (worldW * amountY * 0.05f ));
		screen.viewport.update(screenW, screenH);
		
		zoom += (amountY * 1);
		
		screen.worldBatch.setProjectionMatrix(screen.camera.combined);
		screen.screenBatch.setProjectionMatrix(screen.screenCamera.combined);
		screen.minimapBatch.setProjectionMatrix(screen.minimapCamera.combined);
		
		return true;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}

}
