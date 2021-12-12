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
		if( a.p.x < b.p.x ) 
		{
			return -1;
		}
		else if( a.p.x > b.p.x )
		{
			return 1;	
		}

		// x-axis equal, compare y-axis
		if( a.p.y < b.p.y ) 
		{
			return -1;
		}
		else if( a.p.y > b.p.y )
		{
			return 1;
		}
		
		// they are equal
		return 0;
	 }
	  
	 
}
