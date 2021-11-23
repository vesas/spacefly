package com.vesas.spacefly.world.procedural.room.rectangleroom;

import com.vesas.spacefly.world.procedural.GenSeed;

public enum ExitDir
{
	N,E,S,W;
	
	public ExitDir getOpposite()
	{
		if( this == S )
			return N;
		
		if( this == N )
			return S;
		
		if( this == W )
			return E;
		
		if( this == E )
			return W;
		
		return null;
			
	}
	
	static public ExitDir getRandom()
	{
		return ExitDir.values()[GenSeed.random.nextInt( ExitDir.values().length )];
	}
	
	static public ExitDir getRandomExcluding( ExitDir exclude )
	{
		ExitDir ex = ExitDir.values()[GenSeed.random.nextInt( ExitDir.values().length )];
		
		int count = 0;
		while( ex.equals( exclude ) )
		{
			ex = ExitDir.values()[GenSeed.random.nextInt( ExitDir.values().length )];
			
			if( count > 5 )
				ex = exclude.getOpposite();
			
			count++;
		}
		
		return ex;
	}
	
	static public ExitDir getRandomExcluding( boolean [] exitDirs )
	{
		ExitDir ex = ExitDir.values()[GenSeed.random.nextInt( ExitDir.values().length )];
		
		int count = 0;
		while( exitDirs[ex.ordinal()] )
		{
			ex = ExitDir.values()[GenSeed.random.nextInt( ExitDir.values().length )];
			
			if( count > 10 )
				return null;
			
			count++;
		}
		
		return ex;
	}
	
	
}
