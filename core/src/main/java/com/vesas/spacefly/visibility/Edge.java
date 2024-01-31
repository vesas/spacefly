package com.vesas.spacefly.visibility;

import util.PolyUtils;

import com.badlogic.gdx.math.Vector2;

public class Edge
{
	public int procRank = 0;
	
	private Triangle t1;
	private Triangle t2;
	
	private EndPoint p1;
	private EndPoint p2;
	
	private boolean boundary = false;
	
	public EndPoint getEndPoint1()
	{
		return p1;
	}
	
	public EndPoint getEndPoint2()
	{
		return p2;
	}
	
	public boolean isBoundary()
	{
		return boundary;
	}
	
	public void addTriangle( final Triangle tri )
	{
		if( t1 == null )
		{
			t1 = tri;
		}
		else
		{
			t2 = tri;
		}
	}
	
	public void setBoundary( final boolean value )
	{
		boundary = value;
	}
	
	public Edge( final EndPoint p1, final EndPoint p2 )
	{
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public EndPoint getOtherEnd( final EndPoint e )
	{
		if( e == p1 )
		{
			return p2;
		}
		else
		{
			return p1;
		}	
	}
	
	public Triangle getOtherTriangle( Triangle t )
	{
		if( t == t1 )
			return t2;
		else
			return t1;
	}
	
	public boolean canSeeFromGate( Vector2 start, Vector2 lGate, Vector2 rGate )
	{
		Vector2 v1 = p1.point;
		Vector2 v2 = p2.point;
		
		boolean v2Right = PolyUtils.isClockwise(	v1.x-start.x, v1.y-start.y, 
													v2.x-start.x, v2.y-start.y);
		
		float rx = v1.x;
		float ry = v1.y;
		float lx = v2.x;
		float ly = v2.y;
		if( v2Right )
		{
			float tx = lx;
			float ty = ly;
			ly = ry;
			lx = rx;
			rx = tx;
			ry = ty;
		}
		
		boolean leftIn = PolyUtils.isCounterClockwise( rx-start.x, ry-start.y, lGate.x-start.x, lGate.y-start.y);
		boolean rightIn = PolyUtils.isClockwise( lx-start.x, ly-start.y, rGate.x-start.x, rGate.y-start.y);
		
		return leftIn && rightIn;
	}
	
	public String toString()
	{
		return "(p1: " + p1 + " p2: " + p2 + " bound: " + boundary + ")";
	}
}
