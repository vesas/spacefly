package com.vesas.spacefly.visibility;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class VisibilityPoly
{
	private Pool<Vector2> VecPool = new Pool<Vector2>() {

		@Override
		protected Vector2 newObject() 
		{
			return new Vector2();
		}
	};
	
	
	private Array<Vector2> triPoints = new Array<Vector2>();
	
	public Array<Vector2> getTriEndPoints()
	{
		return triPoints;
	}
	
	public void addHit( Vector2 first, Vector2 second ) 
	{
		triPoints.add( first );
		triPoints.add( second );
	}
	
	public Vector2 getVec()
	{
		return VecPool.obtain();
	}
	
	public void clear()
	{
		int size = triPoints.size;
		for( int i = 0; i < size ; i++ )
		{
			VecPool.free(triPoints.get( i ));
		}
		triPoints.clear();
	}
}
