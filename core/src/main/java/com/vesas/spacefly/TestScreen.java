package com.vesas.spacefly;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.visibility.DelaunayTriangulator;
import com.vesas.spacefly.world.procedural.room.WallBlock;

import quadtree.AABB;
import quadtree.Point;
import quadtree.QuadTree;
import util.SimplexNoise;

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

    private int test = 0;

    public TestScreen(TestGame game, int test) {

		super();
		this.game = game;
        this.test = test;

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

    private QuadTree tree = null;

    private void init() {

        final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(width, height);
	    ((OrthographicCamera)camera).setToOrtho(false, width, height);
	    ((OrthographicCamera)camera).zoom = 1.0f;

        camera.position.set(0, 0, 0);

        float scale = 0.028f;

        // viewport = new FitViewport( width  * scale, height * scale, camera );
        viewport = new FitViewport( width  * scale, height * scale, camera );

        viewport.apply(true);
        // viewport.centerCameraAt(0, 0);
        
        batch = new SpriteBatch();

        viewport.update(width, height, true);

        batch.setProjectionMatrix(camera.combined);
        // batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

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

        tree = new QuadTree(new AABB(new Point(15,15), new Point(10,10)));


        if(test == 1) {
            test1Init();
        }
        else if(test == 2) {
            testInit2();
        }
        
    }

    // 
    // test1
    // 
    private void test1Init() {

        float xx = 15f;
        float yy = 15f;
        tree.insert(xx+1, yy+1);
        tree.insert(xx+1.4f, yy+1.2f);
        tree.insert(xx+1.24f, yy+1.12f);
        tree.insert(xx+5,yy+2.7f);
        tree.insert(xx+2,yy+1);
        tree.insert(xx-4f,yy+2.6f);
        tree.insert(xx+1.5f, yy+1.5f);
        tree.insert(xx+3.9f,yy-3);
        tree.insert(xx-8.4f,yy-1.15f);
        tree.insert(xx-8.6f,yy-1.31f);
        tree.insert(xx-8.77f,yy-1.42f);
        tree.insert(xx-8.8f,yy-1.55f);
        tree.insert(xx-9.2f,yy-2.22f);
        tree.insert(xx-8.99f,yy-1.85f);
    }
    private void test1Render(float delta) {
        test1RenderTree(delta, tree);
    }
    private void test1RenderTree(float delta, QuadTree tree) {

        Point c = new Point(tree.getBoundary().centerX, tree.getBoundary().centerY);
        Point d = new Point(tree.getBoundary().halfWidth, tree.getBoundary().halfHeight);

        G.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        G.shapeRenderer.setColor(1, 1, 1, 1);
        G.shapeRenderer.line(c.x - d.x, c.y - d.y, c.x + d.x, c.y - d.y);
        G.shapeRenderer.line(c.x + d.x, c.y - d.y, c.x + d.x, c.y + d.y);
        G.shapeRenderer.line(c.x + d.x, c.y + d.y, c.x - d.x, c.y + d.y);
        G.shapeRenderer.line(c.x - d.x, c.y + d.y, c.x - d.x, c.y - d.y);
        G.shapeRenderer.end();

        List<Point> points = new ArrayList<>();
        tree.getAllPoints(points);

        G.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        G.shapeRenderer.setColor(1, 0.5f, 0.5f, 1);
        for(Point p : points) {
            G.shapeRenderer.circle(p.x, p.y, 0.15f, 12);
        }
        G.shapeRenderer.end();

        if(tree.getNorthWest() != null) {
            test1RenderTree(delta, tree.getNorthWest());
        }
        if(tree.getNorthEast() != null) {
            test1RenderTree(delta, tree.getNorthEast());
        }
        if(tree.getSouthWest() != null) {
            test1RenderTree(delta, tree.getSouthWest());
        }
        if(tree.getSouthEast() != null) {
            test1RenderTree(delta, tree.getSouthEast());
        }
    }

    // 
    // test2
    // 

    private Texture noiseTexture;
    private void testInit2() {
        int width = 512;
        int height = 512;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Multiple octaves with amplification
                double value =  
                      SimplexNoise.noise(x * 0.01f, y * 0.01f) * 0.6     // base frequency, amplified
                    + SimplexNoise.noise(x * 0.02f, y * 0.02f) * 0.3     // double frequency, half amplitude
                    + SimplexNoise.noise(x * 0.04f, y * 0.04f) * 0.15    // quadruple frequency, quarter amplitude
                    + SimplexNoise.noise(x * 0.08f, y * 0.08f) * 0.075;    
                
                // Clamp to [0,1]
                value = MathUtils.clamp((value + 1.0) / 2.0, 0, 1);
                
                // Debug first few values
                if (x < 2 && y < 2) {
                    System.out.println("Noise at " + x + "," + y + ": " + value);
                }
                
                pixmap.setColor((float)value, (float)value * 1.0f, (float)value * 1.0f, 1);
                pixmap.drawPixel(x, y);
            }
        }
        
        noiseTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void testRender2(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Reset projection matrix before drawing
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();
        float x = 0; // Start with fixed position for debugging
        float y = 0;
        batch.draw(noiseTexture, x, y, 32, 32);  // smaller size for testing
        batch.end();
    }


    @Override
    public void show() {
        
    }

    private DelaunayTriangulator.Triangle triangle = new DelaunayTriangulator.Triangle(new DelaunayTriangulator.Vertex(2,1), 
        new DelaunayTriangulator.Vertex(5,8), new DelaunayTriangulator.Vertex(1,3.5f));

    private static float degs = 0.0f;

    

    @Override
    public void render(float delta) {

        if(test == 1) {
            test1Render(delta);
        }
        else if(test == 2) {
            testRender2(delta);
        }
        

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
