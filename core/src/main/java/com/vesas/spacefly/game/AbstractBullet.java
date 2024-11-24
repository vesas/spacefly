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
	
	public AbstractBullet( float posx, float posy, float dirx, float diry, short category, short mask )
	{
		creationTime = TimeUtils.millis();
		
		body = BodyBuilder.getInstance()
			.circle(0.05f)
			.setDensity(0.8f)
			.setFriction( 0.00001f)
			.setRestitution( 0.85f)
			.setFilterCategoryBits( category )
			.setFilterMaskBits( mask )
			.setBodyType( BodyType.DynamicBody)
			.setPosition(posx, posy)
			.setLinearVelocity(dirx, diry)
			.setLinearDamping(0.0f)
			.isBullet( true )
			.setUserdata( this )
			.construct();
		
	}
	
	public void resetCreationTime()
	{
		creationTime = TimeUtils.millis();
	}
	
	public void setActive( boolean active )
	{
		body.setAwake( active );
		body.setActive( active );
	}
	
	abstract public void draw( SpriteBatch batch );
}
