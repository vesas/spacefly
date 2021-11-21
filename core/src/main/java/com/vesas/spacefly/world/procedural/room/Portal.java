package com.vesas.spacefly.world.procedural.room;

import com.badlogic.gdx.math.Vector2;

// portal means the two points which connect this feature to other feature
public class Portal
{

	public Vector2 center = new Vector2();
	public Vector2 dir = new Vector2(); // out from the feature
	public float width;
}
