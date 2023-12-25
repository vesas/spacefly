package com.vesas.spacefly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Hud;
import com.vesas.spacefly.game.InventoryScreen;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.PlayerBullets;
import com.vesas.spacefly.game.PlayerInput;
import com.vesas.spacefly.game.Util;
import com.vesas.spacefly.game.cameraeffects.CameraPositionState;
import com.vesas.spacefly.game.cameraeffects.Shake;
import com.vesas.spacefly.game.cameraeffects.ShakeTranslate;
import com.vesas.spacefly.monster.MonsterBullets;
import com.vesas.spacefly.world.AbstractGameWorld;

import util.DebugShow;
import util.FrameTime;
import util.Log;

public class GameScreen implements Screen
{
	private static float TARGET_FPS = 60.0f;
	
	private static long NANOS_TO_SECONDS = 1000000000;
	
	private static double TARGET_FRAME_TIME = 1.0 / TARGET_FPS;
	
	private static long TARGET_FRAME_NANOTIME = (long)(NANOS_TO_SECONDS * TARGET_FRAME_TIME);
	
	private long lastFrameStarted = 0;

	boolean paused = false;
	private PlayerInput playerInput;
	
	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	private InventoryScreen inventory = new InventoryScreen();

	float accum = 0;
	float debugKeyCooldown = 0;
	float pauseKeyCooldown = 0;

	public SpriteBatch screenBatch;
	public SpriteBatch worldBatch;
	public SpriteBatch minimapBatch;
	
	public Camera camera;
	public Camera screenCamera;
	public Camera boxCamera;
	public Camera minimapCamera;
	
	public static final float GAMEWORLD_VIEWPORT_WIDTH = 16.0f * 3.7f;
	public static final float GAMEWORLD_VIEWPORT_HEIGHT = 9.0f * 3.7f;
	
	public Viewport viewport;
	public Viewport minimapViewport;

	private Hud hud;

	private GLProfiler glProfiler;
	private int frameNumber = 0;

	private SpaceflyGame game;

	public GameScreen(SpaceflyGame game) {
		super();
		this.game = game;
	}

	public void init()
	{
		glProfiler = new GLProfiler(Gdx.graphics);
		glProfiler.enable();

		final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
	    ((OrthographicCamera)camera).setToOrtho(false, width, height);
	    ((OrthographicCamera)camera).zoom = 0.8f;
	    
	    minimapCamera = new OrthographicCamera();
	    ((OrthographicCamera)minimapCamera).setToOrtho(false, width * 0.1f , height * 0.15f);
	    ((OrthographicCamera)minimapCamera).zoom = 0.5f;	
		
//		viewport = new ScalingViewport( Scaling.fillY, GAMEWORLD_VIEWPORT_WIDTH , GAMEWORLD_VIEWPORT_HEIGHT, camera );
	    
	    float scale = (float) ((GAMEWORLD_VIEWPORT_HEIGHT / height ) * 0.5);
	    viewport = new FitViewport( width  * scale, height * scale, camera );
	    
	    minimapViewport = new ExtendViewport( width  * scale , height * scale , minimapCamera );
		
		screenCamera = new OrthographicCamera();
	    ((OrthographicCamera)screenCamera).setToOrtho(false, width, height);
	    
	    boxCamera = new OrthographicCamera();
	    ((OrthographicCamera)boxCamera).setToOrtho(false, width , height);
	    
		screenBatch = new SpriteBatch();
		worldBatch = new SpriteBatch();
		minimapBatch = new SpriteBatch();
		
		G.shapeRenderer = new ShapeRenderer();
		
		viewport.update(width, height, true);
		
		screenBatch.setProjectionMatrix(screenCamera.combined);
		worldBatch.setProjectionMatrix(camera.combined);
		minimapBatch.setProjectionMatrix(minimapCamera.combined);
		G.shapeRenderer.setProjectionMatrix(camera.combined);
		
		worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		
		hud = new Hud();

		Shake shake = new ShakeTranslate();
		CameraPositionState.addEffect( shake );

		inventory.init();

		playerInput = new PlayerInput( this );
		Gdx.input.setInputProcessor( playerInput );
	}

	public float getCameraAngle(OrthographicCamera cam) {
		return ((float) Math.atan2(cam.up.y, cam.up.x) * Util.RADTODEG);
	}

	public void updatePosition( Vector2 pos, float delta, float angle )
	{
		minimapCamera.position.x = pos.x;
		minimapCamera.position.y = pos.y;
		
		CameraPositionState.tick( delta );
		CameraPositionState.performEffect( pos, camera.position );

		float playerAngle = ((angle+90) % 360) * Util.RADTODEG;
		// Vector3 camDir = new Vector3(0.0f, 1.0f, 0.0f);
		// camDir.rotate(angle, 0.0f, 0.0f, 1.0f);
		// camera.rotate((this.getCameraAngle(((OrthographicCamera)camera)) - playerAngle), 0.0f, 0.0f, -1.0f);

		// camera. rotate(angle, 0.0f, 0.0f, 1.0f);
		
		final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();
		
		final boolean centerCamera = false;
		
		
		camera.update();    
		minimapCamera.update();
		
		worldBatch.setProjectionMatrix(camera.combined);
		minimapBatch.setProjectionMatrix(minimapCamera.combined);
		
		G.shapeRenderer.setProjectionMatrix(camera.combined);
		
	}
	
	
	public boolean outSideScreen( Vector2 pos, int tolerance )
	{	
		return false;
	}
	
	public void dispose()
	{
		
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {

		
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
		
		// float delta = Gdx.graphics.getDeltaTime();
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

	private void renderFrame() {

		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		worldBatch.begin();
		
		AbstractGameWorld.INSTANCE.draw( this );
		
		worldBatch.begin();
		
		PlayerBullets.INSTANCE.draw( this );
		Player.INSTANCE.draw( this );
		worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		MonsterBullets.INSTANCE.draw( this );
		
		worldBatch.end();
	
		screenBatch.begin();
		
		hud.draw( this );
		
		DebugHelper.printGCStats(this.screenBatch);
		
		if( this.isPaused() )
		{
			G.font.draw( this.screenBatch, "PAUSED", (float)(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() * 0.5 - 100f), 
						(float)(Gdx.graphics.getHeight() - Gdx.graphics.getHeight() * 0.9f ) );

			 //G.shapeRenderer.setProjectionMatrix(screenCamera.combined);
			//screenBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
			// inventory.draw(screen.batch );
		}

		DebugHelper.render( this );
		DebugShow.draw( this );
		this.screenBatch.end();

		Gdx.gl.glFlush();
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
			
		AbstractGameWorld.INSTANCE.tick( this, floatDelta );
		
		Player.INSTANCE.tick( this, floatDelta );
		
		PlayerBullets.INSTANCE.tick( floatDelta );
		MonsterBullets.INSTANCE.tick( floatDelta );
		
		Box2DWorld.world.step(floatDelta, 3, 2);
		
		PlayerBullets.INSTANCE.removeBullets();
		MonsterBullets.INSTANCE.removeBullets();
	}

	@Override
	public void resize(int width, int height) {
		this.init();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}
}
