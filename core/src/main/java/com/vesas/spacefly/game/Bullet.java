package com.vesas.spacefly.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.G.BulletType;

public class Bullet extends AbstractBullet
{
	public Weapon.Archetype archetype = Weapon.Archetype.BLASTER;

	public Bullet( float posx, float posy, float dirx, float diry, BodyBuilder bodyBuilder )
	{
		super( posx, posy, dirx, diry, (short)4, (short)17, bodyBuilder );
	}

	@Override
	public void draw( SpriteBatch batch )
	{
		final Vector2 center = body.getWorldCenter();
		final Vector2 dir = body.getLinearVelocity();
		dir.nor();
		float angle = dir.angleDeg();

		final Sprite sprite = G.bullets.get(BulletType.PLAYER);

		switch (archetype) {
			case SCATTER_GUN:
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				sprite.setSize(0.22f, 0.18f);
				sprite.setColor(1.0f, 0.88f, 0.12f, 1.0f);
				break;
			case BEAM_REPEATER:
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
				sprite.setSize(0.07f, 0.58f);
				sprite.setColor(0.25f, 0.92f, 1.0f, 0.95f);
				break;
			case RAIL_LAUNCHER:
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
				sprite.setSize(0.26f, 0.72f);
				sprite.setColor(1.0f, 0.97f, 0.65f, 1.0f);
				break;
			default: // BLASTER
				batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
				sprite.setSize(0.15f, 0.40f);
				sprite.setColor(1.0f, 0.58f, 0.18f, 1.0f);
				break;
		}

		sprite.setOriginCenter();
		sprite.setRotation(angle - 90.0f);
		sprite.setPosition(center.x - sprite.getWidth() * 0.5f - dir.x * 0.18f,
		                   center.y - sprite.getHeight() * 0.5f - dir.y * 0.18f);
		sprite.draw(batch);

		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
}