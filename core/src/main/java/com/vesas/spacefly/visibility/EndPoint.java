package com.vesas.spacefly.visibility;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EndPoint
{
	public Vector2 point = new Vector2();
	
	public EndPoint( float x, float y )
	{
		this.point.x = x;
		this.point.y = y;
	}

	private Array<Edge> edges = new Array<Edge>();
	
	public void addEdge( Edge e )
	{
		edges.add( e );
	}
	
	public Edge getDirectConnectionTo( EndPoint e )
	{
		for( int i = 0; i < edges.size; i++ )
		{
			if( edges.get(i).getOtherEnd(this) == e )
				return edges.get(i);
		}
		
		return null;
	}
	
	private static final float EPSILON = 0.0001f;

	public boolean isSamePoint( EndPoint o )
	{
		return 	Math.abs( o.point.x - point.x ) < EPSILON &&
				Math.abs( o.point.y - point.y ) < EPSILON;
	}
	
	public boolean isSamePoint( float x, float y )
	{
		return 	Math.abs( x - point.x ) < EPSILON &&
				Math.abs( y - point.y ) < EPSILON;
	}
	
	
	public String toString()
	{
		return "(" + point.x + ":" + point.y + ")";
	}
}
