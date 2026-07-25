package com.vesas.spacefly.monster;

import java.util.Random;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.AnimateEntity;
import com.vesas.spacefly.game.Bullet;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.Util;
import com.vesas.spacefly.screen.GameScreen;

abstract public class Monster implements AnimateEntity
{
	// Box2D body instance
	protected Body body;
	protected int health = 30;
	protected int maxHealth = 30;
	protected String healthString;

	/** millis() of the last hit — drives the white hit-flash and the fade-in health bar. */
	protected long lastHitTime = -100000L;

	private static final float FLASH_MS    = 130f;   // hit-flash duration
	private static final float BAR_SHOW_MS = 2600f;  // health bar visible after a hit
	private static final float BAR_FADE_MS = 500f;   // …fading out over the last stretch

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
		maxHealth = h;
		healthString = "" + h;
	}

	public void getHit(Bullet b) {
		health -= b.damage;
		healthString = "" + health;
		lastHitTime = TimeUtils.millis();

		// Flinch: a small knockback along the bullet's travel direction. Scaled by mass so
		// every monster reacts about the same amount regardless of body size.
		if (body != null) {
			Vector2 v = b.body.getLinearVelocity();
			float len = v.len();
			if (len > 0.0001f) {
				float impulse = 0.9f * body.getMass();
				Vector2 c = body.getWorldCenter();
				body.applyLinearImpulse(v.x / len * impulse, v.y / len * impulse, c.x, c.y, true);
			}
		}
	}

	/** 0 = no flash, 1 = just hit. Decays over FLASH_MS. */
	protected float flashAmount() {
		float dt = TimeUtils.millis() - lastHitTime;
		if (dt >= FLASH_MS) return 0f;
		return 1f - dt / FLASH_MS;
	}

	/** Additive re-draw of the monster's already-positioned sprite, to flash it bright on a
	 *  hit. Call right after the normal sprite.draw(). Restores the shared sprite + blend. */
	protected void drawHitFlash(SpriteBatch batch, Sprite sprite) {
		float flash = flashAmount();
		if (flash <= 0f) return;

		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		sprite.setColor(1f, 1f, 1f, flash * 0.9f);
		sprite.draw(batch);
		sprite.setColor(1f, 1f, 1f, 1f);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}

	/** Thin health bar above the monster that fades in on damage and out a few seconds later —
	 *  so you only ever see the bar of something you are actively shooting. Drawn in the world
	 *  batch with normal alpha blending (call it from the unshaded overlay pass). */
	public void drawHealthBar(SpriteBatch batch) {
		if (body == null || maxHealth <= 0) return;

		float dt = TimeUtils.millis() - lastHitTime;
		if (dt >= BAR_SHOW_MS) return;

		float alpha = 1f;
		if (dt > BAR_SHOW_MS - BAR_FADE_MS)
			alpha = (BAR_SHOW_MS - dt) / BAR_FADE_MS;

		float frac = health / (float) maxHealth;
		if (frac < 0f) frac = 0f;
		if (frac > 1f) frac = 1f;

		Vector2 pos = body.getWorldCenter();
		final float w = 0.9f, h = 0.11f;
		final float x = pos.x - w * 0.5f;
		final float y = pos.y + 0.55f;

		// dark backing
		batch.setColor(0f, 0f, 0f, 0.55f * alpha);
		batch.draw(G.whitePixel, x - 0.03f, y - 0.03f, w + 0.06f, h + 0.06f);
		// depleted track
		batch.setColor(0.28f, 0.05f, 0.05f, 0.7f * alpha);
		batch.draw(G.whitePixel, x, y, w, h);
		// current health, red at low -> green at full
		batch.setColor(1f - frac, 0.85f * frac + 0.1f, 0.12f, 0.95f * alpha);
		batch.draw(G.whitePixel, x, y, w * frac, h);

		batch.setColor(1f, 1f, 1f, 1f);
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
