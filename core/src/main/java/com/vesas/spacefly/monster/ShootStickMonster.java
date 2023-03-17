package com.vesas.spacefly.monster;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.Screen;
import com.vesas.spacefly.game.Util;

public class ShootStickMonster extends Monster
{
	private float cooldown = 0;

	private static Vector2 tmpVector = new Vector2();

	private Vector2 faceDir = new Vector2();

	public enum DIRECTION
	{
		E, W, S, N;
	}
	
	public ShootStickMonster(float posx, float posy, Vector2 faceDir)
	{
		this.faceDir = faceDir;

		cooldown = 0 + random.nextFloat() * 0.1f;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set(posx, posy);
		bodyDef.angle = faceDir.angleRad();

		body = Box2DWorld.world.createBody(bodyDef);

		PolygonShape polyShape = new PolygonShape();

		float[] v = new float[8];

		// right back corner
		v[0] = -0.38f;
		v[1] = 0.12f;

		// left back corner
		v[2] = -0.38f;
		v[3] = -0.12f;
		
		// front left corner
		v[4] = 0.03f;
		v[5] = -0.12f;

		// front right corner
		v[6] = 0.03f;
		v[7] = 0.12f;

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

		setHealth( 8 );
	}
	
	@Override
	public void tick( float delta )
	{
		cooldown -= delta;

		if (cooldown <= 0)
		{
			fireBullet();
			cooldown = 2.5f + cooldown;
		}

	}

	private void fireBullet()
	{
		fireBulletAtDir( faceDir, 0.0f, 2.0f, 1, 0.2f);
	}

	@Override
	public void draw(Screen screen)
	{
		if (body == null)
			return;

		Vector2 pos = body.getWorldCenter();

//		pos.scl(Physics.BOX_TO_WORLD);
		Sprite sprite = G.monsters[4];

		sprite.setOriginCenter();
		sprite.setScale( 0.012f );
		sprite.setPosition(pos.x - sprite.getWidth() * 0.5f, pos.y - sprite.getHeight() * 0.5f);

		float angle = body.getAngle();
		float bodyAngleInDegrees = angle * Util.RADTODEG - 90f;

		sprite.setRotation(bodyAngleInDegrees);

		sprite.draw(screen.worldBatch);


	}

}
