package com.vesas.spacefly.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.vesas.spacefly.box2d.Box2DWorld;

public class Spice implements AnimateEntity
{
	
	protected Body body;
	
	
	public Spice( float x, float y)
	{
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;	
		bodyDef.position.set( x,  y);
		
		body = Box2DWorld.world.createBody(bodyDef);

		PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(11, 11 );
		
		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polyShape;
		fixtureDef.density = 0.515f; 
		fixtureDef.friction = 0.0001f;
		fixtureDef.restitution = 0.95f; // Make it bounce a little bit
		//fixtureDef.filter.groupIndex = Physics.GROUP_MONSTER;
		
		//fixtureDef.filter.categoryBits = 16; // 1 wall, 2 player, 4 playerbullet, 8 monsterbullet, 16 monster
		//fixtureDef.filter.maskBits = 23;
		
		body.createFixture(fixtureDef);
		
		body.setAwake(true);
		body.setActive( true );
		
		body.setLinearDamping(0.1f);
		body.setAngularDamping(0.1f);
		
		//body.setTransform(body.getPosition().x, body.getPosition().y, 90.0f * DEGREES_TO_RADIANS);
		
		body.setUserData( this );
		
		polyShape.dispose();
		
		
	}
	
	@Override
	public Body getBody()
	{
		return body;
	}

	@Override
	public void tick( float delta )
	{
		
	}

	@Override
	public void draw(Screen screen)
	{
		Vector2 pos = body.getPosition();
		
		Sprite sprite = G.spice[0];
		
		float bodyAngleInDegrees = body.getAngle() * G.RADIANS_TO_DEGREES - 90.0f;

		sprite.setRotation(bodyAngleInDegrees);

		sprite.setPosition(pos.x-12, pos.y-12);
		
		sprite.draw(screen.worldBatch);
		
		
	}

	@Override
	public void destroy()
	{
		Box2DWorld.safeDestroyBody(this.body);
		
	}

}
