package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Rectangle;

public interface Feature
{

	public boolean overlaps( Rectangle rect );
}
