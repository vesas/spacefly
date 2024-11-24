package com.vesas.spacefly.game.floater;

import com.badlogic.gdx.utils.Array;

public class FloaterFactory
{
	private static Array<Floater> pool = new Array<Floater>(false,32);
	
	static public Floater getFloater() {
		if( pool.size > 0 ) {
			Floater f = pool.pop();
			f.init();
			return f;
		}
		
		Floater f = new Floater();
		f.init();
		return f;
	}
	
	static public void returnFloater( Floater f ) {
		pool.add( f );
	}
}
