package com.vesas.spacefly.world.procedural.room;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.world.procedural.FeatureBlock;

/* 
 * Walls which have 0.5 world unit thickness
 * 
 * Contains physics body and texture
*/
public class WallBlock implements FeatureBlock
{

	static private enum Origin {
		ORIGIN_TOP_LEFT,
		ORIGIN_BOTTOM_LEFT,
		ORIGIN_TOP_RIGHT;
	}

	private Body body;
	private Polygon poly;

	private int DONT_DRAW;
	
	
	private float xpos;
	private float ypos;
	private float originx;
	private float originy;
	private int blocks;
	private float rotation;
	
	public WallBlock( int blox )
	{
		this.blocks = blox;
	}

	public void initBottomLeft( float originx, float originy, float rotate) {
		init( originx, originy, rotate, Origin.ORIGIN_BOTTOM_LEFT );
	}

	public void initTopRight( float originx, float originy, float rotate) {
		init( originx, originy, rotate, Origin.ORIGIN_TOP_RIGHT );
	}

	public void initTopLeft( float originx, float originy, float rotate) {
		init( originx, originy, rotate, Origin.ORIGIN_TOP_LEFT );
	}
	
	private void init( float originx, float originy, float rotate, Origin origin)
	{
		

		/*
		 * These are the unit measurements in world space "blocks"
		 */
		Vector2 right = new Vector2();
		right.x = 0.5f;
		right.y = 0f;
		right.rotateDeg( rotate );
		
		Vector2 up = new Vector2();
		up.x = 0f;
		up.y = 0.5f;
		up.rotateDeg( rotate );
		
		if(origin == Origin.ORIGIN_BOTTOM_LEFT) {
			this.xpos = originx;
			this.ypos = originy;
		}
		else if(origin == Origin.ORIGIN_TOP_LEFT) {
			this.xpos = originx - up.x;
			this.ypos = originy - up.y;
		}
		else {
			this.xpos = originx - right.x * blocks - up.x;
			this.ypos = originy - right.y * blocks - up.y;
		}

		setupTexture(rotate);
		setUpBody(right, up);
	}

	private void setupTexture(float rotate) {
		
		rotation = rotate;
		// texture

		

		/* */
		/*
		sprite = G.getAtlas().createSprite("edgeA" + blocks );
		sprite.setSize(blocks * 0.5f ,0.5f);
		sprite.setOrigin( 0, 0);
		sprite.rotate(rotate);
		 */
	}

	private void setUpBody(Vector2 right, Vector2 up) {

		float[] v = new float[8];

		// Bottom left
		v[0] = xpos;
		v[1] = ypos;

		// Bottom right
		v[2] = xpos + right.x * blocks;
		v[3] = ypos + right.y * blocks;

		// Top right
		v[4] = xpos + right.x * blocks + up.x;
		v[5] = ypos + right.y * blocks + up.y;

		// Top left
		v[6] = xpos + up.x;
		v[7] = ypos + up.y;
		
		BodyBuilder builder = BodyBuilder.getInstance();

		builder.setPosition( 0 , 0 );
		builder.polygon( v );
		builder.setBodyType(BodyType.StaticBody );
		
		body = builder.construct();
	}

	public void draw(Batch batch) {

		TextureRegion wallTex = G.walls[1];
		batch.draw(wallTex, xpos, ypos, 0f,0f,blocks * 0.5f, 0.5f,1.0f,1.0f,rotation);

		/** Draws a rectangle with the bottom left corner at x,y and stretching the region to cover the given width and height. */
		// public void draw (TextureRegion region, float x, float y, float width, float height);

		// public void draw (TextureRegion region, float x, float y, float originX, float originY, float width, float height,
		// float scaleX, float scaleY, float rotation);
		
	}

	public void tick(GameScreen screen, float delta) {
	}
}
