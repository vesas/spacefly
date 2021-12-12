package com.vesas.spacefly.monster;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.vesas.spacefly.DebugHelper;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.Bullet;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.Screen;
import com.vesas.spacefly.game.Util;

public class ShellMonster extends Monster
{
	private float cooldown = 0 + random.nextFloat() * 0.1f;

	private static float MAX_VELOCITY = 2.99f;
	
	private static Vector2 tmpVector = new Vector2();

	private Vector2 gunDir = new Vector2();

	public enum DIRECTION
	{
		E, W, S, N;

	}
	
	//String debug1 = "";
	//String debug2 = "";

	//String debug3 = "";
	//String debug4 = "";

	public class Brain2 extends Brain
	{
		DIRECTION dir = DIRECTION.W;
		private WallDistanceCallback wallcb = new WallDistanceCallback();

		class WallDistanceCallback implements RayCastCallback
		{

			private float distanceToWall = 100.0f;
			private Vector2 hitPoint = new Vector2();

			public void init()
			{
				distanceToWall = 9999999.0f;
			}

			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
			{
				Object o2 = fixture.getBody().getUserData();

				if (o2 instanceof MonsterBullet || o2 instanceof Bullet)
				{
					// ignore bullets
					return -1;
				}

				if (o2 instanceof SlurgMonster || o2 instanceof ShellMonster)
				{
					// ignore monsters
					return -1;
				}

				if (o2 instanceof Player)
				{
					// ignore player
					return -1;
				}

				Vector2 pos = body.getPosition();

				float tempDist = pos.dst2(point);

				if (tempDist < distanceToWall)
				{
					distanceToWall = tempDist;
					hitPoint.x = point.x;
					hitPoint.y = point.y;
				}

				return 1;

			}

		}
		
		private void moveGunToPlayer()
		{
			Vector2 pos = body.getPosition();
			Vector2 playerPos = Player.INSTANCE.getPosition();

			playerPos.sub(pos);
			playerPos.nor();

			float playerAngle = playerPos.angle();
			float gunAngle = gunDir.angle();

			// debug3 = "playerAngle: " + playerAngle;

			float angleAbsDiff = Util.absAngleDiff(playerAngle, gunAngle);
			float angleDiff = Util.angleDiff(playerAngle, gunAngle);
			
			if( angleAbsDiff > 1 )
			{
				gunDir.rotate( angleDiff * 0.05f );
			}
		}

		@Override
		public void tick( float delta )
		{
			cooldown -= delta;
			
			if( canSeePlayer )
			{
				moveGunToPlayer();
			}
			
			if (cooldown <= 0)
			{
				cooldown = 0.5f;

				findClosestWall();

				determineSight();

				determineHearing();
				
				Vector2 tmp = ShellMonster.tmpVector;

				if (dir == DIRECTION.E)
				{
					tmp.x = 1.0f;
					tmp.y = 0f;
				} else if (dir == DIRECTION.W)
				{
					tmp.x = -1.0f;
					tmp.y = 0f;
				} else if (dir == DIRECTION.S)
				{
					tmp.x = 0f;
					tmp.y = -1.0f;
				} else if (dir == DIRECTION.N)
				{
					tmp.x = 0f;
					tmp.y = +1.0f;
				} else
				{
					tmp.x = 0f;
					tmp.y = +1.0f;
				}

				tmp.scl(2.95f);

				body.applyForceToCenter(tmp, true);

				// reverse
				tmp.x = -tmp.x;
				tmp.y = -tmp.y;
				tmp.nor();

				float targetAngle = tmp.angle();
				float bodyAngle = body.getAngle() * Util.RADTODEG;

				// debug1 = "targetAngle: " + targetAngle;
				// debug2 = "bodyAngle: " + bodyAngle;

				float diff = Util.angleDiff(targetAngle, bodyAngle);

				float scaling = 1.99f;

				float absDiff = Math.abs(diff);

				if (absDiff < 140.0)
					scaling = scaling * 0.9f;

				if (absDiff < 75.0)
					scaling = scaling * 0.7f;

				if (absDiff < 35.0)
					scaling = scaling * 0.6f;

				if (absDiff < 15.0)
					scaling = scaling * 0.6f;

				if (absDiff < 5.0)
					scaling = scaling * 0.6f;

				if (diff < 0)
				{
					body.applyTorque(-scaling, true);
				} else
				{
					body.applyTorque(scaling, true);
				}
			}

		}

		@Override
		protected void determineSight()
		{
			Vector2 pos = body.getPosition();
			Vector2 playerPos = Player.INSTANCE.getPosition();

			playerPos.sub(pos);
			playerPos.nor();

			float playerAngle = playerPos.angle();
			float bodyAngle = body.getAngle() * Util.RADTODEG;

			// debug3 = "playerAngle: " + playerAngle;

			float angleDiff = Util.absAngleDiff(playerAngle, bodyAngle);

			if (angleDiff < 110)
			{
				makeSightRayCast();
			} else
			{
				canSeePlayer = false;
			}
		}

		protected void findClosestWall()
		{
			DIRECTION tempDir = DIRECTION.W;
			float closestDistance = 6000.0f;

			Vector2 pos = body.getPosition();

			Vector2 target = ShellMonster.tmpVector;
			target.x = pos.x - 43.0f;
			target.y = pos.y;

			wallcb.init();
			Box2DWorld.world.rayCast(wallcb, target, pos);
			float westDist = wallcb.distanceToWall;

			if (DebugHelper.BOX2D_DEBUG)
			{
				//debug1 = "westDist: " + westDist;

			}

			if (westDist < closestDistance)
			{
				tempDir = DIRECTION.W;
				closestDistance = westDist;
			}

			target.x = pos.x + 43.0f;
			target.y = pos.y;

			wallcb.init();
			Box2DWorld.world.rayCast(wallcb, target, pos);
			float eastDist = wallcb.distanceToWall;

			if (DebugHelper.BOX2D_DEBUG)
			{
				//debug2 = "eastDist: " + eastDist;
			}

			if (eastDist < closestDistance)
			{
				tempDir = DIRECTION.E;
				closestDistance = eastDist;
			}

			target.x = pos.x;
			target.y = pos.y - 43.0f;

			wallcb.init();
			Box2DWorld.world.rayCast(wallcb, target, pos);
			float southDist = wallcb.distanceToWall;

			if (DebugHelper.BOX2D_DEBUG)
			{
				//debug3 = "southDist: " + southDist;
			}

			if (southDist < closestDistance)
			{
				tempDir = DIRECTION.S;
				closestDistance = southDist;
			}

			target.x = pos.x;
			target.y = pos.y + 43.0f;

			wallcb.init();
			Box2DWorld.world.rayCast(wallcb, target, pos);
			float northDist = wallcb.distanceToWall;

			if (DebugHelper.BOX2D_DEBUG)
			{
				//debug4 = "northDist: " + northDist;
			}

			if (northDist < closestDistance)
			{
				tempDir = DIRECTION.N;
				closestDistance = northDist;
			}

			dir = tempDir;
		}
	}

	public ShellMonster(float posx, float posy)
	{
		cooldown = 0 + random.nextFloat() * 0.1f;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(posx, posy);

		body = Box2DWorld.world.createBody(bodyDef);

		PolygonShape polyShape = new PolygonShape();

		float[] v = new float[16];

		// facing east
		
		// backside
		v[0] = -0.25f;
		v[1] = 0.38f;

		v[2] = -0.25f;
		v[3] = -0.38f;
		// end of backside
		
		
		// curve from down to up
		v[4] = -0.07f;
		v[5] = -0.36f;

		v[6] = 0.02f;
		v[7] = -0.30f;

		
		v[8] = 0.10f;
		v[9] = -0.1f;

		v[10] = 0.10f;
		v[11] = 0.1f;

		
		v[12] = 0.02f;
		v[13] = 0.30f;

		v[14] = -0.07f;
		v[15] = 0.36f;

		polyShape.set(v);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polyShape;
		fixtureDef.density = 2.11f;
		fixtureDef.friction = 0.6f;
		fixtureDef.restitution = 0.75f; // Make it bounce a little bit
		// fixtureDef.filter.groupIndex = Physics.GROUP_MONSTER;
		fixtureDef.filter.categoryBits = 16; // 1 wall, 2 player, 4
											 // playerbullet, 8 monsterbullet,
											 // 16 monster
		fixtureDef.filter.maskBits = 23;

		body.createFixture(fixtureDef);

		polyShape.dispose();

		body.setLinearDamping(0.7f);
		body.setAngularDamping(0.7f);

		body.setUserData(this);

		gunDir.x = 1.0f;
		gunDir.y = 0.0f;

		this.brain = new Brain2();

		setHealth( 8 );

	}
	
	@Override
	public void tick( float delta )
	{
		if (false)
			return;

		if (body == null)
			return;

		brain.tick( delta );

		cooldown -= delta;

		if (cooldown <= 0)
		{
			cooldown = 0.6f;

			Vector2 vel = body.getLinearVelocity();

			if (vel.len() > MAX_VELOCITY)
			{
				vel.nor();
				vel.scl(MAX_VELOCITY);
				body.setLinearVelocity(vel);
			}

			if (random.nextInt(100) < 40)
			{
				Vector2 dir = new Vector2();

				dir.x = 0.5f - random.nextFloat();
				dir.y = 0.5f - random.nextFloat();

				dir.scl(3.5f * random.nextFloat() + 1.1f);

				// body.applyForceToCenter( dir );

			}

			if (random.nextInt(100) < 70 && brain.canSeePlayer)
			{
				fireBullet();
			}

			if (random.nextInt(100) < 50 && brain.canSeePlayer)
			{
				// moveTowardsPlayer();
			}
		}

	}

	private void moveTowardsPlayer()
	{
		Vector2 playerPos = Player.INSTANCE.getPosition();
		Vector2 pos = body.getWorldCenter();

		Vector2 tmp = ShellMonster.tmpVector;
		tmp.x = playerPos.x - pos.x;
		tmp.y = playerPos.y - pos.y;
		tmp.nor();

		tmp.scl(1.95f);

		body.applyForceToCenter(tmp, true);
	}

	private void fireBullet()
	{
		fireBulletAtDir( gunDir, 0.15f, 12.2f, 1);
	}

	@Override
	public void draw(Screen screen)
	{
		if (body == null)
			return;

		Vector2 pos = body.getWorldCenter();

//		pos.scl(Physics.BOX_TO_WORLD);
		Sprite sprite = G.monsters[2];

		sprite.setOriginCenter();
		sprite.setScale( 0.012f );
		// sprite.setOrigin(-6, -5);
		sprite.setPosition(pos.x - sprite.getWidth() * 0.5f, pos.y - sprite.getHeight() * 0.5f);
		// sprite.setPosition(pos.x, pos.y);

		float angle = body.getAngle();
		float bodyAngleInDegrees = angle * Util.RADTODEG - 90.0f;

		sprite.setRotation(bodyAngleInDegrees);

		sprite.draw(screen.worldBatch);

//		Sprite gunSprite = G.monsters[5];
		//gunSprite.setOrigin(0, 0);
//		gunSprite.setPosition(pos.x- 5, pos.y-8);
		//gunSprite.draw(screen.batch);
		
		//G.font.draw(screen.batch, healthString, sprite.getX() + 28, sprite.getY() + 34);

		int qwe = 0;
		if (brain.canSeePlayer)
			qwe = 0;//G.font.draw(screen.batch, "S", sprite.getX() - 19, sprite.getY() + 15);

		if (brain.canHearPlayer)
			qwe = 0;//G.font.draw(screen.batch, "H", sprite.getX() - 19, sprite.getY() + 32);

		if (DebugHelper.BOX2D_DEBUG)
		{
			//G.font.draw(screen.batch, debug1, sprite.getX() + 28, sprite.getY() + 50);
			//G.font.draw(screen.batch, debug2, sprite.getX() + 28, sprite.getY() + 70);

			//G.font.draw(screen.batch, debug3, sprite.getX() + 28, sprite.getY() + 90);
			//G.font.draw(screen.batch, debug4, sprite.getX() + 28, sprite.getY() + 110);
		}

		// batch.draw( ship, 384, 284 );

	}

}
