package com.vesas.spacefly.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PooledLinkedList;
import com.badlogic.gdx.utils.TimeUtils;
import com.vesas.spacefly.GameScreen;

public class BaseBullets
{
	private static int MAXSIZE = 364;

	protected Array<AbstractBullet> deadPool = new Array<AbstractBullet>(false, 16);
	protected Array<AbstractBullet> bulletsToBeRemoved = new Array<AbstractBullet>(false, 16);

	protected PooledLinkedList<AbstractBullet> bullets = new PooledLinkedList<AbstractBullet>(MAXSIZE);

	public void preRemove(AbstractBullet b)
	{
		if (!bulletsToBeRemoved.contains(b, true))
			bulletsToBeRemoved.add(b);

		bullets.iter();
		for (int i = 0; i <= MAXSIZE; i++)
		{
			AbstractBullet bn = bullets.next();

			if (bn == null)
				break;

			if (bn == b)
			{
				bullets.remove();
				break;
			}
		}
	}

	public void removeBullets()
	{
		final int size = bulletsToBeRemoved.size;

		if (size == 0)
			return;

		for (int i = 0; i < size; i++)
		{
			AbstractBullet b = bulletsToBeRemoved.get(i);
			b.deactivate();

			deadPool.add(b);
		}

		bulletsToBeRemoved.clear();
	}

	public void tick(float delta)
	{
		long time = TimeUtils.millis();

		bullets.iter();

		for (int i = 0; i <= MAXSIZE; i++)
		{
			AbstractBullet b = bullets.next();

			if (b == null)
				break;

			if (time - b.creationTime > 4000)
			{
				bulletsToBeRemoved.add(b);

				bullets.remove();
			}

		}
	}

	public void draw(GameScreen screen)
	{
		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA , GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		PooledLinkedList<AbstractBullet> localBullets = bullets;

		localBullets.iter();
		for (int i = 0; i <= MAXSIZE; i++)
		{
			AbstractBullet b = localBullets.next();

			if (b == null)
				break;

			b.draw(screen.worldBatch);
		}

	}
}
