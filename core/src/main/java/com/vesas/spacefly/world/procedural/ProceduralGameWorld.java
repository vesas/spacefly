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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.DebugHelper;
import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.game.AnimateEntity;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.monster.Monster;
import com.vesas.spacefly.monster.ZipperMonster;
import com.vesas.spacefly.particles.ExplosionInterface;
import com.vesas.spacefly.visibility.Edge;
import com.vesas.spacefly.visibility.EndPoint;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.visibility.VisibilityPoly;
import com.vesas.spacefly.world.AbstractGameWorld;

import util.FrameTime;

public class ProceduralGameWorld extends AbstractGameWorld
{
	private Array<Feature> feats;
	
	private Visibility visib;

	private ShaderProgram visibShader;
	
	private FrameBuffer fbo;

	@Override
	public void init()
	{
		feats = new Array<Feature>();
		
		visib = new Visibility();
		
		visib.startLoad();
		WorldGen gen = new WorldGen( this,visib );
		gen.setFirstRoomCenter(Player.INSTANCE.getWorldCenter());
		
		feats = gen.generate(); 
		
		visib.finishLoad();

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		fbo = new FrameBuffer(Format.RGBA8888, (int)width, (int)height, false);
		
		final String vertexShaderString = Gdx.files.local("data/vertexShader.glsl").readString();
		final String visibPixelShaderString = Gdx.files.local("data/visibShader.glsl").readString();
		visibShader = new ShaderProgram(vertexShaderString, visibPixelShaderString);

		visibShader.bind();
		visibShader.setUniformf("ambientColor", 0.15f, 0.15f, 0.15f, 0.25f);
		
		Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888 );
		pixmap.setColor(1.0f, 1.0f,1.0f,1.0f);
		pixmap.fill();
		
		textureSolid = new Texture(pixmap);
		
	}
	
	@Override
	public void draw(GameScreen screen)
	{

		Vector2 playerCenter = Player.INSTANCE.getWorldCenter();
		
		float screenHeight = screen.viewport.getScreenHeight();
		float screenWidth = screen.viewport.getScreenWidth();
		
		visibShader.bind();
		visibShader.setUniformf("ambientColor", 0.5f, 0.5f, 0.7f, 0.519f);
		visibShader.setUniformi("u_lightmap", 1);
		visibShader.setUniformf("resolution", screenWidth, screenHeight);
		
		// defaultShader.setUniformf("viewcenc", screenWidth*0.5f, screenHeight*0.5f);
		
		
		// screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		// Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		// Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		// Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);

		screen.worldBatch.end();
		
		//draw the light to the FBO
		fbo.begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		visib.setLightLocation( playerCenter.x, playerCenter.y);
		visib.sweep();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA );

		drawVisibility( screen, fbo, playerCenter );

		Gdx.gl.glDisable(GL20.GL_BLEND);
		// screen.worldBatch.draw(light, playerCenter.x - lightSize*0.5f , playerCenter.y - lightSize*0.5f, lightSize, lightSize);
		
		fbo.end();
		
		fbo.getColorBufferTexture().bind(1);
		textureSolid.bind(0);
		
		screen.worldBatch.begin();
		
//		fbo.getColorBufferTexture().bind(1); //this is important! bind the FBO to the 2nd texture unit

		screen.worldBatch.setShader(null);
		
		long startNano = System.nanoTime();
		
		for( int i = 0; i < feats.size; i++ )
		{
			Feature feat = feats.get( i );
			feat.draw(screen.worldBatch);
		}
		long endNano = System.nanoTime();
		FrameTime.features = endNano - startNano;

		screen.worldBatch.setShader(visibShader);

		startNano = System.nanoTime();
		for( int i = 0; i < feats.size; i++ )
		{
			Feature feat = feats.get( i );
			feat.drawWithVisibility(screen);
		}
		endNano = System.nanoTime();
		FrameTime.featvisib = endNano - startNano;
	
		// drawVisibility( screen, fbo, playerCenter );
		
		for( int i = 0, size = monsters.size; i < size; i++ )
		{
			Monster m = monsters.get(i);
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
			renderVisibilityDebug2(screen);
		}
		
	}
	
	private void renderVisibilityDebug2(GameScreen screen) 
	{
		
		G.shapeRenderer.begin(ShapeType.Line);
		
		G.shapeRenderer.setColor(0.999f, 0.999f, 0.999f, 0.499f);
		
		List<Edge> edges = visib.edges;
		final int size = edges.size();
		for( int i = 0; i < size; i++ )
		{
			final Edge edge = edges.get( i );
			
			if( edge.isBoundary() ) 
			{
				G.shapeRenderer.setColor(0.999f, 0.999f, 0.999f, 0.899f);
			}
			else 
			{
				float val = edge.procRank;
				val = val * 0.5f;
				if(val > 1.0f) {
					val = 1.0f;
				}

				G.shapeRenderer.setColor(0.0f + val, 0.299f, 0.299f, 0.899f);
			}

			final EndPoint e0 = edge.getEndPoint1();
			final float ax = e0.p.x;
			final float ay = e0.p.y;
			
			final EndPoint e1 = edge.getEndPoint2();
			final float bx = e1.p.x;
			final float by = e1.p.y;
						
			G.shapeRenderer.line(ax, ay, bx, by);

		}

		G.shapeRenderer.end();

		/*
		screen.worldBatch.begin();
		G.font.draw(screen.worldBatch, "S", 123 - 16, 123 + 15);
		screen.worldBatch.end();
		*/
	}
	
	
	public void drawMiniMap( GameScreen screen )
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
			Monster m = monsters.get(i);
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
	
	private void drawVisibility( GameScreen screen, FrameBuffer fb, Vector2 playerCenter )
	{
		VisibilityPoly visiPoly = visib.getVisibPoly();
		
		// these are the triangle endpoints
		final Array<Vector2> points = visiPoly.getTriEndPoints();
		
		G.shapeRenderer.begin(ShapeType.Filled);

//		Matrix4 mat = new Matrix4();
//		mat.setToTranslation(0.0f, 1.5f, 0.0f);
//		G.shapeRenderer.setTransformMatrix(mat);
		G.shapeRenderer.setColor(0.999f, 0.999f, 0.999f, 1.0f);

		for( int i = 0; i < points.size; i = i + 2 )
		{
			final Vector2 p1 = points.get( i );
			final Vector2 p2 = points.get( i + 1 );
			G.shapeRenderer.triangle(playerCenter.x, playerCenter.y, p1.x, p1.y, p2.x, p2.y);
		}
		
		G.shapeRenderer.end();
	}

	@Override
	public void tick(GameScreen screen, float delta)
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
