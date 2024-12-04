package com.vesas.spacefly.game;

public class PlayerBullets extends BaseBullets
{
	public static PlayerBullets INSTANCE = new PlayerBullets();
	
	private PlayerBullets() { }
	
	public void fireBullet( float posx, float posy, float dirx, float diry )
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
				bullets.add( bul );
				return;
			}
		}

		// Nothing in the pool, got to create a new one
		Bullet b = new Bullet( posx, posy, dirx, diry );

		bullets.add( b );
	}
}
