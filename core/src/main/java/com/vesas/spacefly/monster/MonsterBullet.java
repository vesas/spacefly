package com.vesas.spacefly.monster;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.AbstractBullet;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.G.BulletType;

public class MonsterBullet extends AbstractBullet
{
	public int type;
	
	public MonsterBullet( float posx, float posy, float dirx, float diry, int type, BodyBuilder bodyBuilder ) 
	{ 
		super( posx, posy, dirx, diry, (short)8, (short)3, bodyBuilder );
		
		this.type = type;
		
	}
	
	@Override
	public void draw( SpriteBatch batch )
	{
		final Vector2 center = body.getWorldCenter();
		
		if( type == 1 )
		{
			Vector2 dir = body.getLinearVelocity();
			dir.nor();
			float angle = dir.angleDeg();
			
			Sprite sprite = G.bullets.get(BulletType.MONSTER2);
			sprite.setSize( 0.2f, 0.55f);
			sprite.setOriginCenter();
			sprite.setPosition(center.x - sprite.getWidth() * 0.5f, center.y - sprite.getHeight() * 0.5f);
			sprite.setRotation(angle - 90.0f);
			sprite.draw(batch);
		}
		else
		{
			Sprite sprite = G.bullets.get(BulletType.MONSTER);
			sprite.setSize( 0.2f, 0.55f);
			sprite.setOriginCenter();
			sprite.setPosition(center.x - sprite.getWidth() * 0.5f, center.y - sprite.getHeight() * 0.5f);
			sprite.draw(batch);
		}
	}
	
}