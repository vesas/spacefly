package com.vesas.spacefly.world.procedural.room;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Screen;

public class Block5Up implements RoomBlock
{
	private Body body;
	private Polygon poly;
	
	private Sprite sprite;
	
	private float xpos, ypos;
	
	public Block5Up()
	{
		
	}
	
	public void init( float xpos, float ypos, float rotate)
	{
		sprite = G.getAtlas().createSprite("edgeA5");
		sprite.setSize(2.5f,0.5f);
		sprite.setOrigin( 0, 0);

		sprite.rotate(rotate+90);
		
		this.xpos = xpos;
		this.ypos = ypos;
	
		Vector2 up = new Vector2();
		Vector2 right = new Vector2();
		right.x = 0.5f;
		right.y = 0f;
		
		up.x = 0f;
		up.y = 0.5f;
		
		right = right.rotateDeg( rotate );
		up = up.rotateDeg( rotate );
		
		float[] v = new float[8];
		
		v[0] = xpos;
		v[1] = ypos;
		
		v[2] = xpos + right.x * 1;
		v[3] = ypos + right.y * 1;
		
		v[4] = xpos + right.x * 1 + up.x * 5;
		v[5] = ypos + right.y * 1 + up.y * 5;
		
		v[6] = xpos + up.x * 5;
		v[7] = ypos + up.y * 5;
		
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
		sprite.setPosition( xpos + 0.5f , ypos);
		sprite.draw(screen.worldBatch);
		
	}

	public void tick(Screen screen, float delta)
	{
	}
	
	
	
	
}
