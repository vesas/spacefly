package com.vesas.spacefly.world.procedural.room;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Screen;

public class Block10Right implements RoomBlock
{
	private Body body;
	private Polygon poly;
	
	static private Sprite sprite;
	
	private float xpos, ypos;
	
	public Block10Right()
	{
		
	}
	
	public void init( float xpos, float ypos, float rotate)
	{
		sprite = G.getAtlas().createSprite("edgeA10");
		sprite.setSize(5f,0.5f);
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
		
		v[2] = xpos + right.x * 10;
		v[3] = ypos + right.y * 10;
		
		v[4] = xpos + right.x * 10 + up.x;
		v[5] = ypos + right.y * 10 + up.y;
		
		v[6] = xpos + up.x;
		v[7] = ypos + up.y;
		
		BodyBuilder builder = BodyBuilder.getInstance();
		
		builder.setPosition( 0 , 0 );
		builder.polygon( v );
		builder.setBodyType(BodyType.StaticBody );
		
		body = builder.construct();
		
//		EarClippingTriangulator ect = new EarClippingTriangulator();
//		triIndices = ect.computeTriangles(vertices);
	}
	
	public void draw(Screen screen)
	{
		
		sprite.setPosition( xpos , ypos);
		sprite.draw(screen.worldBatch);
		
	}

	public void tick(Screen screen, float delta)
	{
	}
	
	
	
	
}
