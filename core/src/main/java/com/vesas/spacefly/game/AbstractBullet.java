package com.vesas.spacefly.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.TimeUtils;
import com.vesas.spacefly.box2d.BodyBuilder;

public abstract class AbstractBullet
{
	public Body body;
	
	public long creationTime;
	
	public void createBody( float posx, float posy, float dirx, float diry, short category, short mask )
	{
		creationTime = TimeUtils.millis();
		
		BodyBuilder builder = BodyBuilder.getInstance();
		
		builder.circle(0.05f);
		builder.setDensity(0.8f).setFriction( 0.00001f).setRestitution( 0.85f);
		
		// 1 wall, 2 player, 4 playerbullet, 8 monsterbullet, 16 monster
		builder.setFilterCategoryBits( category );
		builder.setFilterMaskBits( mask );
		
		builder.setBodyType( BodyType.DynamicBody);
		
		builder.setPosition(posx, posy);
		
		builder.setLinearVelocity(dirx, diry);
		builder.setLinearDamping(0.000f);
		builder.isBullet( true );
		builder.setUserdata( this );
		
		this.body = builder.construct();
		
	}
	
	public void resetCreationTime()
	{
		creationTime = TimeUtils.millis();
	}
	
	public void deactivate()
	{
		body.setAwake( false );
		body.setActive( false );
	}
	
	public void activate()
	{
		body.setAwake( true );
		body.setActive( true );
	}
	
	abstract public void draw( SpriteBatch batch );
}
