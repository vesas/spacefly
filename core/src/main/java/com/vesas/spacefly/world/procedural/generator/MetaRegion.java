package com.vesas.spacefly.world.procedural.generator;

public class MetaRegion
{
	
	private MetaRoom root;

	public MetaRegion()
	{
		
	}

	public void addRoom( MetaRoom room )
	{
		root = room;
	}
	
	
	public void stroke(  Region region )
	{
		root.stroke( region );
	}
}
