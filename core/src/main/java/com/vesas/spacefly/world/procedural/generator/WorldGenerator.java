package com.vesas.spacefly.world.procedural.generator;

import java.util.Random;

public class WorldGenerator
{

	private static Random rand = new Random(12);
	
	public MetaRoom create()
	{
		MetaRoom room1 = new MetaRoom(10,15);
//		room1.setPosition( 20,20 );
		
		MetaRoom room2 = new MetaRoom(10, 10);
		
		MetaExit exit = new MetaExit();
		
		exit.addRoom( room1 );
		exit.addRoom( room2 );
		
		return room1;
	}
	
	private void stroke( Region region )
	{
		MetaRegion zone = new MetaRegion();
		
		zone.addRoom( create() );
		
		
		zone.stroke( region );
	}
	
	public Region createRegion( int edgeSize )
	{
//		Region region = new Region( edgeSize );
//		region.init();
		
//		stroke( region );
		
//		return region;
		
		return null;
	}
	
	/*
	public byte[] createMap( int edgeSize )
	{
		edgeLen = edgeSize;
		
		int size = edgeLen*edgeLen;
		map = new byte[size];
		
		for (int i = 0; i < size; i++)
		{
			float ran = rand.nextFloat();
			
			if( ran < 0.045 )
			{
				map[i] = 0; // land	
			}
			else
			{
				map[i] = 1; // sea	
			}
		}
		
		for( int i = 0; i < 15 ; i++ )
			evolve(i+1);

		
		return map;
		//checkCorners( 123 );
	}
	*/
	
	/*
	public void evolve(int seed)
	{
		for (int y = 0; y < edgeLen; y++)
		{
			for (int x = 0; x < edgeLen; x++)
			{
				float prob = 0.0f;
				
				if (safeGetMap( x, y+1 ) == 0)
				{
					prob += 0.15;
				}
				
				if (safeGetMap( x, y-1 ) == 0)
				{
					prob += 0.15;
				}
				
				if (safeGetMap( x-1, y ) == 0)
				{
					prob += 0.15;
				}
				
				if (safeGetMap( x+1, y ) == 0)
				{
					prob += 0.15;
				}
				
				if (rand.nextFloat() < prob)
				{
					setMap(x,y, (byte)0 );
				}
			}
		}
	}
	*/
	
	
}
