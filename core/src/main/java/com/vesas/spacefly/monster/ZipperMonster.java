package com.vesas.spacefly.monster;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.Bullet;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.G.MonsterType;
import com.vesas.spacefly.game.Util;
import com.vesas.spacefly.screen.GameScreen;

public class ZipperMonster extends Monster
{
	enum IMPULSESTATE
	{
		RESTING,
		PREPARING,
		PUSHING;
	}
	
	private IMPULSESTATE state = IMPULSESTATE.RESTING;
	
	private float cooldown = 0 + random.nextFloat();
	
	private Vector2 targetDir = new Vector2(0.0f, 1.0f);
	private Vector2 dir = new Vector2(0.0f, 1.0f);

	private boolean turning = false;
	
	private float actionTime = 0.0f;
	private static float PREPARING_MAX_TIME = 0.3f;
	private static float PUSHING_MAX_TIME = 0.15f;
	
	private float targetModifier = 1.0f;
	private float currentModifier = 1.0f;
	
	private Vector2 targetPoint = new Vector2();
	
	private ZipperCloud cloud;
	
	public void addCloud( ZipperCloud cl )
	{
		cloud = cl;
		cloud.addMonster( this );
	}
	
	public ZipperMonster(float posx, float posy, int amount, BodyBuilder bodyBuilder )
	{
		cooldown = 0 + random.nextFloat() * 0.01f;

		body = bodyBuilder.setBodyType(BodyType.DynamicBody)
			.setPosition(posx, posy)
			.circle(0.12f)
			.setDensity(1.015f)
			.setFriction(0.000001f)
			.setRestitution(0.75f)
			.setFilterCategoryBits((short)16)
			.setFilterMaskBits((short)23)
			.setLinearDamping(0.60f)
			.setAngularDamping(0.80f)
			.setUserdata(this)
			.construct();

		targetDir.setAngleRad(body.getAngle());
		dir.setAngleRad(body.getAngle());

		setHealth(1);

	}
	
	@Override
	public void getHit(Bullet b)
	{
		super.getHit(b);
		
		if( this.isDead() )
		{
			cloud.remove( this );
		}
	}

	@Override
	public void tick( GameScreen screen, float delta )
	{
		brain.tick( delta );

		if (body == null)
			return;
		

		cooldown -= delta;

		if (cooldown <= 0.0f)
		{
			//cooldown = 0.0015f;
			cooldown = 0.10f + cooldown;
			
			// Vector2 vel = body.getLinearVelocity();
			/*
			 * if( vel.len() > MAX_VELOCITY ) { vel.nor(); vel.mul( MAX_VELOCITY
			 * ); body.setLinearVelocity( vel ); }
			 */
			
			
			if( state == IMPULSESTATE.RESTING && random.nextInt(100) < 50)
			{
				Vector2 tmp = cloud.getCenter();
				
				targetPoint.x = tmp.x;
				targetPoint.y = tmp.y;
				
				Vector2 thisCenter = body.getWorldCenter();
				
				targetDir.x = targetPoint.x - thisCenter.x;
				targetDir.y = targetPoint.y - thisCenter.y;
				targetDir.nor();
				
				if( random.nextInt( 100 ) < 15 )
					turnToRandomTarget();
				else
					turnToTarget( delta );
				
				turning = true;
			}
			
			if( turning && state == IMPULSESTATE.RESTING )
			{
				float turnamount = this.getBody().getAngularVelocity();
				
				if( turnamount < 0.05f )
				{
					turning = false;
					
					state = IMPULSESTATE.PREPARING;
					actionTime = 0.0f;
				}
			}
			
			if( state == IMPULSESTATE.PREPARING )
			{
				actionTime += delta;
				
				targetModifier = ( 1.0f - actionTime ) * 1.4f - 0.35f;
				
				if( actionTime > PREPARING_MAX_TIME )
				{
					state = IMPULSESTATE.PUSHING;
					actionTime = 0.0f;
				}
			}
			if( state == IMPULSESTATE.PUSHING )
			{
				actionTime += delta;
				
				targetModifier = ( 1.0f + actionTime ) * 1.4f - 0.25f;
				
				dir.setAngleRad(body.getAngle());
				dir.nor();
				dir.scl(random.nextFloat() * 0.14f + ( 0.15f + PUSHING_MAX_TIME - actionTime ) );

				body.applyForceToCenter(dir, true);
				
				if( actionTime > PUSHING_MAX_TIME )
				{
					state = IMPULSESTATE.RESTING;
					actionTime = 0.0f;
				}
			}
		}

	}
	
	private void turnToTarget( float delta )
	{
		float targetAngle = targetDir.angleDeg();
		float bodyAngle = body.getAngle() * Util.RADTODEG;

		float diff = Util.angleDiff(targetAngle, bodyAngle);

		float scaling = 2.10f * delta;

		float absDiff = Math.abs(diff);

		if (absDiff < 140.0)
			scaling = scaling * 0.99f;

		if (absDiff < 65.0)
			scaling = scaling * 0.8f;

		if (absDiff < 35.0)
			scaling = scaling * 0.6f;

		if (absDiff < 15.0)
			scaling = scaling * 0.4f;

		if (absDiff < 5.0)
			scaling = scaling * 0.2f;

		if (diff < 0)
		{
			body.applyTorque(-scaling, true );
		} else
		{
			body.applyTorque(scaling, true );
		}
		
	}

	private void turnToRandomTarget()
	{
		float randomAmount = random.nextFloat() * 0.05f;

		if ( random.nextBoolean() )
		{
			body.applyTorque(-randomAmount, true );
		} else
		{
			body.applyTorque(randomAmount, true );
		}
		
	}
	

	@Override
	public void draw(GameScreen screen)
	{
		if (body == null)
			return;
		
//		cloud.draw( screen );

		final Vector2 pos = body.getPosition();

//		if (screen.outSideScreen(pos, 16))
//			return;

		final Sprite sprite = G.monsters.get(MonsterType.ZIPPER);
		
		
		if( state == IMPULSESTATE.PUSHING )
		{
			if( actionTime < PUSHING_MAX_TIME * 0.5f ) 
			{
				currentModifier = currentModifier + (targetModifier - currentModifier) * 0.05f;
			}
			else
			{
				currentModifier = currentModifier + (1.0f - currentModifier) * 0.05f;
			}
			
			
			sprite.setSize(0.25f * (1.3f - currentModifier * 0.5f ), 0.25f * currentModifier );
		}
		else if( state == IMPULSESTATE.PREPARING )
		{
			currentModifier = currentModifier + (targetModifier - currentModifier) * 0.03f;	
			
			sprite.setSize(0.25f * (1.3f - currentModifier * 0.5f ), 0.25f * currentModifier );
		}
		else
		{
			sprite.setSize(0.25f * (1.3f - currentModifier * 0.5f ), 0.25f * currentModifier);	
		}
		
		sprite.setOriginCenter();
		
		sprite.setPosition(pos.x - sprite.getWidth()*0.5f, pos.y - sprite.getHeight()*0.5f);

		float angle = body.getAngle();

		sprite.setRotation(angle * Util.RADTODEG - 90.0f);

		sprite.draw(screen.worldBatch);

	}

}
