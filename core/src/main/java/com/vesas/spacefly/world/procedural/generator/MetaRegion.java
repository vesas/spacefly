package com.vesas.spacefly.world.procedural.generator;

public class MetaRegion
{
	
	private MetaRectangleRoom root;

	public MetaRegion()
	{
		
	}

	public void addRoom( MetaRectangleRoom room )
	{
		root = room;
	}
	
	
	public void stroke(  Region region )
	{
		root.stroke( region );
	}
}
