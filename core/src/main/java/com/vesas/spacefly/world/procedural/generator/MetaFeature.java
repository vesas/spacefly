package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public interface MetaFeature
{
	public int getId();
	
	public boolean overlaps( Rectangle rect );
	
	public Rectangle getBounds();

	public Array<MetaPortal> getPortalArray(MetaPortal exclude);

	public void closePortal(MetaPortal portal);
	
}
