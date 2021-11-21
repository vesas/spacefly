package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Region
{
	private static Rectangle tmpRect = new Rectangle();
	
	private MetaRoom firstRoom;

	private Array<MetaFeature> arr = new Array<MetaFeature>();
	
	public Array<MetaFeature> getMetaList()
	{
		return arr;
	}

	public int getSize()
	{
		return arr.size;
	}
	
	public boolean canAdd( MetaRoom metaRoom )
	{
		Rectangle rect = metaRoom.getBounds();
		
		for( int i = 0; i < arr.size; i++ )
		{
			MetaFeature feat = arr.get( i );
			
			if( feat.overlaps( rect ) )
				return false;
		}
		
		return true;
	}	
	
	public boolean canAdd( MetaCorridor corr )
	{
		Rectangle rect = corr.getBounds();
		
		for( int i = 0; i < arr.size; i++ )
		{
			MetaFeature feat = arr.get( i );
			
			if( feat.overlaps( rect ) )
				return false;
		}
		
		return true;
	}
	
	public void add( MetaFeature feat )
	{
		arr.add( feat );
	}
	
	public void setFirstRoom( MetaRoom aRoom )
	{
		firstRoom = aRoom;
	}
	
	public MetaRoom getFirstRoom()
	{
		return firstRoom;
	}
	
}
