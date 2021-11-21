package com.vesas.spacefly.box2d;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;

public class BodyBuilder
{
	private Shape shape;
	
	private BodyType bodyType;
	private float linearDamping = 0.0f;
	private float angularDamping = 0.0f;
	private float density = 0.0f;
	private float friction = 0.0f;
	private float restitution = 0.0f;
	private short filterCategoryBits = -1;
	private short filterMaskBits = -1;
	private float xpos;
	private float ypos;
	private float linearVelX = 0.0f;
	private float linearVelY = 0.0f;
	
	private boolean bullet = false;

	private Object userData;
	
	private boolean isSensor = false;
	
	private static BodyBuilder INSTANCE = new BodyBuilder();
	
	public static BodyBuilder getInstance()
	{
		INSTANCE.init();
		return INSTANCE;
	}
	
	public BodyBuilder circle( float radius )
	{
		shape = new CircleShape();
		shape.setRadius( radius );
		return INSTANCE;
	}
	
	public BodyBuilder polybox( float xlen, float ylen)
	{
		shape = new PolygonShape();
		((PolygonShape)shape).setAsBox( xlen, ylen );
		
		return INSTANCE;
	}
	
	public BodyBuilder polygon( float [] vertices )
	{
		shape = new PolygonShape();
		((PolygonShape)shape).set(vertices);
		
		return INSTANCE;
	}
	
	public BodyBuilder chainLoop( float [] vertices )
	{
		shape = new ChainShape();
		((ChainShape)shape).createLoop(vertices);
		
		return INSTANCE;
	}
	
	public BodyBuilder chain( float [] vertices )
	{
		shape = new ChainShape();
		((ChainShape)shape).createChain(vertices);
		
		return INSTANCE;
	}
	
	public BodyBuilder setDensity( float density )
	{
		this.density = density;
		
		return INSTANCE;
	}
	
	public BodyBuilder setFriction( float friction )
	{
		this.friction = friction;
		
		return INSTANCE;
	}
	
	public BodyBuilder setRestitution( float restitution )
	{
		this.restitution = restitution;
		return INSTANCE;
	}
	
	
	public BodyBuilder setBodyType( BodyType bodyType )
	{
		this.bodyType = bodyType;
		return INSTANCE;
	}
	
	public BodyBuilder setPosition( float xpos, float ypos )
	{
		this.xpos = xpos;
		this.ypos = ypos;
		return INSTANCE;
	}
	
	public BodyBuilder setLinearDamping( float val )
	{
		linearDamping = val;
		return INSTANCE;
	}
	
	public BodyBuilder setAngularDamping( float val )
	{
		angularDamping = val;
		return INSTANCE;
	}
	
	public BodyBuilder setUserdata( Object userData )
	{
		this.userData = userData;
		return INSTANCE;
	}
	
	public BodyBuilder isSensor( boolean val )
	{
		this.isSensor = val;
		return INSTANCE;
	}
	
	public BodyBuilder setFilterCategoryBits( short bits )
	{
		filterCategoryBits = bits;
		return INSTANCE;
	}
	
	public BodyBuilder setFilterMaskBits( short bits )
	{
		filterMaskBits = bits;
		return INSTANCE;
	}

	public BodyBuilder setLinearVelocity( float dirx, float diry )
	{
		linearVelX = dirx;
		linearVelY = diry;
		return INSTANCE;
	}
	
	public BodyBuilder isBullet( boolean val )
	{
		bullet = val;
		return INSTANCE;
	}
	
	
	public Body construct()
	{
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = this.density; 
		fixtureDef.friction = this.friction;
		fixtureDef.restitution = this.restitution;
		fixtureDef.isSensor = this.isSensor;
		
		if( filterCategoryBits > -1 )
		{
			fixtureDef.filter.categoryBits = filterCategoryBits;
		}
		
		if( filterMaskBits > -1 )
		{
			fixtureDef.filter.maskBits = filterMaskBits;
		}
		
		
		BodyDef bodyDef = new BodyDef();
		
		bodyDef.position.set(xpos,ypos );
		
		if( this.bodyType == null )
			bodyDef.type = BodyType.StaticBody;
		else
			bodyDef.type = this.bodyType;
		
		Body body = Box2DWorld.world.createBody(bodyDef);
		
		body.setAwake(true);
		body.setActive( true );
		body.setBullet( bullet );
		body.setLinearDamping( this.linearDamping );
		body.setAngularDamping( this.angularDamping );
		body.setUserData( this.userData );
		body.setLinearVelocity(linearVelX, linearVelY);
		
		body.createFixture(fixtureDef);
		
		shape.dispose();
		
		init();
		
		return body;
	}
	
	private void init()
	{
		bodyType = null;
		shape = null;
		userData = null;
		bullet = false;
		
		linearDamping = 0.0f;
		angularDamping = 0.0f;
		
		filterCategoryBits = -1;
		filterMaskBits = -1;
		
		density = 0.0f;
		friction = 0.0f;
		restitution = 0.0f;
		
		isSensor = false;
		
		linearVelX = 0.0f;
		linearVelY = 0.0f;
		
		xpos = 0.0f;
		ypos = 0.0f;
	}
}
