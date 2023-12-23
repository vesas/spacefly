package com.vesas.spacefly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.TimeUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.CListener;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.InventoryScreen;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.PlayerBullets;
import com.vesas.spacefly.game.PlayerInput;
import com.vesas.spacefly.monster.MonsterBullets;
import com.vesas.spacefly.world.AbstractGameWorld;

import util.DebugShow;
import util.FrameTime;
import util.Log;

public class SpaceflyGame extends Game 
{
	private InventoryScreen inventory = new InventoryScreen();
	
	float accum = 0;
	
	float debugKeyCooldown = 0;
	float pauseKeyCooldown = 0;
	
	boolean paused = false;
	
	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	private static float TARGET_FPS = 60.0f;
	
	private static long NANOS_TO_SECONDS = 1000000000;
	
	private static double TARGET_FRAME_TIME = 1.0 / TARGET_FPS;
	
	private static long TARGET_FRAME_NANOTIME = (long)(NANOS_TO_SECONDS * TARGET_FRAME_TIME);
	
	private long lastFrameStarted = 0;
	
	private CListener clistener;
	
	private Screen currentScreen;
	private GameScreen gameScreen;
	private PlayerInput playerInput;
	
	private GLProfiler glProfiler;

	private int frameNumber = 0;
	
	@Override
	public void create() {		
		
		G.loadTextures();

		glProfiler = new GLProfiler(Gdx.graphics);
		glProfiler.enable();
		// Lwjgl3Window.getGraphics();
		
		Player.INSTANCE.init();
		
		G.loadTextures();

		VisUI.load(SkinScale.X2);


		glProfiler = new GLProfiler(Gdx.graphics);
		glProfiler.enable();
		// Lwjgl3Window.getGraphics();
		
		Player.INSTANCE.init();
		
		gameScreen = new GameScreen(this);
		gameScreen.init();
		gameScreen.updatePosition( Player.INSTANCE.getWorldCenter(), 0.5f,0.0f );
		
		playerInput = new PlayerInput( gameScreen );
		Gdx.input.setInputProcessor( playerInput );
		
		AbstractGameWorld.INSTANCE.init(gameScreen);
		
		clistener = new CListener(); 
		Box2DWorld.world.setContactListener( clistener );
		
		inventory.init();

		currentScreen = new MainMenuScreen(this);
		
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

		
		DebugShow.debug = true;
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
	}

	@Override
	public void dispose() {
		
		gameScreen.dispose();
		
		VisUI.dispose();
		G.disposeTextures();
		
	}

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
		tick();
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

	public void tick()
	{
		//int d1 = GLProfiler.shaderSwitches;
		//int d2 = GLProfiler.calls;
		//int d3 = GLProfiler.drawCalls;
		//int d4 = GLProfiler.textureBindings;
		
		float floatDelta = Gdx.graphics.getDeltaTime();
		
		final boolean spacePressed =  Gdx.input.isKeyPressed(Keys.SPACE);
		
		debugKeyCooldown -= floatDelta;
		if( debugKeyCooldown <= 0 )
			debugKeyCooldown = 0;
		
		if( Gdx.input.isKeyPressed(Keys.F5) && debugKeyCooldown <= 0 )
		{	
			debugKeyCooldown = 1;
			DebugHelper.BOX2D_DEBUG = !DebugHelper.BOX2D_DEBUG;
		}

		if( Gdx.input.isKeyPressed(Keys.F6) && debugKeyCooldown <= 0 )
		{	
			debugKeyCooldown = 1;
			DebugHelper.GC_DEBUG = !DebugHelper.GC_DEBUG;
		}
		
		if( Gdx.input.isKeyPressed(Keys.F7) && debugKeyCooldown <= 0 )
		{	
			debugKeyCooldown = 1;
			DebugHelper.VISIB_DEBUG = !DebugHelper.VISIB_DEBUG;
		}

		if( Gdx.input.isKeyPressed(Keys.F8) && debugKeyCooldown <= 0 )
		{	
			debugKeyCooldown = 1;
			DebugHelper.PLAYER_DEBUG = !DebugHelper.PLAYER_DEBUG;
		}

		if( Gdx.input.isKeyPressed(Keys.F9) && debugKeyCooldown <= 0) {
			debugKeyCooldown = 1;
			DebugHelper.FRAMETIME_DEBUG = !DebugHelper.FRAMETIME_DEBUG;
		}
		
		pauseKeyCooldown -= floatDelta;
	
		if( spacePressed && pauseKeyCooldown <= 0 )
		{
			pauseKeyCooldown = 0.2f;
			paused = !paused;
		}
		
		if( paused )
			return;
			
		AbstractGameWorld.INSTANCE.tick( gameScreen, floatDelta );
		
		Player.INSTANCE.tick( gameScreen, floatDelta );
		
		PlayerBullets.INSTANCE.tick( floatDelta );
		MonsterBullets.INSTANCE.tick( floatDelta );
		
		Box2DWorld.world.step(floatDelta, 3, 2);
		
		PlayerBullets.INSTANCE.removeBullets();
		MonsterBullets.INSTANCE.removeBullets();
	}
	
	public void renderFrame()
	{

		currentScreen.render(Gdx.graphics.getDeltaTime());
		// gameScreen.render(Gdx.graphics.getDeltaTime());

		

	}
	
	
	@Override
	public void resize(int width, int height) 
	{
		
		gameScreen.init();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
