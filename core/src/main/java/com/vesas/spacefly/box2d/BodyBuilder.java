package com.vesas.spacefly.box2d;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class BodyBuilder
{
	private Shape shape;
	
	private BodyType bodyType;
	private short filterCategoryBits = -1;
	private short filterMaskBits = -1;
	
	private static class PhysicsProperties {
		float linearDamping = 0.0f;
		float angularDamping = 0.0f;
		float density = 0.0f;
		float friction = 0.0f;
		float restitution = 0.0f;
		boolean bullet = false;
	}

	private static class PositionProperties {
		float xpos = 0.0f;
		float ypos = 0.0f;
		float linearVelX = 0.0f;
		float linearVelY = 0.0f;
	}

	private PhysicsProperties physics = new PhysicsProperties();
	private PositionProperties position = new PositionProperties();
	
	private Object userData;

	private static BodyBuilder INSTANCE = new BodyBuilder();

	public static BodyBuilder getInstance() {
		INSTANCE.init();
		return INSTANCE;
	}
	
	private boolean isSensor = false;
	
	public BodyBuilder circle( float radius )
	{
		shape = new CircleShape();
		
		shape.setRadius( radius );
		return this;
	}
	
	public BodyBuilder polybox( float xlen, float ylen)
	{
		shape = new PolygonShape();
		((PolygonShape)shape).setAsBox( xlen, ylen );
		
		return this;
	}
	
	public BodyBuilder polygon( float [] vertices )
	{
		shape = new PolygonShape();
		((PolygonShape)shape).set(vertices);
		
		return this;
	}
	
	public BodyBuilder chainLoop( float [] vertices )
	{
		shape = new ChainShape();
		((ChainShape)shape).createLoop(vertices);
		
		return this;
	}
	
	public BodyBuilder chain( float [] vertices )
	{
		shape = new ChainShape();
		((ChainShape)shape).createChain(vertices);
		
		return this;
	}
	
	public BodyBuilder setDensity( float density )
	{
		physics.density = density;
		
		return this;
	}
	
	public BodyBuilder setFriction( float friction )
	{
		physics.friction = friction;
		
		return this;
	}
	
	public BodyBuilder setRestitution( float restitution )
	{
		physics.restitution = restitution;
		return this;
	}
	
	
	public BodyBuilder setBodyType( BodyType bodyType )
	{
		this.bodyType = bodyType;
		return this;
	}
	
	public BodyBuilder setPosition( float xpos, float ypos )
	{
		position.xpos = xpos;
		position.ypos = ypos;
		return this;
	}
	
	public BodyBuilder setLinearDamping( float val )
	{
		physics.linearDamping = val;
		return this;
	}
	
	public BodyBuilder setAngularDamping( float val )
	{
		physics.angularDamping = val;
		return this;
	}
	
	public BodyBuilder setUserdata( Object userData )
	{
		this.userData = userData;
		return this;
	}
	
	public BodyBuilder isSensor( boolean val )
	{
		this.isSensor = val;
		return this;
	}
	
	public BodyBuilder setFilterCategoryBits( short bits )
	{
		filterCategoryBits = bits;
		return this;
	}
	
	public BodyBuilder setFilterMaskBits( short bits )
	{
		filterMaskBits = bits;
		return this;
	}

	public BodyBuilder setLinearVelocity( float dirx, float diry )
	{
		position.linearVelX = dirx;
		position.linearVelY = diry;
		return this;
	}
	
	public BodyBuilder isBullet( boolean val )
	{
		physics.bullet = val;
		return this;
	}
	
	
	public Body construct()
	{
		FixtureDef fixtureDef = createFixtureDef();
		
		BodyDef bodyDef = new BodyDef();
		
		bodyDef.position.set(position.xpos, position.ypos );
		
		if( this.bodyType == null )
			bodyDef.type = BodyType.StaticBody;
		else
			bodyDef.type = this.bodyType;
		
		Body body = Box2DWorld.world.createBody(bodyDef);
		
		body.setAwake(true);
		body.setActive( true );
		body.setBullet( physics.bullet );
		body.setLinearDamping( physics.linearDamping );
		body.setAngularDamping( physics.angularDamping );
		body.setUserData( this.userData );
		body.setLinearVelocity(position.linearVelX, position.linearVelY);
		
		body.createFixture(fixtureDef);
		
		shape.dispose();
		
		init();
		
		return body;
	}
	
	private FixtureDef createFixtureDef() {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = physics.density;
		fixtureDef.friction = physics.friction;
		fixtureDef.restitution = physics.restitution;
		fixtureDef.isSensor = this.isSensor;
		
		if (filterCategoryBits > -1) {
			fixtureDef.filter.categoryBits = filterCategoryBits;
		}
		
		if (filterMaskBits > -1) {
			fixtureDef.filter.maskBits = filterMaskBits;
		}
		
		return fixtureDef;
	}
	
	private void init()
	{
		bodyType = null;
		shape = null;
		userData = null;
		physics.bullet = false;
		
		physics.linearDamping = 0.0f;
		physics.angularDamping = 0.0f;
		
		filterCategoryBits = -1;
		filterMaskBits = -1;
		
		physics.density = 0.0f;
		physics.friction = 0.0f;
		physics.restitution = 0.0f;
		
		isSensor = false;
		
		position.linearVelX = 0.0f;
		position.linearVelY = 0.0f;
		
		position.xpos = 0.0f;
		position.ypos = 0.0f;
	}
}
