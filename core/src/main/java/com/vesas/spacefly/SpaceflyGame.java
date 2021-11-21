package com.vesas.spacefly;

import util.DebugShow;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.Hud;
import com.vesas.spacefly.game.InventoryScreen;
import com.vesas.spacefly.game.PlayerBullets;
import com.vesas.spacefly.game.CListener;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.PlayerInput;
import com.vesas.spacefly.game.Screen;
import com.vesas.spacefly.monster.MonsterBullets;
import com.vesas.spacefly.world.AbstractGameWorld;

public class SpaceflyGame extends Game 
{
	private InventoryScreen inventory = new InventoryScreen();
	
	float accum = 0;
	
	float debugKeyCooldown = 0;
	float pauseKeyCooldown = 0;
	
	boolean paused = false;
	
	private static float TARGET_FPS = 60.0f;
	
	private static long NANOS_TO_SECONDS = 1000000000;
	private static long NANOS_TO_MS = 1000000;
	
	private static double TARGET_FRAME_TIME = 1.0 / TARGET_FPS;
	
	private static long TARGET_FRAME_NANOTIME = (long)(NANOS_TO_SECONDS * TARGET_FRAME_TIME);
	
	private long lastFrameStarted = 0;
	
	private CListener clistener;
	
	private Screen screen;
	private PlayerInput playerInput;
	
	private Hud hud;
	
	@Override
	public void create() {		
		
		//Gdx.input.setCursorCatched(true);
		
		G.loadTextures();
		
		Player.INSTANCE.init();
		
		screen = new Screen();
		screen.init();
		screen.updatePosition( Player.INSTANCE.getWorldCenter(), 0.5f );
		
		playerInput = new PlayerInput( screen );
		Gdx.input.setInputProcessor( playerInput );
		
		AbstractGameWorld.INSTANCE.init(screen);
		
		hud = new Hud();
		
//		TileBasedMap map = AbstractGameWorld.INSTANCE.getPathFindingGrid();
//		FindPath.init(map);
		
		
		clistener = new CListener(); 
		Box2DWorld.world.setContactListener( clistener );
		
		inventory.init();
		
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		
		DebugShow.debug = true;
		/*
		try {
            final org.lwjgl.input.Cursor emptyCursor = new org.lwjgl.input.Cursor( 1 , 1 , 0 , 0 , 1 , BufferUtils.createIntBuffer( 1 ) , null );
            org.lwjgl.input.Mouse.setNativeCursor( emptyCursor );
        } catch ( final Exception e ) {
            Gdx.app.log( "MouseCursor" , "init()  Exception e= " + e );
        }
        */
		
		//Gdx.graphics.setVSync(true);
		
//		GLProfiler.enable();
	}

	@Override
	public void dispose() {
		
		screen.dispose();
		
		G.disposeTextures();
		
	}

	@Override
	public void render()
	{
		Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		

		long time = TimeUtils.nanoTime();
		
		long nanoDelta = time - lastFrameStarted;
		float delta = (float)((float)nanoDelta / (float)NANOS_TO_SECONDS);
		
		tick();

		renderFrame();
		
		
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
		
		lastFrameStarted = time;
		
		
	}

	public void tick()
	{
		//int d1 = GLProfiler.shaderSwitches;
		//int d2 = GLProfiler.calls;
		//int d3 = GLProfiler.drawCalls;
		//int d4 = GLProfiler.textureBindings;
		
		float floatDelta = Gdx.graphics.getDeltaTime();
		
		final boolean spacePressed =  Gdx.input.isKeyPressed(Keys.SPACE);
		
		final boolean f5 =  Gdx.input.isKeyPressed(Keys.F5);
		final boolean f6 =  Gdx.input.isKeyPressed(Keys.F6);
		final boolean f7 =  Gdx.input.isKeyPressed(Keys.F7);
		
		debugKeyCooldown -= floatDelta;
		if( debugKeyCooldown <= 0 )
			debugKeyCooldown = 0;
		
		if( f5 && debugKeyCooldown <= 0 )
		{	
			debugKeyCooldown = 1;
			DebugHelper.BOX2D_DEBUG = !DebugHelper.BOX2D_DEBUG;
		}

		if( f6 && debugKeyCooldown <= 0 )
		{	
			debugKeyCooldown = 1;
			DebugHelper.GC_DEBUG = !DebugHelper.GC_DEBUG;
		}
		
		if( f7 && debugKeyCooldown <= 0 )
		{	
			debugKeyCooldown = 1;
			DebugHelper.VISIB_DEBUG = !DebugHelper.VISIB_DEBUG;
		}
		
		pauseKeyCooldown -= floatDelta;
	
		if( spacePressed && pauseKeyCooldown <= 0 )
		{
			pauseKeyCooldown = 0.2f;
			paused = !paused;
		}
		
		if( paused )
			return;
			
		AbstractGameWorld.INSTANCE.tick( screen, floatDelta );
		
		Player.INSTANCE.tick( screen, floatDelta );
		
		PlayerBullets.INSTANCE.tick( floatDelta );
		MonsterBullets.INSTANCE.tick( floatDelta );
		
		Box2DWorld.world.step(floatDelta, 6, 3);
		
		PlayerBullets.INSTANCE.removeBullets();
		MonsterBullets.INSTANCE.removeBullets();
	}
	
	public void renderFrame()
	{
		screen.worldBatch.begin();
		
		AbstractGameWorld.INSTANCE.draw( screen );
		
		
		screen.worldBatch.begin();
		
		PlayerBullets.INSTANCE.draw( screen );
		Player.INSTANCE.draw( screen );
		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		MonsterBullets.INSTANCE.draw( screen );
		
		
		screen.worldBatch.end();
		
		screen.screenBatch.begin();
		
		
		hud.draw( screen );
		
		
		
		DebugHelper.printGCStats(screen.screenBatch);
		
		if( paused )
		{
			G.font.draw( screen.screenBatch, "PAUSED", (float)(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() * 0.5 - 100f), 
						(float)(Gdx.graphics.getHeight() - Gdx.graphics.getHeight() * 0.9f ) );

			 //G.shapeRenderer.setProjectionMatrix(screenCamera.combined);
			//screenBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
			// inventory.draw(screen.batch );
		}

		DebugHelper.render( screen );
		DebugShow.draw( screen );
		screen.screenBatch.end();

	}
	
	
	@Override
	public void resize(int width, int height) 
	{
		screen.init();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
