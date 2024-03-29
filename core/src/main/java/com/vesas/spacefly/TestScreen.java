package com.vesas.spacefly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.visibility.Triangulator;
import com.vesas.spacefly.world.procedural.room.WallBlock;

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
    private WallBlock block;
    private WallBlock block2;
    private WallBlock block3;
    private WallBlock block4;
    private WallBlock block5;
    private WallBlock block6;
    private void init() {

        

        final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(width, height);
	    ((OrthographicCamera)camera).setToOrtho(false, width, height);
	    ((OrthographicCamera)camera).zoom = 1.0f;

        camera.position.set(0, 0, 0);

        float scale = 0.012f;

        // viewport = new FitViewport( width  * scale, height * scale, camera );
        viewport = new FitViewport( width  * scale, height * scale, camera );

        viewport.apply(true);
        // viewport.centerCameraAt(0, 0);
        
        batch = new SpriteBatch();

        viewport.update(width, height, true);

        batch.setProjectionMatrix(camera.combined);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        block = new WallBlock(10);
        block.initBottomLeft( 5f, 3f , 0);

        block2 = new WallBlock(10);
        block2.initBottomLeft( 10, 3 , 45);

        block3 = new WallBlock(10);
        block3.initBottomLeft( (float)(10 + (5 / Math.sqrt(2))) , (float)(3 + (5 / Math.sqrt(2))) , 90);

        block4 = new WallBlock(10);
        block4.initBottomLeft( (float)(5 - (5 / Math.sqrt(2))) , (float)(3 + (5 / Math.sqrt(2))) , -45);

        block5 = new WallBlock(10);
        block5.initBottomLeft( (float)(5 - (5 / Math.sqrt(2))) + WALL_WIDTH, (float)(3 + (5 / Math.sqrt(2))) , 90);

        block6 = new WallBlock(10);
        block6.initBottomLeft( (float)(5 - (5 / Math.sqrt(2))) + WALL_WIDTH, (float)(3 + 5 + (5 / Math.sqrt(2))) , 45);

        G.shapeRenderer = new ShapeRenderer();
        G.shapeRenderer.setProjectionMatrix(camera.combined);
    }

    @Override
    public void show() {
        
    }

    private Triangulator.Triangle triangle = new Triangulator.Triangle(new Triangulator.Vertex(2,1), 
        new Triangulator.Vertex(5,8), new Triangulator.Vertex(1,3.5f));

    private static float degs = 0.0f;
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

        degs += delta * 18.7f;
        line1.setAngleDeg(degs);

        G.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        G.shapeRenderer.setColor(1, 1, 1, 1);
        
        float centerx = 5;
        float centery = 8;
        
        // G.shapeRenderer.line(centerx, centery, centerx + 100, centery + 50);

        G.shapeRenderer.line(centerx, centery, centerx + line1.x, centery + line1.y);

        G.shapeRenderer.setColor(1, 0.5f, 0.5f, 1);
        line2.set(line1.y, -line1.x);
        G.shapeRenderer.line(centerx + 0, centery + 0, centerx + line2.x, centery + line2.y);

        triangle.circumCenter(G.shapeRenderer);

        G.shapeRenderer.end();

        Gdx.gl.glFlush();
    }

    static private Vector2 line1 = new Vector2(5,0);
    static private Vector2 line2 = new Vector2(5,0);

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
