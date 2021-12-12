package com.vesas.spacefly.monster;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.Bullet;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Screen;
import com.vesas.spacefly.game.Util;

public class ZipperMonster extends Monster
{
	enum IMPULSE_STATE
	{
		RESTING,
		PREPARING,
		PUSHING;
	}
	
	private IMPULSE_STATE state = IMPULSE_STATE.RESTING;
	
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
	
	public ZipperMonster(float posx, float posy, int amount )
	{
		cooldown = 0 + random.nextFloat() * 0.01f;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(posx, posy);

		body = Box2DWorld.world.createBody(bodyDef);

		CircleShape shape = new CircleShape();
		shape.setRadius(0.12f);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 1.015f;
		fixtureDef.friction = 0.000001f;
		fixtureDef.restitution = 0.75f; // Make it bounce a little bit
		// fixtureDef.filter.groupIndex = Physics.GROUP_MONSTER;

		fixtureDef.filter.categoryBits = 16; // 1 wall, 2 player, 4
											 // playerbullet, 8 monsterbullet,
											 // 16 monster
		fixtureDef.filter.maskBits = 23;

		body.createFixture(fixtureDef);

		body.setAwake(true);
		body.setActive(true);

		body.setLinearDamping(0.60f);
		body.setAngularDamping(0.80f);

		// body.setTransform(body.getPosition().x, body.getPosition().y, 90.0f *
		// DEGREES_TO_RADIANS);

		body.setUserData(this);

		targetDir.setAngleRad(body.getAngle());
		dir.setAngleRad(body.getAngle());

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		shape.dispose();

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
	public void tick( float delta )
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
			
			
			if( state == IMPULSE_STATE.RESTING && random.nextInt(100) < 50)
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
			
			if( turning && state == IMPULSE_STATE.RESTING )
			{
				float turnamount = this.getBody().getAngularVelocity();
				
				if( turnamount < 0.05f )
				{
					turning = false;
					
					state = IMPULSE_STATE.PREPARING;
					actionTime = 0.0f;
				}
			}
			
			if( state == IMPULSE_STATE.PREPARING )
			{
				actionTime += delta;
				
				targetModifier = ( 1.0f - actionTime ) * 1.4f - 0.35f;
				
				if( actionTime > PREPARING_MAX_TIME )
				{
					state = IMPULSE_STATE.PUSHING;
					actionTime = 0.0f;
				}
			}
			if( state == IMPULSE_STATE.PUSHING )
			{
				actionTime += delta;
				
				targetModifier = ( 1.0f + actionTime ) * 1.4f - 0.25f;
				
				dir.setAngleRad(body.getAngle());
				dir.nor();
				dir.scl(random.nextFloat() * 0.14f + ( 0.15f + PUSHING_MAX_TIME - actionTime ) );

				body.applyForceToCenter(dir, true);
				
				if( actionTime > PUSHING_MAX_TIME )
				{
					state = IMPULSE_STATE.RESTING;
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
	public void draw(Screen screen)
	{
		if (body == null)
			return;
		
//		cloud.draw( screen );

		final Vector2 pos = body.getPosition();

//		if (screen.outSideScreen(pos, 16))
//			return;

		final Sprite sprite = G.monsters[1];
		
		
		
		if( state == IMPULSE_STATE.PUSHING )
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
		else if( state == IMPULSE_STATE.PREPARING )
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
