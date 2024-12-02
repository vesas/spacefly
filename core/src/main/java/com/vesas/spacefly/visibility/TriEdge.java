package com.vesas.spacefly.visibility;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.vesas.spacefly.util.PolyUtils;

public class TriEdge 
{
	public Triangle tri;
	public Edge e;
	public Vector2 lGate = new Vector2();
	public Vector2 rGate = new Vector2();
	
	
	public TriEdge()
	{
		lGate.x = 0.0f;
		lGate.y = 0.0f;
		rGate.x = 0.0f;
		rGate.y = 0.0f;
		e = null;
		tri = null;
	}
	
	public void createInitialGate( final Vector2 viewPoint )
	{
		final Vector2 p1 = e.getEndPoint1().point;
		final Vector2 p2 = e.getEndPoint2().point;
		
		final boolean p2Left = PolyUtils.isCounterClockwise(p1.x-viewPoint.x, p1.y-viewPoint.y, p2.x-viewPoint.x, p2.y-viewPoint.y);
		
		if( p2Left ) {
			rGate.x = p1.x;
			rGate.y = p1.y;
			
			lGate.x = p2.x;
			lGate.y = p2.y;
		}
		else {
			rGate.x = p2.x;
			rGate.y = p2.y;
			
			lGate.x = p1.x;
			lGate.y = p1.y;
		}
		
		return;
	}
	
	private static Vector2 temp1 = new Vector2();
	private static Vector2 temp2 = new Vector2();
	private static Vector2 temp3 = new Vector2();
	private static Vector2 temp4 = new Vector2();
	
	/**
	 * Assumes gates are in left/right order
	 * @param viewpoint
	 * @param from
	 * @return
	 */
	public boolean narrowGate( final Vector2 viewpoint, final TriEdge from )
	{
		float flx = from.lGate.x;
		float fly = from.lGate.y;
		
		float frx = from.rGate.x;
		float fry = from.rGate.y;
		
		final boolean needsLeftNarrow 
				= PolyUtils.isClockwise(	lGate.x-viewpoint.x, lGate.y-viewpoint.y, 
											flx-viewpoint.x, fly-viewpoint.y);
		
		final boolean needsRightNarrow 
				= PolyUtils.isCounterClockwise(	rGate.x-viewpoint.x, rGate.y-viewpoint.y, 
												frx-viewpoint.x, fry-viewpoint.y);
		
		if( needsLeftNarrow )
		{
			temp1.x = flx-viewpoint.x;
			temp1.y = fly-viewpoint.y;
			
			// start2
			// the left gate
			temp2.x = lGate.x;
			temp2.y = lGate.y;
			
			// direction2
			// direction from right gate to left gate
			temp3.x = lGate.x - rGate.x;
			temp3.y = lGate.y - rGate.y;
			
			float d = Intersector.intersectRayRay(temp2,temp3,viewpoint,temp1 );
			
			// d should always be positive number at this point
			
			// intersectionx/y will be the intersection point and new gate
			float intersectionx = temp2.x + temp3.x * d;
			float intersectiony = temp2.y + temp3.y * d;
			
			lGate.x = intersectionx;
			lGate.y = intersectiony;
		}
		
		if( needsRightNarrow )
		{
			// direction1
			// from viewpoint to right gate
			temp1.x = frx-viewpoint.x;
			temp1.y = fry-viewpoint.y;
			
			// start2
			// the right gate
			temp2.x = rGate.x;
			temp2.y = rGate.y;
			
			// direction2
			// direction from right gate to left gate
			temp3.x = lGate.x - rGate.x;
			temp3.y = lGate.y - rGate.y;
			
			float d = Intersector.intersectRayRay(temp2,temp3,viewpoint,temp1 );
			
			// d should always be positive number at this point
			
			// temp4 will be the intersection point and new gate
			float intersectionx = temp2.x + temp3.x * d;
			float intersectiony = temp2.y + temp3.y * d;
			
			rGate.x = intersectionx;
			rGate.y = intersectiony;
		}
		
		return needsLeftNarrow || needsRightNarrow;
	}
	
}
