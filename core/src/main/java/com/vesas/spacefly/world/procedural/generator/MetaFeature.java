package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Rectangle;

public interface MetaFeature
{

	public boolean overlaps( Rectangle rect );
	
	public Rectangle getBounds();
	
}
