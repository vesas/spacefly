package com.vesas.spacefly.world.procedural.room.rectangleroom;

import java.util.EnumMap;

import com.vesas.spacefly.world.procedural.GenSeed;

public enum ExitDir
{
	N,E,S,W;

	private static EnumMap<ExitDir, ExitDir> opposites = new EnumMap<ExitDir, ExitDir>(ExitDir.class);

	static {
		opposites.put(N, S);
		opposites.put(S, N);
		opposites.put(E, W);
		opposites.put(W, E);
	}
	
	public ExitDir getOpposite()
	{
		return opposites.get(this);
	}
	
	public static ExitDir getRandom()
	{
		return ExitDir.values()[GenSeed.random.nextInt( ExitDir.values().length )];
	}
	
	public static ExitDir getRandomExcluding( ExitDir exclude )
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
	
	public static ExitDir getRandomExcluding( boolean [] exitDirs )
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
