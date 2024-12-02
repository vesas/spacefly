package com.vesas.spacefly.world.procedural.room;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.world.procedural.FeatureBlock;

public class BlockUp implements FeatureBlock
{
	private static float WORLD_TILE_SIZE = 0.5f;
	private Body body;
	private Polygon poly;
	
	private Sprite sprite;
	
	private float xpos, ypos;
	
	private int blocks;
	
	public BlockUp( int blox ) 
	{ 
		this.blocks = blox;	
	}
	
	public void init( float xpos, float ypos, float rotate)
	{
		sprite = G.getAtlas().createSprite("edgeA" + blocks );
		sprite.setSize(blocks * WORLD_TILE_SIZE, WORLD_TILE_SIZE);
		sprite.setOrigin( 0, 0);
		sprite.rotate(rotate+90);
		
		this.xpos = xpos;
		this.ypos = ypos;
		
		Vector2 right = new Vector2();
		right.x = WORLD_TILE_SIZE;
		right.y = 0f;
	
		Vector2 up = new Vector2();
		up.x = 0f;
		up.y = WORLD_TILE_SIZE;
		
		right = right.rotateDeg( rotate );
		up = up.rotateDeg( rotate );
		
		float[] v = new float[8];
		
		v[0] = xpos;
		v[1] = ypos;
		
		v[2] = xpos + right.x * 1;
		v[3] = ypos + right.y * 1;
		
		v[4] = xpos + right.x * 1 + up.x * blocks;
		v[5] = ypos + right.y * 1 + up.y * blocks;
		
		v[6] = xpos + up.x * blocks;
		v[7] = ypos + up.y * blocks;
		
		BodyBuilder builder = BodyBuilder.getInstance();
		
		builder.setPosition( 0 , 0 );
		builder.polygon( v );
		builder.setBodyType(BodyType.StaticBody );
		
		body = builder.construct();
		
	}
	
	public void draw(Batch batch)
	{
		sprite.setPosition( xpos + 0.5f , ypos);
		sprite.draw(batch);
	}

	public void tick(GameScreen screen, float delta)
	{
	}
}
