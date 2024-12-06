package com.vesas.spacefly.screen;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vesas.spacefly.TestGame;
import com.vesas.spacefly.quadtree.Point;
import com.vesas.spacefly.quadtree.QuadTree;
import com.vesas.spacefly.util.SimplexNoise;
import com.vesas.spacefly.visibility.DelaunayTriangulator;
import com.vesas.spacefly.visibility.Edge;
import com.vesas.spacefly.visibility.EndPoint;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;
import com.vesas.spacefly.world.procedural.room.rectangleroom.RectangleRoom;
import com.vesas.spacefly.world.procedural.room.rectangleroom.RectangleRoomBuilder;

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
    private Visibility visib;

    private ShapeRenderer shapeRenderer;

    private int test = 0;

    public TestScreen(TestGame game, int test) {

		super();
		this.game = game;
        this.test = test;

        stage = new Stage(new ScreenViewport());

        visib = new Visibility();

        init();
	}
    

    private void init() {

        final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();

        camera = new OrthographicCamera(width, height);
	    ((OrthographicCamera)camera).setToOrtho(false, width, height);
	    ((OrthographicCamera)camera).zoom = 1.0f;

        camera.position.set(0, 0, 0);

        float scale = 0.021f;

        // viewport = new FitViewport( width  * scale, height * scale, camera );
        viewport = new FitViewport( width  * scale, height * scale, camera );

        viewport.apply(true);
        // viewport.centerCameraAt(0, 0);
        
        batch = new SpriteBatch();

        viewport.update(width, height, true);

        batch.setProjectionMatrix(camera.combined);
        // batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        this.shapeRenderer = new ShapeRenderer();
        this.shapeRenderer.setProjectionMatrix(camera.combined);

        if(test == 1) {
            test1Init();
        }
        else if(test == 2) {
            testInit2();
        }
        else if(test == 3) {
            testInit3();
        }
        
    }

    // 
    // test1
    // 
    private QuadTree tree = null;

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

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.line(c.x - d.x, c.y - d.y, c.x + d.x, c.y - d.y);
        shapeRenderer.line(c.x + d.x, c.y - d.y, c.x + d.x, c.y + d.y);
        shapeRenderer.line(c.x + d.x, c.y + d.y, c.x - d.x, c.y + d.y);
        shapeRenderer.line(c.x - d.x, c.y + d.y, c.x - d.x, c.y - d.y);
        shapeRenderer.end();

        List<Point> points = new ArrayList<>();
        tree.getAllPoints(points);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0.5f, 0.5f, 1);
        for(Point p : points) {
            shapeRenderer.circle(p.x, p.y, 0.15f, 12);
        }
        shapeRenderer.end();

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

    // 
    // test3
    // 

    private RectangleRoom room1;
    private void testInit3() {

        visib.startLoad();
        RectangleRoomBuilder.INSTANCE.setPos(0,0);
        RectangleRoomBuilder.INSTANCE.setVisib(visib);

        MetaRectangleRoom metaRoom = new MetaRectangleRoom();
        metaRoom.setSize(4, 4, 12, 12);
        metaRoom.setHasColumns(true);
        MetaPortal metaPortalN = new MetaPortal();
        metaPortalN.setWidth(2);
        metaRoom.addPortal( ExitDir.S, metaPortalN );

        room1 = RectangleRoomBuilder.INSTANCE.buildFrom(metaRoom);

        visib.finishLoad();

    }

    private void testRender3(float delta) {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Reset projection matrix before drawing
        batch.setProjectionMatrix(camera.combined);
        
        batch.begin();

        room1.draw(batch);


        batch.end();

        
        visib.setLightLocation(5, 5);
        visib.sweep();

        // drawVisibility(new Vector2(5,5));
        renderVisibilityDebug2();

        
    }

    private void renderVisibilityDebug2() 
	{
		shapeRenderer.begin(ShapeType.Line);
		
		shapeRenderer.setColor(0.999f, 0.999f, 0.999f, 0.299f);
		
		List<Edge> edges = visib.edges;
		final int size = edges.size();
		for( int i = 0; i < size; i++ )
		{
			final Edge edge = edges.get( i );
			
			if( edge.isBoundary() ) 
			{
				shapeRenderer.setColor(0.999f, 0.999f, 0.999f, 0.599f);
			}
			else 
			{
				float val = edge.procRank;
				val = val * 0.5f;
				if(val > 1.0f) {
					val = 1.0f;
				}

				shapeRenderer.setColor(0.0f + val, 0.299f, 0.299f, 0.499f);
			}

			final EndPoint e0 = edge.getEndPoint1();
			final float ax = e0.point.x;
			final float ay = e0.point.y;
			
			final EndPoint e1 = edge.getEndPoint2();
			final float bx = e1.point.x;
			final float by = e1.point.y;
						
			shapeRenderer.line(ax, ay, bx, by);

		}

		shapeRenderer.end();

		/*
		screen.worldBatch.begin();
		G.font.draw(screen.worldBatch, "S", 123 - 16, 123 + 15);
		screen.worldBatch.end();
		*/
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
        else if(test == 3) {
            testRender3(delta);
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
