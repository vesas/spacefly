package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Region
{
	private Array<MetaFeature> arr = new Array<MetaFeature>();
	
	public Array<MetaFeature> getMetaList()
	{
		return arr;
	}

	public int getSize()
	{
		return arr.size;
	}
	
	public boolean canAdd( MetaFeature metaFeature )
	{
		final Rectangle rect = metaFeature.getBounds();
		
		for( MetaFeature feat : arr )
		{
			if( feat.overlaps( rect ) )
				return false;
		}
		
		return true;
	}
	
	public void add( MetaFeature feat )
	{
		arr.add( feat );
	}
}
