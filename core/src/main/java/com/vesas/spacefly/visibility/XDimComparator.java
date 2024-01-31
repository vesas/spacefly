package com.vesas.spacefly.visibility;

import java.util.Comparator;

/**
 * Compares points first by x-axis, then y-axis.
 */
class XDimComparator implements Comparator<EndPoint>
{

	@Override 
	public int compare( final EndPoint a, final EndPoint b) 
	{ 
		// compare x-axis
		if( a.point.x < b.point.x ) 
		{
			return -1;
		}
		else if( a.point.x > b.point.x )
		{
			return 1;	
		}

		// x-axis equal, compare y-axis
		if( a.point.y < b.point.y ) 
		{
			return -1;
		}
		else if( a.point.y > b.point.y )
		{
			return 1;
		}
		
		// they are equal
		return 0;
	}
}
