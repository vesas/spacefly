package com.vesas.spacefly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.CListener;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.screen.TestScreen;
import com.vesas.spacefly.util.DebugShow;
import com.vesas.spacefly.world.AbstractGameWorld;

public class TestGame extends Game 
{
	private GameScreen gameScreen;

	private int test;

	public TestGame(int test) {
		this.test = test;
	}
	
	@Override
	public void create() {		
		
		G.loadTextures();

		Player.INSTANCE.init(3,1);
		
		// Lwjgl3Window.getGraphics();
		
		
		
		AbstractGameWorld.INSTANCE.init();
		
		Box2DWorld.world.setContactListener( new CListener() );
		
		this.setScreen(new TestScreen(this, test));
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
