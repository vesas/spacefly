package com.vesas.spacefly.monster;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.Bullet;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.G.MonsterType;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.Util;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.util.DebugHelper;

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

			float playerAngle = playerPos.angleDeg();
			float gunAngle = gunDir.angleDeg();

			// debug3 = "playerAngle: " + playerAngle;

			float angleAbsDiff = Util.absAngleDiff(playerAngle, gunAngle);
			float angleDiff = Util.angleDiff(playerAngle, gunAngle);
			
			if( angleAbsDiff > 1 )
			{
				gunDir.rotateDeg( angleDiff * 0.05f );
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

				float targetAngle = tmp.angleDeg();
				float bodyAngle = body.getAngle() * Util.RADTODEG;

				// debug1 = "targetAngle: " + targetAngle;
				// debug2 = "bodyAngle: " + bodyAngle;

				float diff = Util.angleDiff(targetAngle, bodyAngle);

				float scaling = 1.99f;

				float absDiff = Math.abs(diff);

				if (absDiff < 140.0) {
					scaling = scaling * 0.9f;
				}

				if (absDiff < 75.0) {
					scaling = scaling * 0.7f;
				}

				if (absDiff < 35.0) {
					scaling = scaling * 0.6f;
				}

				if (absDiff < 15.0) {
					scaling = scaling * 0.6f;
				}

				if (absDiff < 5.0) {
					scaling = scaling * 0.6f;
				}

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

			float playerAngle = playerPos.angleDeg();
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

			if (northDist < closestDistance)
			{
				tempDir = DIRECTION.N;
				closestDistance = northDist;
			}

			dir = tempDir;
		}
	}

	public ShellMonster(float posx, float posy, BodyBuilder bodyBuilder)
	{
		cooldown = 0 + random.nextFloat() * 0.1f;

		float[] vertices = new float[] {
			// backside
			-0.25f,  0.38f,
			-0.25f, -0.38f,
			
			// curve from down to up
			-0.07f, -0.36f,
			 0.02f, -0.30f,
			 0.10f, -0.10f,
			 0.10f,  0.10f,
			 0.02f,  0.30f,
			-0.07f,  0.36f
		};

		body = bodyBuilder
			.setBodyType(BodyType.DynamicBody)
			.setPosition(posx, posy)
			.polygon(vertices)
			.setDensity(2.11f)
			.setFriction(0.6f)
			.setRestitution(0.75f)
			.setFilterCategoryBits((short)16)  // monster
			.setFilterMaskBits((short)23)
			.setLinearDamping(0.7f)
			.setAngularDamping(0.7f)
			.setUserdata(this)
			.construct();

		gunDir.x = 1.0f;
		gunDir.y = 0.0f;

		this.brain = new Brain2();

		setHealth( 8 );

	}
	
	@Override
	public void tick(GameScreen screen, float delta ) {
		if (body == null) {
			return;
		}

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
				fireBullet(screen.getBodyBuilder());
			}

			if (random.nextInt(100) < 50 && brain.canSeePlayer)
			{
				moveTowardsPlayer();
			}
		}

	}

	private void moveTowardsPlayer() {
		Vector2 playerPos = Player.INSTANCE.getPosition();
		Vector2 pos = body.getWorldCenter();

		Vector2 tmp = ShellMonster.tmpVector;
		tmp.x = playerPos.x - pos.x;
		tmp.y = playerPos.y - pos.y;
		tmp.nor();

		tmp.scl(1.95f);

		body.applyForceToCenter(tmp, true);
	}

	private void fireBullet(BodyBuilder bodyBuilder) {
		fireBulletAtDir( gunDir, 0.15f, 12.2f, 1, bodyBuilder);
	}

	@Override
	public void draw(GameScreen screen) {
		if (body == null) {
			return;
		}

		Vector2 pos = body.getWorldCenter();

//		pos.scl(Physics.BOX_TO_WORLD);
		Sprite sprite = G.monsters.get(MonsterType.SHELL);
		
		sprite.setOriginCenter();
		sprite.setScale( 0.012f );
		// sprite.setOrigin(-6, -5);
		sprite.setPosition(pos.x - sprite.getWidth() * 0.5f, pos.y - sprite.getHeight() * 0.5f);
		// sprite.setPosition(pos.x, pos.y);

		float angle = body.getAngle();
		float bodyAngleInDegrees = angle * Util.RADTODEG - 90.0f;

		sprite.setRotation(bodyAngleInDegrees);

		sprite.draw(screen.worldBatch);

	}

}
