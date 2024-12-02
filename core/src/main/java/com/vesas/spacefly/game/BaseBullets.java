package com.vesas.spacefly.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.vesas.spacefly.screen.GameScreen;

public class BaseBullets
{

	protected Array<AbstractBullet> deadPool = new Array<>(false, 16);
	protected Array<AbstractBullet> bulletsToBeRemoved = new Array<>(false, 16);

	protected Array<AbstractBullet> bullets = new Array<>(false, 32);

	public void preRemove(AbstractBullet b)
	{
		if (!bulletsToBeRemoved.contains(b, true))
			bulletsToBeRemoved.add(b);

		bullets.removeValue(b, true);
	}

	public void removeBullets()
	{
		final int size = bulletsToBeRemoved.size;

		if (size == 0)
			return;

		for (int i = 0; i < size; i++)
		{
			AbstractBullet b = bulletsToBeRemoved.get(i);
			b.setActive( false );

			deadPool.add(b);
		}

		bulletsToBeRemoved.clear();
	}

	public void tick(float delta)
	{
		long time = TimeUtils.millis();

		for (int i = bullets.size - 1; i >= 0; i--)
		{
			AbstractBullet b = bullets.get(i);
			if (time - b.creationTime > 4000)
			{
				bulletsToBeRemoved.add(b);
				bullets.removeIndex(i);
			}
		}
	}

	public void draw(GameScreen screen)
	{
		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA , GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		for (AbstractBullet b : bullets)
		{
			b.draw(screen.worldBatch);
		}
	}

	public void add(AbstractBullet bullet) {
		bullets.add(bullet);
	}

	public void clear() {
		bullets.clear();
	}

	public Array<AbstractBullet> getBullets() {
		return bullets;
	}

	public void remove() {
		removeBullets();
	}
}
