package com.vesas.spacefly.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet extends AbstractBullet
{
	
	public Bullet( float posx, float posy, float dirx, float diry ) 
	{ 
		super( posx, posy, dirx, diry, (short)4, (short)17 );
	}
	
	
	@Override
	public void draw( SpriteBatch batch )
	{
		final Vector2 center = body.getWorldCenter();
		
		final Vector2 dir = body.getLinearVelocity();
		dir.nor();
		float angle = dir.angleDeg();
		
		final Sprite sprite = G.bullets[0];
		
		sprite.setSize( 0.15f, 0.4f);
		sprite.setOriginCenter();
		
		sprite.setRotation(angle - 90.0f );
		sprite.setPosition(center.x - sprite.getWidth() * 0.5f - dir.x * 0.18f, center.y - sprite.getHeight() * 0.5f - dir.y * 0.18f);
		

		sprite.draw(batch);
	}
	
}