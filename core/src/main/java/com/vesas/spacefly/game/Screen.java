package com.vesas.spacefly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vesas.spacefly.game.cameraeffects.CameraPositionState;
import com.vesas.spacefly.game.cameraeffects.Shake;
import com.vesas.spacefly.game.cameraeffects.ShakeTranslate;

public class Screen
{

	public SpriteBatch screenBatch;
	public SpriteBatch worldBatch;
	public SpriteBatch minimapBatch;
	
	public Camera camera;
	public Camera screenCamera;
	public Camera boxCamera;
	public Camera minimapCamera;
	
	static public float GAMEWORLD_VIEWPORT_WIDTH = 16.0f * 3.7f;
	static public float GAMEWORLD_VIEWPORT_HEIGHT = 9.0f * 3.7f;
	
	public Viewport viewport;
	public Viewport minimapViewport;
	
	// current shakeDisplacement
	public Vector2 shakeDisplacement = new Vector2();
	
	public void init()
	{
		final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera();
	    ((OrthographicCamera)camera).setToOrtho(false, width, height);
	    ((OrthographicCamera)camera).zoom = 0.6f;
	    
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
		
		
//		minimapViewport.update((int)(width * 0.2f) , (int)(height * 0.2f), true);

		
		viewport.update(width, height, true);
		
		
		screenBatch.setProjectionMatrix(screenCamera.combined);
		worldBatch.setProjectionMatrix(camera.combined);
		minimapBatch.setProjectionMatrix(minimapCamera.combined);
		G.shapeRenderer.setProjectionMatrix(camera.combined);
		
		worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		
		Shake shake = new ShakeTranslate();
		CameraPositionState.addEffect( shake );
	}
	
	public void updatePosition( Vector2 pos, float delta )
	{
//		viewport.getCamera().position.x = pos.x;
//		viewport.getCamera().position.y = pos.y;
		
		minimapCamera.position.x = pos.x;
		minimapCamera.position.y = pos.y;
		
//		float lerp = 1.1f * delta;
//		Vector3 position = camera.position;
//		position.x += (pos.x - position.x) * lerp;
//		position.y += (pos.y - position.y) * lerp;
		
		CameraPositionState.tick( delta );
		CameraPositionState.performEffect( pos, camera.position );
		
		final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();
		
		final boolean centerCamera = false;
		
		
		camera.update();    
		minimapCamera.update();
		
		worldBatch.setProjectionMatrix(camera.combined);
		minimapBatch.setProjectionMatrix(minimapCamera.combined);
		
		G.shapeRenderer.setProjectionMatrix(camera.combined);
		
//		Graphics.batch.setProjectionMatrix(Graphics.mainViewport.getCamera().combined);
//		Graphics.polySpriteBatch.setProjectionMatrix(Graphics.mainViewport.getCamera().combined);
//		Graphics.shapeRenderer.setProjectionMatrix(Graphics.mainViewport.getCamera().combined);
	}
	
	
	public boolean outSideScreen( Vector2 pos, int tolerance )
	{	
		return false;
		
	}
	
	public void dispose()
	{
		
	}
}
