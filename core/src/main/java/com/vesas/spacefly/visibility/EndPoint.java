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
	
	public boolean isSamePoint( EndPoint o )
	{
		return 	Math.abs( o.point.x - point.x ) < 0.00001f &&
				Math.abs( o.point.y - point.y ) < 0.00001f;
	}
	
	public boolean isSamePoint( float x, float y )
	{
		return 	Math.abs( x - point.x ) < 0.00001f &&
				Math.abs( y - point.y ) < 0.00001f;
	}
	
	
	public String toString()
	{
		return "(" + point.x + ":" + point.y + ")";
	}
}
