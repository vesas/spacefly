package com.vesas.spacefly.world.procedural;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.DebugHelper;
import com.vesas.spacefly.game.AnimateEntity;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.Screen;
import com.vesas.spacefly.monster.BaseMonster;
import com.vesas.spacefly.monster.ZipperMonster;
import com.vesas.spacefly.particles.ExplosionInterface;
import com.vesas.spacefly.world.AbstractGameWorld;
import com.vesas.spacefly.world.procedural.corridor.Corridor1;
import com.vesas.spacefly.world.procedural.room.RectangleRoom;
import com.vesas.spacefly.visibility.Edge;
import com.vesas.spacefly.visibility.EndPoint;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.visibility.VisibilityPoly;

public class ProceduralGameWorld extends AbstractGameWorld
{
	private RectangleRoom room2 = new RectangleRoom();
	
	private Corridor1 cor1 = new Corridor1();
	
	private Array<Feature> feats;
	
	private Visibility visib;

	private EarClippingTriangulator tri = new EarClippingTriangulator();
	
	String vertexShader = "";
	String defaultPixelShader = "";
	
	private ShaderProgram defaultShader;
	
	private FrameBuffer fbo;


	/*
	 * 
	private void createVisibTestCase0()
	{
		int size = 10;
		// outside perimeter
		
		visib.initLoad();
		
		// bottom
		visib.addSegment(-size, 0, size, 0);
		
		// left
		visib.addSegment(-size, 0, 0, size);
		
		// right
		visib.addSegment(size, 0, 0, size);
		
		//
		// end top
		//
		
		// small one
		visib.addSegment( -size * 0.25f, 0 + size * 0.25f, +size * 0.25f,  0 + size * 0.25f );

		
	}

	private void createVisibTestCase1()
	{

		int size = 10;
		// outside perimeter
		
		visib.initLoad();
		
		// bottom
		visib.addSegment(-size, -size, size, -size);
		
		//
		// top
		//
		visib.addSegment(-size, size, size, size);
		
		//
		// end top
		//
		
		// small one close to right
		visib.addSegment(size - size * 0.25f, 0 + size * 0.7f,size - size * 0.25f, 0 - size * 0.7f );

		// left
		visib.addSegment(-size, -size, -size, size);
		// right
		visib.addSegment(size, -size, size, size);
		
	}

	private void createVisibTestCase2()
	{

		int size = 10;
		// outside perimeter
		
		visib.initLoad();
		
		// bottom
		visib.addSegment(-size, -size, size, -size);
		
		//
		// top
		//
		visib.addSegment(-size, size,-size + size*0.25f, size);
		visib.addSegment(-size + size*0.25f, size,-size + size*0.25f, size + size*0.5f);
		visib.addSegment(-size + size*0.25f, size + size*0.5f,size - size*0.25f, size + size*0.5f);
		visib.addSegment(size - size*0.25f, size + size*0.5f,size - size*0.25f, size);
		visib.addSegment(size - size*0.25f, size,size, size);
		
//		visib.addSegment(-size, size, size, size);
		
		//
		// end top
		//
		
		// small ones close to right
		visib.addSegment(size - size * 0.35f, 0 + size * 0.7f,size - size * 0.35f, 0 + size * 0.5f );
		visib.addSegment(size - size * 0.35f, 0 + size * 0.3f,size - size * 0.35f, 0 + size * 0.1f );
		
		visib.addSegment(size - size * 0.35f, 0 - size * 0.1f,size - size * 0.35f, 0 - size * 0.3f );
		visib.addSegment(size - size * 0.35f, 0 - size * 0.5f,size - size * 0.35f, 0 - size * 0.7f );
		
		// small ones close to left
		visib.addSegment(-size + size * 0.35f, 0 + size * 0.7f,-size + size * 0.35f, 0 + size * 0.5f );
		visib.addSegment(-size + size * 0.35f, 0 + size * 0.3f,-size + size * 0.35f, 0 + size * 0.1f );
		
		visib.addSegment(-size + size * 0.35f, 0 - size * 0.1f,-size + size * 0.35f, 0 - size * 0.3f );
		visib.addSegment(-size + size * 0.35f, 0 - size * 0.5f,-size + size * 0.35f, 0 - size * 0.7f );

		// left
		visib.addSegment(-size, -size, -size, size);
		// right
		visib.addSegment(size, -size, size, size);
		
	}
	

	private void createVisibTestCase3()
	{

		int size = 10;
		// outside perimeter
		
		visib.initLoad();
		
		// bottom
		visib.addSegment(-size, -size, size, -size);
		
		//
		// top
		//
		visib.addSegment(-size, size,-size + size*0.35f, size);
		visib.addSegment(-size + size*0.35f, size,-size + size*0.35f, size + size*0.5f);
		
//		visib.addSegment(-size + size*0.35f, size + size*0.5f,size - size*0.35f, size + size*0.5f);
		visib.addSegment(-size + size*0.35f, size + size*0.5f,
						 -size - size*0.55f, size + size*0.5f);
		visib.addSegment(-size - size*0.55f, size + size*0.5f,
						 -size - size*0.55f, size + size*1.5f);
		visib.addSegment(-size - size*0.55f, size + size*1.5f,
						  size + size*0.55f, size + size*1.5f);
		visib.addSegment( size + size*0.55f, size + size*1.5f,
					     size + size*0.55f, size + size*0.5f);
		visib.addSegment(size + size*0.55f, size + size*0.5f,
					     size - size*0.35f, size + size*0.5f);
		
		
		visib.addSegment(size - size*0.35f, size + size*0.5f,size - size*0.35f, size);
		visib.addSegment(size - size*0.35f, size,size, size);
		
//		visib.addSegment(-size, size, size, size);
		
		//
		// end top
		//
		
		// small ones close to right
		visib.addSegment(size - size * 0.45f, 0 + size * 0.7f,size - size * 0.45f, 0 + size * 0.5f );
		visib.addSegment(size - size * 0.45f, 0 + size * 0.3f,size - size * 0.45f, 0 + size * 0.1f );
		
		visib.addSegment(size - size * 0.45f, 0 - size * 0.1f,size - size * 0.45f, 0 - size * 0.3f );
		visib.addSegment(size - size * 0.45f, 0 - size * 0.5f,size - size * 0.45f, 0 - size * 0.7f );
		
		// small ones close to left
		visib.addSegment(-size + size * 0.45f, 0 + size * 0.7f,-size + size * 0.45f, 0 + size * 0.5f );
		visib.addSegment(-size + size * 0.45f, 0 + size * 0.3f,-size + size * 0.45f, 0 + size * 0.1f );
		
		visib.addSegment(-size + size * 0.45f, 0 - size * 0.1f,-size + size * 0.45f, 0 - size * 0.3f );
		visib.addSegment(-size + size * 0.45f, 0 - size * 0.5f,-size + size * 0.45f, 0 - size * 0.7f );

		// left
		visib.addSegment(-size, -size, -size, size);
		// right
		visib.addSegment(size, -size, size, size);
		
	}
	*/
	
	@Override
	public void init( Screen screen )
	{
		feats = new Array<Feature>();
		
		visib = new Visibility();
		
		visib.startLoad();
		WorldGen gen = new WorldGen( this,visib );
		
		feats = gen.generate(); 
		
//		createVisibTestCase1();	
//		
		visib.finishLoad();
		

		defaultPixelShader = Gdx.files.local("data/defaultPixelShader.glsl").readString();
		vertexShader = Gdx.files.local("data/vertexShader.glsl").readString();
		
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
//		 screen.
//		fbo = new FrameBuffer(Format.RGBA8888, width, height, false);
		
		float screenHeight = screen.viewport.getScreenHeight();
		float screenWidth = screen.viewport.getScreenWidth();
		fbo = new FrameBuffer(Format.RGBA8888, (int)width, (int)height, false);
		
		defaultShader = new ShaderProgram(vertexShader, defaultPixelShader);
//		defaultPixelShader = new FileHandle("data/defaultPixelShader.glsl").readString();

		defaultShader.begin();
		defaultShader.setUniformf("ambientColor", 0.15f, 0.15f, 0.15f, 0.25f);
		defaultShader.end();
		
		
		Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888 );
		pixmap.setColor(1.0f, 1.0f,1.0f,1.0f);
		pixmap.fill();
		
		textureSolid = new Texture(pixmap);
		
		int qwe = 0;
	}
	
	@Override
	public void draw(Screen screen)
	{

		Vector2 playerCenter = Player.INSTANCE.getWorldCenter();
		
		float screenHeight = screen.viewport.getScreenHeight();
		float screenWidth = screen.viewport.getScreenWidth();
		
		
		defaultShader.begin();
		defaultShader.setUniformf("ambientColor", 0.5f, 0.5f, 0.7f, 0.519f);
		defaultShader.setUniformi("u_lightmap", 1);
		defaultShader.setUniformf("resolution", screenWidth, screenHeight);
//		defaultShader.setUniformf("viewcenc", screenWidth*0.5f, screenHeight*0.5f);
		defaultShader.end();
		
		
		
		
//		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);

		
		screen.worldBatch.end();
		//draw the light to the FBO
		fbo.begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float lightSize = 5.0f;
		
		visib.setLightLocation( playerCenter.x, playerCenter.y);
		visib.sweep();
		drawVisibility( screen, fbo, playerCenter );
		//screen.worldBatch.draw(light, playerCenter.x - lightSize*0.5f , playerCenter.y - lightSize*0.5f, lightSize, lightSize);
		
		fbo.end();
		
		
		fbo.getColorBufferTexture().bind(1);
		textureSolid.bind(0);
		
		screen.worldBatch.setShader(defaultShader);
		screen.worldBatch.begin();
//		fbo.getColorBufferTexture().bind(1); //this is important! bind the FBO to the 2nd texture unit

		
		for( int i = 0; i < feats.size; i++ )
		{
			Feature feat = feats.get( i );
			feat.draw(screen);
		}
//		drawVisibility( screen, fbo, playerCenter );
		
		for( int i = 0, size = monsters.size; i < size; i++ )
		{
			BaseMonster m = monsters.get(i);
			m.draw(screen);
		}
		
		for( int i = 0, size = resources.size; i < size ; i++ )
		{
			AnimateEntity p = resources.get( i );
			p.draw( screen );
		}

		
		for( int i = 0, size = systems.size; i < size ; i++ )
		{
			ExplosionInterface explo = systems.get( i );
			explo.draw( screen );
		}
		
		screen.worldBatch.end();
		


		if( DebugHelper.VISIB_DEBUG ) {
			renderVisibilityDebug2();
		}
		

	}
	
	private void renderVisibilityDebug2() 
	{
		G.shapeRenderer.begin(ShapeType.Line);
		
		G.shapeRenderer.setColor(0.999f, 0.999f, 0.999f, 0.499f);
		
		List<Edge> triangles = visib.edges;
		final int size = triangles.size();
		for( int i = 0; i < size; i++ )
		{
			final Edge t = triangles.get( i );
			
			if( t.isBoundary() ) 
			{
				G.shapeRenderer.setColor(0.999f, 0.999f, 0.999f, 0.899f);
			}
			else 
			{
				G.shapeRenderer.setColor(0.399f, 0.399f, 0.999f, 0.899f);
			}

			final EndPoint e0 = t.getEndPoint1();
			final float ax = e0.p.x;
			final float ay = e0.p.y;
			
			final EndPoint e1 = t.getEndPoint2();
			final float bx = e1.p.x;
			final float by = e1.p.y;
						
			G.shapeRenderer.line(ax, ay, bx, by);
		}

		G.shapeRenderer.end();
	}
	
	
	public void drawMiniMap( Screen screen )
	{
		G.shapeRenderer.begin(ShapeType.Filled);
		G.shapeRenderer.setColor(0.6f, 0.6f, 0.6f, 0.5f);
		for( int i = 0; i < feats.size; i++ )
		{
			Feature feat = feats.get( i );
			
			float xpos = feat.getXpos();
			float ypos = feat.getYpos();
			float width = feat.getWidth();
			float height = feat.getHeight();
			
			G.shapeRenderer.rect(xpos, ypos, width, height);
		}
		
		
		G.shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
		for( int i = 0, size = monsters.size; i < size; i++ )
		{
			BaseMonster m = monsters.get(i);
			Vector2 pos = m.getBody().getWorldCenter();
			
			if( m instanceof ZipperMonster )
			{
				G.shapeRenderer.setColor(0.999f, 0.5f, 0.3f, 1.0f);
				G.shapeRenderer.circle( pos.x, pos.y, 0.5f, 5);
			}
			else
			{
				G.shapeRenderer.setColor(0.999f, 0.0f, 0.0f, 1.0f);
				G.shapeRenderer.circle( pos.x, pos.y, 1.0f, 5);
			}
				
		}

		
		
		G.shapeRenderer.end();
		
	}
	
	
	private PolygonSprite poly;
	private PolygonSpriteBatch polyBatch;
	private Texture textureSolid;
	
	private void drawVisibility2( Screen screen, FrameBuffer fb )
	{
		Vector2 playerCenter = Player.INSTANCE.getWorldCenter();
		VisibilityPoly visiPoly = visib.getVisibPoly();
		
//		Array<Vector2> points = visiPoly.getTriEndPoints();
	}
	
	private void drawVisibility( Screen screen, FrameBuffer fb, Vector2 playerCenter )
	{
		
		VisibilityPoly visiPoly = visib.getVisibPoly();
		
		// these are the triangle endpoints
		Array<Vector2> points = visiPoly.getTriEndPoints();

		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		G.shapeRenderer.begin(ShapeType.Filled);

//		Matrix4 mat = new Matrix4();
//		mat.setToTranslation(0.0f, 1.5f, 0.0f);
//		G.shapeRenderer.setTransformMatrix(mat);
		G.shapeRenderer.setColor(0.99975f, 0.99975f, 0.99975f, 0.9999f);
		
		for( int i = 0; i < points.size; i = i + 2 )
		{
			final Vector2 p1 = points.get( i );
			final Vector2 p2 = points.get( i + 1 );
			G.shapeRenderer.triangle(playerCenter.x, playerCenter.y, p1.x, p1.y, p2.x, p2.y);
		}
		
		G.shapeRenderer.end();
		
		
	}

	@Override
	public void tick(Screen screen, float delta)
	{
		for( int i = 0; i < feats.size; i++ )
		{
			Feature feat = feats.get( i );
			feat.tick(screen, delta);
		}
		
		for( int i = 0, size = resources.size; i < size ; i++ )
		{
			AnimateEntity p = resources.get( i );
			p.tick( delta );
		}
		
		for( int i = 0, size = systems.size; i < size ; i++ )
		{
			ExplosionInterface p = systems.get( i );
			p.tick(delta);
		}

		super.monsterTick( delta );
		
		cleanup();
	}

}
