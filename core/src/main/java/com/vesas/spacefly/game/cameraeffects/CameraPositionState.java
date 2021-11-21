package com.vesas.spacefly.game.cameraeffects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class CameraPositionState
{
	private static Array<Shake> active = new Array<Shake>(false,32);
	
	private static Vector2 temp = new Vector2();
	
	public static void performEffect( Vector2 start, Vector3 cameraPos )
	{
		temp.x = 0.0f;
		temp.y = 0.0f;
		
		for( int i = 0; i < active.size; i++ )
		{
			Shake shake = active.get( i );
			
			shake.performOn( temp );
		}
		
		cameraPos.x = start.x + temp.x;
		cameraPos.y = start.y + temp.y;
	}
	
	public static void tick( float delta )
	{
		for( int i = 0; i < active.size; i++ )
		{
			Shake shake = active.get( i );
			
			if( shake.isDead() )
				continue;
			
			shake.tick( delta );
		}
		
		for( int i = 0; i < active.size; i++ )
		{
			Shake shake = active.get( i );
			
			if( shake.isDead() )
				active.removeIndex(i);
		}
	}
	
	public static void addEffect( Shake shake )
	{
		active.add( shake );
	}
}
