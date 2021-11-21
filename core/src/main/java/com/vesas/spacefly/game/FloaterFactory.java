package com.vesas.spacefly.game;

import com.badlogic.gdx.utils.Array;

public class FloaterFactory
{
	private static Array<Floater> pool = new Array<Floater>(false,32);
	
	static public Floater getFloater()
	{
		if( pool.size > 0 )
		{
			Floater f = pool.pop();
			f.init();
			return f;
		}
		
		return Floater.newInstance();
	}
	
	static public void returnFloater( Floater f )
	{
		pool.add( f );
	}
}
