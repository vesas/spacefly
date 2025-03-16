package com.vesas.spacefly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.box2d.WorldWrapper;
import com.vesas.spacefly.game.CListener;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.screen.TestScreen;
import com.vesas.spacefly.util.DebugShow;

public class TestGame extends Game 
{
	private GameScreen gameScreen;

	// which test to run
	private int testNumber;

	public TestGame(int test) {
		this.testNumber = test;
	}
	
	@Override
	public void create() {		
		
		G.loadTextures();

		Player.INSTANCE.init(3,1);
		
		Box2DWorld.init(new WorldWrapper(new Vector2(0, 0), true), new Box2DDebugRenderer());
		Box2DWorld.world.setContactListener( new CListener() );
		
		this.setScreen(new TestScreen(this, testNumber));
		// currentScreen = new MainMenuScreen(this);
		
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

		
		DebugShow.debug = true;
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
	}


	@Override
	public void dispose() {
		
		if(gameScreen != null)
			gameScreen.dispose();
		
		G.disposeTextures();
		
	}
}
