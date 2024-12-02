package com.vesas.spacefly.monster;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.G.MonsterType;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.game.Util;

public class ShootStickMonster extends Monster
{
	private float cooldown = 0;

	private final Vector2 faceDir;

	public ShootStickMonster(float posx, float posy, Vector2 faceDir) {
		this.faceDir = faceDir;
		cooldown = 0 + random.nextFloat() * 0.1f;
		initializeBody(posx, posy);
		setHealth( 4 );
	}

	private void initializeBody(float posx, float posy) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(posx, posy);
		bodyDef.angle = faceDir.angleRad();

		body = Box2DWorld.world.createBody(bodyDef);

		PolygonShape polyShape = new PolygonShape();
		polyShape.set(
			new float[] {
				-0.38f, 0.12f, // right back corner
				-0.38f, -0.12f, // left back corner
				0.03f, -0.12f, // front left corner
				0.03f, 0.12f // front right corner
			}
		);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polyShape;
		fixtureDef.density = 2.11f;
		fixtureDef.friction = 0.6f;
		fixtureDef.restitution = 0.75f; 
		fixtureDef.filter.categoryBits = 16; // monster
		fixtureDef.filter.maskBits = 23;

		body.createFixture(fixtureDef);

		polyShape.dispose();

		body.setLinearDamping(0.7f);
		body.setAngularDamping(0.7f);

		body.setUserData(this);
	}
	
	@Override
	public void tick( float delta ) {
		cooldown -= delta;

		if (cooldown <= 0) {
			fireBullet();
			cooldown = 2.5f + cooldown;
		}
	}

	private void fireBullet() {
		fireBulletAtDir( faceDir, 0.0f, 2.0f, 1, 0.2f);
	}

	@Override
	public void draw(GameScreen screen) {
		if (body == null) return;

		Vector2 pos = body.getWorldCenter();

		Sprite sprite = G.monsters.get(MonsterType.SHOOT_STICK);

		sprite.setOriginCenter();
		sprite.setScale( 0.012f );
		sprite.setPosition(pos.x - sprite.getWidth() * 0.5f, pos.y - sprite.getHeight() * 0.5f);
		sprite.setRotation(body.getAngle() * Util.RADTODEG - 90f);

		sprite.draw(screen.worldBatch);
	}

}
