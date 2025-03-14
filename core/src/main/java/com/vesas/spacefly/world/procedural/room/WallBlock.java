package com.vesas.spacefly.world.procedural.room;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.screen.GameScreen;
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
	
	private float xpos;
	private float ypos;
	private int blocks;
	private float rotation;
	
	/**
	 * Blocks have 0.5 world unit thickness. 
	 * @param blocks defines how many 0.5 units the block is.
	 */
	public WallBlock( int blox ) {
		this.blocks = blox;
	}

	/**
	 * Initializes the block with the bottom left as the origin of this block.
	 */
	public void initBottomLeft( float originx, float originy, float rotate, BodyBuilder bodyBuilder) {
		init( originx, originy, rotate, Origin.ORIGIN_BOTTOM_LEFT, bodyBuilder );
	}

	/**
	 * Initializes the block with the right right as the origin of this block.
	 */
	public void initTopRight( float originx, float originy, float rotate, BodyBuilder bodyBuilder) {
		init( originx, originy, rotate, Origin.ORIGIN_TOP_RIGHT, bodyBuilder );
	}

	/**
	 * Initializes the block with the top left as the origin of this block.
	 */
	public void initTopLeft( float originx, float originy, float rotate, BodyBuilder bodyBuilder) {
		init( originx, originy, rotate, Origin.ORIGIN_TOP_LEFT, bodyBuilder );
	}
	
	/**
	 * Initializes the block with the given origin.
	 */
	private void init( float originx, float originy, float rotate, Origin origin, BodyBuilder bodyBuilder)
	{
		/*
		 * These are the unit measurements in world space "blocks"
		 */
		Vector2 right = new Vector2(0.5f, 0f);
		right.rotateDeg( rotate );
		
		Vector2 up = new Vector2(0f, 0.5f);
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
		setUpBody(right, up, bodyBuilder);
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

	private void setUpBody(Vector2 right, Vector2 up, BodyBuilder bodyBuilder) {

		float [] v = {

			// Bottom left
			xpos, ypos,

			// Bottom right
			xpos + right.x * blocks,
			ypos + right.y * blocks,

			// Top right
			xpos + right.x * blocks + up.x,
			ypos + right.y * blocks + up.y,

			// Top left
			xpos + up.x,
			ypos + up.y
		};

		body = bodyBuilder
			.setPosition( 0 , 0 )
			.polygon( v )
			.construct();
		
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
