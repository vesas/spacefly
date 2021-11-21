package com.vesas.spacefly.visibility;

class Triangle
{
	private EndPoint [] points = new EndPoint[3];
	private Edge [] edges = new Edge[3];
	
	public void setEndPoints( final EndPoint e1, final EndPoint e2, final EndPoint e3 )
	{
		points[0] = e1;
		points[1] = e2;
		points[2] = e3;
	}
	
	public void setEdges( final Edge e1, final Edge e2, final Edge e3 )
	{
		edges[0] = e1;
		edges[1] = e2;
		edges[2] = e3;
	}
	
	public EndPoint getThirdEndPoint( final Edge goingIn )
	{
		final EndPoint t1 = goingIn.getEndPoint1();
		final EndPoint t2 = goingIn.getEndPoint2();
		
		for( int i = 0 ; i < 3; i++ )
		{
			if( !points[i].isSamePoint( t1 ) && !points[i].isSamePoint( t2 ) )
				return points[i];	
		}
		
		return null;
	}
	
	public EndPoint getPoint( int i )
	{
		return points[i];
	}
	
	public Edge getEdge( int i )
	{
		return edges[i];
	}
	
	public String toString()
	{
		return "(" + points[0] + " " + points[1] + " " + points[2] + ")";
	}
}
