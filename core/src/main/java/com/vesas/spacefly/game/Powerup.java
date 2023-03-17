package com.vesas.spacefly.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.world.procedural.GenSeed;

public class Powerup implements AnimateEntity
{
	protected Body body;
	
	private int type = 0;
	
	public static int HEAL = 0;
	public static int AMMO1 = 1;
	public static int ADD_HEALTH = 2;
	public static int ADD_FIRERATE = 3;
	
	private float starRot = 0.0f;
	private float starEase = 0.0f;
	
	private boolean starStarted = false;
	
	public Powerup( float x, float y, int paramtype )
	{
		this.type = paramtype;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;	
		bodyDef.position.set( x,  y);
		
		body = Box2DWorld.world.createBody(bodyDef);

		PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(0.25f , 0.25f );
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polyShape;
		fixtureDef.density = 0.515f; 
		fixtureDef.friction = 0.0001f;
		fixtureDef.restitution = 0.65f;
		
		body.createFixture(fixtureDef);
		
		body.setAwake(true);
		body.setActive( true );
		
		body.setLinearDamping(0.2f);
		body.setAngularDamping(0.2f);
		
		body.setUserData( this );
		
		polyShape.dispose();
		
		
	}
	
	public int getType()
	{
		return type;
	}
	
	@Override
	public Body getBody()
	{
		return body;
	}

	@Override
	public void tick( float delta )
	{
		
		
		if( !starStarted )
		{
			if( GenSeed.random.nextFloat() < 0.015f )
			{
				starStarted = true;
			}
		}
		
		if( starStarted )
		{
			starRot += delta;
			starEase += (delta * 2.5f) ;
		}	
		
	}

	@Override
	public void draw(Screen screen)
	{
		Vector2 pos = body.getPosition();
		
		Sprite sprite = G.spice[ type ];
		
		sprite.setOriginCenter();
		sprite.setSize(0.55f, 0.55f);
		
		float bodyAngleInDegrees = body.getAngle() * Util.RADTODEG - 90.0f;

		sprite.setRotation(bodyAngleInDegrees);

		sprite.setPosition(pos.x-sprite.getWidth()*0.5f, pos.y-sprite.getHeight()* 0.5f);
		sprite.draw(screen.worldBatch);
		
		Sprite star = G.effects[3];
		
		Interpolation interIn = Interpolation.pow4In;
		Interpolation interOut = Interpolation.pow4Out;
		
		if( starStarted )
		{
			if( starEase > 2.0f )
			{
				starStarted = false;
				starEase = 0.0f;
			}
				
			
			float val = 0.0f;
			
			if( starEase > 1.0f )
				val = interIn.apply( 2.0f- starEase);
			else
				val = interIn.apply(starEase);
			
		
			star.setSize( val * 0.51f, val * 0.51f );
			star.setOriginCenter();
			star.setColor(0.99f, 0.99f, 0.99f, 1.0f );
			
			star.setRotation(starRot*275.7f);
			star.setPosition(pos.x-sprite.getWidth() * 0.25f - star.getWidth()*0.5f, pos.y-sprite.getHeight() * 0.3f - star.getHeight()* 0.5f);
			
			star.draw( screen.worldBatch );
		}
	}

	@Override
	public void destroy()
	{
		Box2DWorld.safeDestroyBody(this.body);
		
	}

}
