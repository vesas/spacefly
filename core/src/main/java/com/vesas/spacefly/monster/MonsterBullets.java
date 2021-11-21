package com.vesas.spacefly.monster;

import com.vesas.spacefly.game.AbstractBullet;
import com.vesas.spacefly.game.BaseBullets;

public class MonsterBullets extends BaseBullets {
	public static MonsterBullets INSTANCE = new MonsterBullets();

	private MonsterBullets() {
	}

	public void fireBullet(float posx, float posy, float dirx, float diry, int type) 
	{
		if (deadPool.size > 0) 
		{
			AbstractBullet bul = deadPool.removeIndex(deadPool.size - 1);
			if (bul != null) 
			{
				bul.body.setTransform(posx, posy, 0f);
				bul.body.setLinearVelocity(dirx, diry);

				bul.body.setAwake(true);
				bul.body.setActive(true);

				((MonsterBullet) bul).type = type;
				bul.resetCreationTime();
				bullets.add(bul);
				return;
			}

		}

		MonsterBullet b = new MonsterBullet(posx, posy, dirx, diry, type);

		bullets.add(b);

	}

}
