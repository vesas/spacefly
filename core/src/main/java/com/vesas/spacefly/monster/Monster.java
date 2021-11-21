package com.vesas.spacefly.monster;

import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.vesas.spacefly.box2d.Physics;
import com.vesas.spacefly.game.AnimateEntity;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.RayCastClosestCB;
import com.vesas.spacefly.game.Screen;
import com.vesas.spacefly.game.Util;

public class Monster extends BaseMonster implements AnimateEntity
{
	private float cooldown = 0 + random.nextFloat();
	
	private static Vector2 tmpVector = new Vector2();
	private static Vector2 tmpVector2 = new Vector2();

	private static float MAX_VELOCITY = 1.99f;

	private float distanceToBlock = 100.0f;
	static private Vector2 blockNormal = new Vector2();

	private Vector2 targetDir = new Vector2(0.0f, 1.0f);
	private Vector2 dir = new Vector2(0.0f, 1.0f);

	private float dA = 0;
	private static String CAN_SEE = "S";
	private static String CAN_HEAR = "H";
	
	private float fireCooldown = 0;
	
	private boolean searchForSpice = false;

	private int debug_tx = 0;
	private int debug_ty = 0;

	private int debug_sx = 0;
	private int debug_sy = 0;

	public Monster(float posx, float posy)
	{
		cooldown = 0 + random.nextFloat() * 0.01f;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(posx, posy);

		body = Physics.world.createBody(bodyDef);

		CircleShape shape = new CircleShape();
		shape.setRadius(0.24f);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 3.315f;
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

		body.setLinearDamping(0.49f);
		body.setAngularDamping(0.45f);

		// body.setTransform(body.getPosition().x, body.getPosition().y, 90.0f *
		// DEGREES_TO_RADIANS);

		body.setUserData(this);

		targetDir.setAngle(body.getAngle() * G.RADIANS_TO_DEGREES);
		dir.setAngle(body.getAngle() * G.RADIANS_TO_DEGREES);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		shape.dispose();

		setHealth(4);

	}

	@Override
	public Body getBody()
	{
		return body;
	}

	@Override
	public void tick( float delta )
	{
		dA += delta;
		brain.tick( delta );

		if (false)
			return;

		if (body == null)
			return;
		
		fireCooldown -= delta;

		cooldown -= delta;

		if (cooldown <= 0.0f)
		{
			//cooldown = 0.0015f;
			cooldown = 0.10f + cooldown;
			
			if (this.brain.canHearPlayer || this.brain.canSeePlayer)
			{
				Vector2 playerPos = Player.INSTANCE.getWorldCenter();
				Vector2 pos = body.getWorldCenter();

				Vector2 tmp = Monster.tmpVector;
				tmp.x = (playerPos.x - pos.x);
				tmp.y = (playerPos.y - pos.y);
				tmp.nor();

				targetDir.x = tmp.x;
				targetDir.y = tmp.y;
			}

			
			// Vector2 vel = body.getLinearVelocity();
			/*
			 * if( vel.len() > MAX_VELOCITY ) { vel.nor(); vel.mul( MAX_VELOCITY
			 * ); body.setLinearVelocity( vel ); }
			 */

			if (random.nextInt(100) < 50)
			{
				dir.setAngle((body.getAngle() * G.RADIANS_TO_DEGREES));
				dir.nor();
				dir.scl(random.nextFloat() * 258.45f * delta );

				body.applyForceToCenter(dir, true);

			}

			if (random.nextInt(100) < 30)
			{
				makeRayCast();
			}
			
			if( (random.nextInt(100) < 8) && brain.canSeePlayer && fireCooldown <= 0)
			{
				fireBullet();
				fireCooldown = 0.08f;
			}

			if (random.nextInt(100) < 60)
			{
				turnToTarget( delta );
			}

			if (random.nextInt(100) < 20 && !brain.canSeePlayer && !brain.canHearPlayer)
			{
				moveDirToRandom();
				
			}

			if (random.nextInt(100) < 20 && !searchForSpice)
			{
				startSpiceSearch();
			}

		}

	}

	private void startSpiceSearch()
	{
		// Mover mover = new Mover();

		AnimateEntity closestSpice = findClosestSpice();

		if (closestSpice == null)
			return;

		int sx, sy, tx, ty;
		Vector2 temp = body.getWorldCenter();
		Vector2 temp2 = closestSpice.getBody().getWorldCenter();

		sx = (int) ((temp.x) / 32.0f);
		sy = (int) ((temp.y) / 32.0f);

		tx = (int) ((temp2.x) / 32.0f);
		ty = (int) ((temp2.y) / 32.0f);

		this.debug_sx = sx;
		this.debug_sy = sy;
		this.debug_tx = tx;
		this.debug_ty = ty;

	}

	private AnimateEntity findClosestSpice()
	{

		Array<AnimateEntity> spices = new Array<AnimateEntity>();//  ProceduralGameWorld.INSTANCE.getSpiceList();

		float closestDist = 50000.0f;

		AnimateEntity closestSpice = null;
		Vector2 thisPos = body.getWorldCenter();

		for( int i = 0, size = spices.size; i < size; i++ )
		{
			AnimateEntity e = spices.get( i );
		
			Body b = e.getBody();

			float dist = b.getWorldCenter().dst2(thisPos);

			if (dist < closestDist && dist < 64.0f)
			{
				closestDist = dist;
				closestSpice = e;
			}

		}

		return closestSpice;
	}

	private BaseMonster findClosestBigMonster()
	{
		Array<BaseMonster> monsters = new Array<BaseMonster>();// ProceduralGameWorld.INSTANCE.getMonsterList();

		float closestDist = 50000.0f;

		BaseMonster closestBigMonster = null;
		Vector2 thisPos = body.getWorldCenter();

		for( int i = 0, size = monsters.size; i < size; i++ )
		{
			BaseMonster e = monsters.get( i );
		
			if (e instanceof Monster2)
			{
				Body b = e.getBody();

				float dist = b.getWorldCenter().dst2(thisPos);

				if (dist < closestDist && dist < 25.0f)
				{
					closestDist = dist;
					closestBigMonster = e;
				}
			} else
				continue;

		}

		return closestBigMonster;
	}

	private void makeRayCast()
	{
		Vector2 pos = body.getPosition();

		Vector2 feeler = Monster.tmpVector;

		feeler.x = pos.x;
		feeler.y = pos.y;

		feeler.x += targetDir.x * 2.5f;
		feeler.y += targetDir.y * 2.5f;

		Vector2 diff = Monster.tmpVector2;
		diff.x = feeler.x - pos.x;
		diff.y = feeler.y - pos.y;

		RayCastClosestCB cb = new RayCastClosestCB(body.getWorldCenter());

		if (diff.len2() > 0.0f)
			Physics.world.rayCast(cb, body.getPosition(), feeler);

		distanceToBlock = cb.closest;

		blockNormal.x = cb.blockNormal.x;
		blockNormal.y = cb.blockNormal.y;

		if (distanceToBlock < 0.3f)
		{
			float adjust = 0.0f;
			float angle = targetDir.angle();

			if (angle < 0)
				adjust = 180.0f;
			else
				adjust = -180f;

			targetDir.rotate(adjust);

			int qwe = 0;
		} else if (distanceToBlock < 0.8f)
		{
			final float scalingFactor = 120.0f;
			targetDir.rotate((random.nextFloat() - 0.5f) * scalingFactor);
		}

	}

	private void moveDirToRandom()
	{
		// if( distanceToBlock < 0.3f )
		// {
		// targetDir.x = -targetDir.x;
		// targetDir.y = -targetDir.y;
		// return;
		// }

		// if( distanceToBlock < 1.5f )
		// {
		// targetDir.x = targetDir.x + blockNormal.x;
		// targetDir.y = targetDir.y + blockNormal.y;
		// targetDir.nor();
		// }

		final float scalingFactor = 55.0f;
		targetDir.rotate((random.nextFloat() - 0.5f) * scalingFactor);

	}

	private void turnToTarget( float delta )
	{
		float targetAngle = targetDir.angle();
		float bodyAngle = body.getAngle() * G.RADIANS_TO_DEGREES;

		float diff = Util.angleDiff(targetAngle, bodyAngle);

		float scaling = 12.50f * delta;

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
	

	private void fireBullet()
	{
		fireBulletAtPlayer(0.38f, 7.8f, 1);
	}
	
	@Override
	protected void fireBulletAtPlayer( float scatter, float speed, int type )
	{
		Vector2 pos = body.getWorldCenter();
		
		float angle = body.getAngle();
		Vector2 temp = Monster.tmpVector;
		temp.nor();
		temp.setAngle(angle * G.RADIANS_TO_DEGREES );
		
		Vector2 temp2 = Monster.tmpVector2;
		temp2.x = temp.x;
		temp2.y = temp.y;
		temp2.scl(0.1f );
		
		MonsterBullets.INSTANCE.fireBullet(pos.x + temp2.x, pos.y + temp2.y , 
				temp.x * speed, temp.y * speed, type );
		
		//G.shot.play( 0.04f );
		
	}

	@Override
	public void draw(Screen screen)
	{
		// sprite.setPosition(camera.position.x+384, camera.position.y+284);

		if (body == null)
			return;

		final Vector2 pos = body.getPosition();

		if (screen.outSideScreen(pos, 16))
			return;

//		pos.scl(Physics.BOX_TO_WORLD);

		final Sprite sprite = G.monsters[0];
		
		sprite.setOriginCenter();
		sprite.setSize(0.5f, 0.5f);
		sprite.setPosition(pos.x - sprite.getWidth()*0.5f, pos.y - sprite.getHeight()*0.5f);

		float angle = body.getAngle();

		sprite.setRotation(angle * G.RADIANS_TO_DEGREES - 90.0f);

		sprite.draw(screen.worldBatch);

		//G.font.draw(screen.batch, healthString, sprite.getX() + 26, sprite.getY() + 32);
		
		G.font.setColor( 0.7f, 0.7f, 0.7f, 0.7f );
		/*
		if (brain.canSeePlayer)
			G.font.draw(screen.batch, CAN_SEE, sprite.getX() - 16, sprite.getY() + 15);

		if (brain.canHearPlayer)
			G.font.draw(screen.batch, CAN_HEAR, sprite.getX() - 16, sprite.getY() + 32);
			*/

		
		//G.font.draw( screen.batch, "DA" + dA, sprite.getX() - 26, sprite.getY() + 45);

		dA = 0.0f;
		// sprite = G.effects[0];
		// sprite.setPosition(pos.x-25, pos.y-25);
		// sprite.setColor(1.0f, 1.0f, 1.0f, 0.5f);
		// sprite.draw( screen.batch );


		debugDraw();

		// batch.draw( ship, 384, 284 );

	}

	private void debugDraw()
	{
		if( true )
			return;
		
		targetDir.nor();
		G.shapeRenderer.begin(ShapeType.Line);
		G.shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 0.1f);
		Vector2 pos = body.getWorldCenter();
		G.shapeRenderer.line(pos.x, pos.y,

		pos.x + targetDir.x * 30.0f, pos.y + targetDir.y * 30.0f);
		G.shapeRenderer.end();
		
		if (debug_sx == 0)
			return;

		G.shapeRenderer.begin(ShapeType.Line);
		G.shapeRenderer.setColor(0.5f, 0.8f, 0.6f, 0.1f);
		G.shapeRenderer.circle(debug_sx * 32 + 16, debug_sy * 32 + 16, 5.0f);

		G.shapeRenderer.setColor(0.9f, 0.8f, 0.6f, 0.1f);
		G.shapeRenderer.circle(debug_tx * 32 + 16, debug_ty * 32 + 16, 5.0f);

		G.shapeRenderer.end();

	}

}
