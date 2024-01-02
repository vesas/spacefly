package com.vesas.spacefly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.world.procedural.room.Block1;
import com.vesas.spacefly.world.procedural.room.BlockRight;

public class TestScreen implements Screen {

    private Stage stage;
    private Table table;

    private TestGame game;

    private MainMenuLabel menu0;
    private MainMenuLabel menu1;
    private MainMenuLabel menu2;

    private int currentSelectedMenu = 0;

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Viewport viewport;

    

    public TestScreen(TestGame game) {

		super();
		this.game = game;

        stage = new Stage(new ScreenViewport());

        init();
	}

    private static float WALL_WIDTH = 0.5f;
    private BlockRight block;
    private BlockRight block2;
    private BlockRight block3;
    private BlockRight block4;
    private BlockRight block5;
    private BlockRight block6;
    private void init() {

        final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(width, height);
	    ((OrthographicCamera)camera).setToOrtho(false, width, height);
	    ((OrthographicCamera)camera).zoom = 1.0f;

        float scale = 0.012f;

        // viewport = new FitViewport( width  * scale, height * scale, camera );
        viewport = new FitViewport( width  * scale, height * scale, camera );
        
        batch = new SpriteBatch();

        viewport.update(width, height, true);

        batch.setProjectionMatrix(camera.combined);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        block = new BlockRight(10);
        block.init( 5, 3 , 0);

        block2 = new BlockRight(10);
        block2.init( 10, 3 , 45);

        block3 = new BlockRight(10);
        block3.init( (float)(10 + (5 / Math.sqrt(2))) , (float)(3 + (5 / Math.sqrt(2))) , 90);

        block4 = new BlockRight(10);
        block4.init( (float)(5 - (5 / Math.sqrt(2))) , (float)(3 + (5 / Math.sqrt(2))) , -45);

        block5 = new BlockRight(10);
        block5.init( (float)(5 - (5 / Math.sqrt(2))) + WALL_WIDTH, (float)(3 + (5 / Math.sqrt(2))) , 90);

        block6 = new BlockRight(10);
        block6.init( (float)(5 - (5 / Math.sqrt(2))) + WALL_WIDTH, (float)(3 + 5 + (5 / Math.sqrt(2))) , 45);

    }

    @Override
    public void show() {
        
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // sets the active texture unit to texture unit 0
        // spritebatch assumes this
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        batch.begin();

        // Sprite sprite = G.health[0];
        // 
        // sprite.setPosition(5, 2);
        // sprite.draw(batch);

        batch.disableBlending();

        // G.props[0].draw(batch);
        block.draw(batch);
        block2.draw(batch);
        block3.draw(batch);
        block4.draw(batch);
        block5.draw(batch);
        block6.draw(batch);

        batch.end();

        Gdx.gl.glFlush();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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

    @Override
    public void dispose() {
    }
    
}
