package com.vesas.spacefly.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.vesas.spacefly.monster.SlurgMonster;

public class RayCastClosestCB implements RayCastCallback
{
	public float closest = 100.0f;
	public Vector2 blockNormal = new Vector2();
	public Vector2 start;

	public RayCastClosestCB(Vector2 start)
	{
		this.start = start;
	}

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
	{
		Object o = fixture.getBody().getUserData();

		if (o instanceof SlurgMonster)
			return -1.0f;

		// float tempDist = start.dst( point );
		float tempDist = fraction;

		if (tempDist < closest)
		{
			closest = tempDist;

			blockNormal.x = normal.x;
			blockNormal.y = normal.y;
		}

		return fraction;
	}

}