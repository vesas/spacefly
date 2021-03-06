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

	public void addAll( Array<MetaFeature> feats )
	{
		arr.addAll(feats);
	}

	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();

		buf.append("Region size: " + arr.size);

		for( MetaFeature feat : arr )
		{
			buf.append("\n");
			buf.append(feat.toString());
		}

		return buf.toString();
	}
}
