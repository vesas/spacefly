package com.vesas.spacefly.game;

import com.vesas.spacefly.box2d.BodyBuilder;

public class PlayerBullets extends BaseBullets
{
	public static PlayerBullets INSTANCE = new PlayerBullets();
	
	private PlayerBullets() { }
	
	public void fireBullet( float posx, float posy, float dirx, float diry,
	                        int damage, Weapon.Archetype archetype, boolean crit, BodyBuilder bodyBuilder )
	{
		// See if the pool contains anything
		if( deadPool.size > 0 )
		{
			AbstractBullet bul = deadPool.pop();
			if( bul != null ) {
				bul.body.setTransform(posx, posy, 0f);
				bul.body.setLinearVelocity( dirx, diry );
				bul.body.setAwake( true );
				bul.body.setActive( true );
				bul.resetCreationTime();
				bul.damage = damage;
				((Bullet) bul).archetype = archetype;
				((Bullet) bul).crit = crit;
				bullets.add( bul );
				return;
			}
		}

		// Nothing in the pool, got to create a new one
		Bullet b = new Bullet( posx, posy, dirx, diry, bodyBuilder);
		b.damage = damage;
		b.archetype = archetype;
		b.crit = crit;

		bullets.add( b );
	}

	public void fireBullet( float posx, float posy, float dirx, float diry, BodyBuilder bodyBuilder )
	{
		fireBullet(posx, posy, dirx, diry, Weapon.forArchetype(Weapon.Archetype.BLASTER).damage,
			Weapon.Archetype.BLASTER, false, bodyBuilder);
	}
}
