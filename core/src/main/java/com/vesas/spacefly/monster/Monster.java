package com.vesas.spacefly.monster;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.AnimateEntity;
import com.vesas.spacefly.game.Bullet;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.Util;
import com.vesas.spacefly.screen.GameScreen;

abstract public class Monster implements AnimateEntity
{
	// Box2D body instance
	protected Body body;
	protected int health = 3;
	protected String healthString;

	static protected Random random = new Random(1312);

	static private int brainCount = 0;

	class Brain implements RayCastCallback
	{
		public boolean canSeePlayer = false;
		public boolean canHearPlayer = false;
		public boolean lookingForPlayer = false;

		float cooldown = 0;

		int id;

		public Brain() {
			id = brainCount;
			brainCount++;
		}

		public void tick( float delta ) {
			cooldown -= delta;

			if (cooldown <= 0) {
				oncePerSecond();
				cooldown = 0.1f;
			}
		}

		private void oncePerSecond() {
			determineSight();
			determineHearing();
		}

		protected void determineHearing()
		{
			Vector2 pos = body.getPosition();
			Vector2 playerPos = Player.INSTANCE.getPosition();

			float distance = pos.dst2(playerPos);
			if (distance < 5.9) {
				canHearPlayer = true;
			} 
			else if (distance < 18.6 && Player.INSTANCE.recentlyShot()) {
				canHearPlayer = true;
			} 
			else {
				canHearPlayer = false;
			}

		}

		protected void determineSight()
		{
			Vector2 pos = body.getPosition();
			Vector2 playerPos = Player.INSTANCE.getPosition();

			playerPos.sub(pos);
			playerPos.nor();

			float playerAngle = playerPos.angleDeg();
			float bodyAngle = body.getAngle() * Util.RADTODEG;

			float absAngle = Util.absAngleDiff(playerAngle, bodyAngle);
			if (absAngle < 95) {
				makeSightRayCast();
			} 
			else {
				canSeePlayer = false;
			}
		}

		protected void makeSightRayCast() {
			final Vector2 pos = body.getWorldCenter();
			final Vector2 playerPos = Player.INSTANCE.getWorldCenter();

			if (pos.dst2(playerPos) < 1296) { // 36 * 36
				lookingForPlayer = true;
				canSeePlayer = false;
				Box2DWorld.world.rayCast(this, pos, playerPos);
			} else {
				canSeePlayer = false;
			}

		}

		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
		{
			Array<Fixture> playerFixtures = Player.INSTANCE.body.getFixtureList();

			Object o2 = fixture.getBody().getUserData();

			if (o2 instanceof MonsterBullet || o2 instanceof Bullet) {
				// ignore bullets
				return -1;
			}

			if (o2 instanceof Monster) {
				// ignore other monsters
				return -1;
			}

			for (int i = 0, size = playerFixtures.size; i < size; i++) {
				Fixture fix = playerFixtures.get(i);
				Object o1 = fix.getBody().getUserData();

				if (o1 != null && o2 != null && o1 == o2) {
					canSeePlayer = true;
					lookingForPlayer = false;

					return -1.0f;
				}
			}

			canSeePlayer = false;
			lookingForPlayer = false;

			return fraction;
		}
	}

	public Body getBody() {
		return body;
	}

	protected Brain brain = new Brain();

	public void setHealth(int h) {
		health = h;
		healthString = "" + h;
	}

	public void getHit(Bullet b) {
		health--;
		healthString = "" + health;
	}

	public boolean isDead() {
		return health <= 0;
	}

	public void destroy() {
		Box2DWorld.safeDestroyBody(this.body);
	}

	protected void fireBulletAtDir(Vector2 dir, float scatter, float speed, int type, BodyBuilder bodyBuilder) {
		fireBulletAtDir(dir, scatter, speed, type, 0.0f, bodyBuilder);
	}

	protected void fireBulletAtDir(Vector2 dir, float scatter, float speed, int type, float posAdvance, BodyBuilder bodyBuilder) {
		final Vector2 pos = body.getPosition();

		dir.x += (scatter - random.nextFloat() * scatter * 2.0f);
		dir.y += (scatter - random.nextFloat() * scatter * 2.0f);

		dir.nor();

		MonsterBullets.INSTANCE.fireBullet(pos.x + dir.x * posAdvance, pos.y + dir.y * posAdvance, dir.x * speed, dir.y
				* speed, type, bodyBuilder);
	}

	protected void fireBulletAtPlayer(float scatter, float speed, int type, BodyBuilder bodyBuilder) {
		Vector2 pos = body.getPosition();

		Vector2 playerPos = Player.INSTANCE.getPosition();

		playerPos.sub(pos);
		playerPos.nor();

		playerPos.x += (scatter - random.nextFloat() * scatter * 2.0f);
		playerPos.y += (scatter - random.nextFloat() * scatter * 2.0f);

		playerPos.nor();

		MonsterBullets.INSTANCE.fireBullet(pos.x, pos.y, playerPos.x * speed,
				playerPos.y * speed, type, bodyBuilder);	

		// G.shot.play( 0.04f );

	}

	public boolean monsterNearby(float x, float y) {
		Vector2 anotherPos = new Vector2(x, y);
		Vector2 myPos = body.getPosition();

		if (anotherPos.dst2(myPos) < 2025.0f)
			return true;

		return false;
	}

	abstract public void draw(GameScreen screen);

	abstract public void tick(GameScreen screen, float delta );
}
