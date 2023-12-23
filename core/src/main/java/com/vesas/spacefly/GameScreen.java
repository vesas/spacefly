package com.vesas.spacefly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Hud;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.PlayerBullets;
import com.vesas.spacefly.game.Util;
import com.vesas.spacefly.game.cameraeffects.CameraPositionState;
import com.vesas.spacefly.game.cameraeffects.Shake;
import com.vesas.spacefly.game.cameraeffects.ShakeTranslate;
import com.vesas.spacefly.monster.MonsterBullets;
import com.vesas.spacefly.world.AbstractGameWorld;

import util.DebugShow;

public class GameScreen implements Screen
{
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

	private SpaceflyGame game;

	public GameScreen(SpaceflyGame game) {
		super();
		this.game = game;
	}

	public void init()
	{
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
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'show'");
	}

	@Override
	public void render(float delta) {
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
		
		if( game.isPaused() )
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

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'resize'");
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'pause'");
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'resume'");
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'hide'");
	}
}
