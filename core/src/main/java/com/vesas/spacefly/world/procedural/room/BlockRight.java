package com.vesas.spacefly.world.procedural.room;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.world.procedural.FeatureBlock;

public class BlockRight implements FeatureBlock
{
	private Body body;
	private Polygon poly;

	private int DONT_DRAW;
	
	private Sprite sprite;
	
	private float xpos, ypos;
	private int blocks;
	
	public BlockRight( int blox )
	{
		this.blocks = blox;
	}
	
	public void init( float xpos, float ypos, float rotate, BodyBuilder bodyBuilder)
	{
		sprite = G.getAtlas().createSprite("edgeA" + blocks );
		sprite.setSize(blocks * 0.5f ,0.5f);
		sprite.setOrigin( 0, 0);
		sprite.rotate(rotate);
		
		this.xpos = xpos;
		this.ypos = ypos;

		Vector2 right = new Vector2();
		right.x = 0.5f;
		right.y = 0f;
		
		Vector2 up = new Vector2();
		up.x = 0f;
		up.y = 0.5f;
		
		right = right.rotateDeg( rotate );
		up = up.rotateDeg( rotate );
		
		float[] v = new float[8];
		
		v[0] = xpos;
		v[1] = ypos;
		
		v[2] = xpos + right.x * blocks;
		v[3] = ypos + right.y * blocks;
		
		v[4] = xpos + right.x * blocks + up.x;
		v[5] = ypos + right.y * blocks + up.y;
		
		v[6] = xpos + up.x;
		v[7] = ypos + up.y;
		
		bodyBuilder.setPosition( 0 , 0 );
		bodyBuilder.polygon( v );
		
		body = bodyBuilder.construct();
	}
	
	public void draw(Batch batch)
	{
		sprite.setPosition( xpos , ypos);
		sprite.draw(batch);
	}

	public void tick(GameScreen screen, float delta)
	{
	}
}
