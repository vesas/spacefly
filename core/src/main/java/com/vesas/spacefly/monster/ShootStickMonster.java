package com.vesas.spacefly.monster;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.G.MonsterType;
import com.vesas.spacefly.game.Util;
import com.vesas.spacefly.screen.GameScreen;

public class ShootStickMonster extends Monster
{
	private float cooldown = 0;

	private final Vector2 faceDir;

	public ShootStickMonster(float posx, float posy, Vector2 faceDir, BodyBuilder bodyBuilder) {
		this.faceDir = faceDir;
		cooldown = 0 + random.nextFloat() * 0.1f;
		initializeBody(posx, posy, bodyBuilder);
		setHealth( 4 );
	}

	private void initializeBody(float posx, float posy, BodyBuilder bodyBuilder) {

		float[] vertices = new float[] {
			-0.38f, 0.12f, // right back corner
			-0.38f, -0.12f, // left back corner
			0.03f, -0.12f, // front left corner
			0.03f, 0.12f // front right corner
		};

		body = bodyBuilder
			.setBodyType(BodyType.StaticBody)
			.setPosition(posx, posy)
			.setAngle(faceDir.angleRad())
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

		int qwe = 0;
	}
	
	@Override
	public void tick( GameScreen screen, float delta ) {
		cooldown -= delta;

		if (cooldown <= 0) {
			fireBullet(screen.getBodyBuilder());
			cooldown = 2.5f + cooldown;
		}
	}

	private void fireBullet(BodyBuilder bodyBuilder) {
		fireBulletAtDir( faceDir, 0.0f, 2.0f, 1, 0.2f, bodyBuilder);
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
