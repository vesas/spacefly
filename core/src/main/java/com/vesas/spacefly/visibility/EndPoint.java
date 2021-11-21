package com.vesas.spacefly.visibility;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EndPoint
{
	public Vector2 p = new Vector2();
	
	public EndPoint( float x, float y )
	{
		this.p.x = x;
		this.p.y = y;
	}

	private Array<Edge> edges = new Array<Edge>();
	
	public void addEdge( Edge e )
	{
		edges.add( e );
	}
	
	public boolean hasDirectConnectionTo( EndPoint e )
	{
		for( int i = 0; i < edges.size; i++ )
		{
			if( edges.get(i).getOtherEnd(this) == e )
				return true;
		}
		
		return false;
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
		return Math.abs( o.p.x - p.x ) < 0.00001f &&
			   Math.abs( o.p.y - p.y ) < 0.00001f;
	}
	
	public boolean isSamePoint( float x, float y )
	{
		return Math.abs( x - p.x ) < 0.00001f &&
			   Math.abs( y - p.y ) < 0.00001f;
	}
	
	
	public String toString()
	{
		return "(" + p.x + ":" + p.y + ")";
	}
}
