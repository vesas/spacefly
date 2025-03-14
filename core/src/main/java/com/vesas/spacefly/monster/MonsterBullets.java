package com.vesas.spacefly.monster;

import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.game.AbstractBullet;
import com.vesas.spacefly.game.BaseBullets;

public class MonsterBullets extends BaseBullets {
	public static MonsterBullets INSTANCE = new MonsterBullets();

	private MonsterBullets() {
	}

	public void fireBullet(float posx, float posy, float dirx, float diry, int type, BodyBuilder bodyBuilder) 
	{
		if (deadPool.size > 0) 
		{
			AbstractBullet bul = deadPool.pop();
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

		MonsterBullet b = new MonsterBullet(posx, posy, dirx, diry, type, bodyBuilder);

		bullets.add(b);

	}

}
