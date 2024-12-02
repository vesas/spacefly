package com.vesas.spacefly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.CListener;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.screen.MainMenuScreen;
import com.vesas.spacefly.util.DebugShow;
import com.vesas.spacefly.world.AbstractGameWorld;

public class SpaceflyGame extends Game 
{
	private GameScreen gameScreen;
	
	@Override
	public void create() {		
		
		G.loadTextures();

		Player.INSTANCE.init(3,1);
		
		// Lwjgl3Window.getGraphics();
		
		
		
		AbstractGameWorld.INSTANCE.init();
		
		Box2DWorld.world.setContactListener( new CListener() );
		
		this.setScreen(new MainMenuScreen(this));
		// currentScreen = new MainMenuScreen(this);
		
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

		
		DebugShow.debug = true;
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
	}

	public void restoreGameScreen() {
		this.setScreen(gameScreen);
		gameScreen.setPaused(false);
	}

	public void setGameScreen() {

		this.getScreen().dispose();

		gameScreen = new GameScreen(this);
		gameScreen.init();
		gameScreen.updatePosition( Player.INSTANCE.getWorldCenter(), 0.5f,0.0f );

		this.setScreen(gameScreen);
		// currentScreen = gameScreen;
	}

	@Override
	public void dispose() {
		
		if(gameScreen != null)
			gameScreen.dispose();
		
		G.disposeTextures();
		
	}

	/*
	@Override
	public void render()
	{
		FrameTime.initFrame();
		long time = TimeUtils.nanoTime();

		long clearstart = TimeUtils.nanoTime();
		
		long glclearstart = TimeUtils.nanoTime();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		long glclearend = TimeUtils.nanoTime();
		long glenablestart = TimeUtils.nanoTime();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		long glenableend = TimeUtils.nanoTime();
		long clearend = TimeUtils.nanoTime();

		FrameTime.glenable = (glenableend-glenablestart);
		FrameTime.glclear = (glclearend-glclearstart);
		FrameTime.cleartime = (clearend-clearstart);

		glProfiler.reset();
		
		renderFrame();
		
		float delta = Gdx.graphics.getDeltaTime();
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		while( (time - lastFrameStarted) < (TARGET_FRAME_NANOTIME - 100000000 ))
		{
			Thread.yield();
		
			try
			{
				Thread.sleep(0, 1);
			} catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			time = TimeUtils.nanoTime();
		}

		long end = TimeUtils.nanoTime();
		FrameTime.frametime = (end - time);

		if(DebugHelper.FRAMETIME_DEBUG) {
			String str = String.format("Frame: %-5s Vrtx count: %-5s" + 
			" shader switches: %-5s" + 
			" draw calls: %-5s", 
			this.frameNumber,
			(int)glProfiler.getVertexCount().average,
			glProfiler.getShaderSwitches(),
			glProfiler.getDrawCalls());
	
			Log.debug(str + " |" + FrameTime.asString());
		}
		
		lastFrameStarted = time;

		frameNumber++;
	}
	 */

	
	public void renderFrame()
	{

		// currentScreen.render(Gdx.graphics.getDeltaTime());
		// gameScreen.render(Gdx.graphics.getDeltaTime());

		

	}
	
}
