package com.vesas.spacefly.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.G.PowerUpType;
import com.vesas.spacefly.screen.GameScreen;

public class WeaponDrop implements AnimateEntity
{
	private Body body;
	private final Weapon weapon;

	public WeaponDrop(float x, float y, Weapon weapon)
	{
		this.weapon = weapon;

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x, y);

		body = Box2DWorld.world.createBody(bodyDef);

		PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(0.25f, 0.25f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polyShape;
		fixtureDef.density = 0.515f;
		fixtureDef.friction = 0.0001f;
		fixtureDef.restitution = 0.65f;
		fixtureDef.filter.categoryBits = 32;
		fixtureDef.filter.maskBits = 2;

		body.createFixture(fixtureDef);

		body.setAwake(true);
		body.setActive(true);
		body.setLinearDamping(0.2f);
		body.setAngularDamping(0.2f);

		body.setUserData(this);

		polyShape.dispose();
	}

	public Weapon getWeapon()
	{
		return weapon;
	}

	@Override
	public Body getBody()
	{
		return body;
	}

	@Override
	public void tick(GameScreen screen, float delta)
	{
	}

	@Override
	public void draw(GameScreen screen)
	{
		Vector2 pos = body.getPosition();

		Sprite sprite = G.powerUps.get(PowerUpType.SHOOT);

		sprite.setOriginCenter();
		sprite.setSize(0.55f, 0.55f);

		float bodyAngleInDegrees = body.getAngle() * Util.RADTODEG - 90.0f;
		sprite.setRotation(bodyAngleInDegrees);
		sprite.setPosition(pos.x - sprite.getWidth() * 0.5f, pos.y - sprite.getHeight() * 0.5f);
		sprite.draw(screen.worldBatch);
	}

	@Override
	public void destroy()
	{
		Box2DWorld.safeDestroyBody(this.body);
	}
}
