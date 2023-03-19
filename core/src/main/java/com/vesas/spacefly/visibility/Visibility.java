package com.vesas.spacefly.visibility;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.math.DelaunayTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ShortArray;
import com.vesas.spacefly.DebugHelper;

import util.FrameTime;
import util.Log;
import util.PolyUtils;

public class Visibility
{
	public List<EndPoint> endpoints = new LinkedList<EndPoint>();
	public List<Edge> edges = new LinkedList<Edge>();
	private List<Triangle> triangles = new ArrayList<Triangle>();
	
	private VisibilityPoly visibPoly = new VisibilityPoly(); 
	
	private Vector2 center = new Vector2();
	
	private ShortArray triShorts;
	private float [] points;
	
	public ShortArray getTriangles()
	{
		return triShorts;
	}
	
	public float[] getPoints()
	{
		return points;
	}
	
	public VisibilityPoly getVisibPoly()
	{
		return visibPoly;
	}
	
	public void initLoad()
	{
		edges.clear();
		endpoints.clear();
	}
	
	private EndPoint getOrCreateEndPoint( float x1, float y1 )
	{	
		for( EndPoint e : endpoints )
		{
			if( e.isSamePoint( x1, y1 ) )
				return e;
		}
		
		EndPoint p1 = new EndPoint( x1, y1 );
		endpoints.add( p1 );
		return p1;
	}
	
	private EndPoint getEndPoint( float x1, float y1 )
	{
		for( EndPoint e : endpoints )
		{
			if( e.isSamePoint( x1, y1 ) )
				return e;
		}
		
		return null;
	}
	
	static private Array<EndPoint> roomPoints = new Array<EndPoint>();
	
	public void addSegment( float x1, float y1, float x2, float y2 )
	{	
		EndPoint p1 = getOrCreateEndPoint( x1, y1 );
		EndPoint p2 = getOrCreateEndPoint( x2, y2 );
		
		if( !roomPoints.contains( p1 , true ) )
			roomPoints.add( p1 );
		
		if( !roomPoints.contains( p2 , true ) )
			roomPoints.add( p2 );
	
		Edge edge = new Edge(p1,p2);
		p1.addEdge( edge );
		p2.addEdge( edge );

		edge.setBoundary( true );
    
		edges.add(edge);
	}
	
	private DelaunayTriangulator triangulator;
	
	public void startLoad()
	{
		triangulator = new DelaunayTriangulator();
	}
	
	public void startConvexArea()
	{
		roomPoints.clear();
	}
	
	public void finishConvexArea()
	{
		triangulateRoom();
	}
	
	public void finishLoad()
	{
//		triangulate();
	}
	
	/*
	 * Find the edge between points
	 */
	private Edge findEdge( EndPoint e1, EndPoint e2 )
	{
		for( int i = 0; i < edges.size(); i++ )
        {
        	Edge edge = edges.get( i );
        	
        	if( (edge.getEndPoint1().isSamePoint( e1 ) &&
        		edge.getEndPoint2().isSamePoint( e2 ) ) 
        		||
        		(edge.getEndPoint1().isSamePoint( e2 ) &&
                edge.getEndPoint2().isSamePoint( e1 ) ) 
                )
        	{
        		return edge;
        	}
        }
		 return null;
	}
	
	private void triangulateRoom()
	{
		points = new float[roomPoints.size * 2];
		
        int count = 0;
        for( int i = 0; i < roomPoints.size; i++ )
        {
        	EndPoint e = roomPoints.get( i );
        	points[count] = e.p.x;
        	count++;
        	points[count] = e.p.y;
        	count++;
        }
        
        ShortArray tris = triangulator.computeTriangles(points, false);
        
        for( int i = 0; i < tris.size ; i = i + 3 )
		{
			short j1 = tris.get(i);
			EndPoint e1 = roomPoints.get( j1 );
			
			short j2 = tris.get(i+1);
			EndPoint e2 = roomPoints.get( j2 );
			
			short j3 = tris.get(i+2);
			EndPoint e3 = roomPoints.get( j3 );
			
			// virtual edges (not boundary)
			Edge edge1 = e1.getDirectConnectionTo( e2 );

			// no direct connection, create new virtual edge
			if( edge1 == null )
			{
				Edge e = findEdge( e1, e2 );
				
				if( e == null )
					e = new Edge(e1,e2);
				
				e1.addEdge( e );
				e2.addEdge( e );
				edges.add(e);
				edge1 = e;
			}
			
			Edge edge2 = e2.getDirectConnectionTo( e3 );
			
			if( edge2 == null )
			{
				Edge e = findEdge( e2,e3);
						
				if( e == null )
					e = new Edge(e2,e3);
				
				e2.addEdge( e );
				e3.addEdge( e );
				edges.add(e);
				edge2 = e;
			}

			Edge edge3 = e3.getDirectConnectionTo( e1 );

			// virtual edge (not boundary)
			if( edge3 == null )
			{
				Edge e = findEdge( e3, e1 );
				
				if( e == null )
					e = new Edge(e3,e1);
				
				e3.addEdge( e );
				e1.addEdge( e );
				edges.add(e);
				edge3 = e;
			}
			
			Triangle t = new Triangle();
			t.setEndPoints( e1,e2,e3 );
			
			edge1.addTriangle( t );
			edge2.addTriangle( t );
			edge3.addTriangle( t );
			
			t.setEdges(edge1, edge2, edge3);
		
			triangles.add( t );
		}
        
	}
	
	
	public void setLightLocation(float x, float y)
	{
		center.x = x;
		center.y = y;
	}
	
	private static Pool<TriEdge> triEdgePool = new Pool<TriEdge>() {
		
		@Override
		protected TriEdge newObject() 
		{
			return new TriEdge();
		}
	};
	
	private static Deque<TriEdge> toProcess = new ArrayDeque<TriEdge>();
	
	Vector2 enter1 = new Vector2();
	Vector2 enter2 = new Vector2();
	
	public void sweep()
	{
		
		visibPoly.clear();
		toProcess.clear();
		
		final Triangle start = findStartTriangle();

		long startNano = System.nanoTime();

		if( start == null )
		{
			// went outside 
			return;
		}

		for(int i = 0; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			edge.procRank = 0;
		}
		
		Edge e1 = start.getEdge( 0 );
		Edge e2 = start.getEdge( 1 );
		Edge e3 = start.getEdge( 2 );
		
		if( !e1.isBoundary() )
		{
			// triedge is the edge out from a triangle into another triangle
			TriEdge te1 = triEdgePool.obtain();
			
			te1.e = e1;
			te1.tri = start;
			te1.createInitialGate( center );
			toProcess.addFirst( te1 );	
		}
		else
		{
			visibPoly.addHit( e1.getEndPoint1().p, e1.getEndPoint2().p );
		}
		
		if( !e2.isBoundary() )
		{
			// triedge is the edge out from a triangle into another triangle
			
			TriEdge te2 = triEdgePool.obtain();
			te2.e = e2;
			te2.tri = start;
			te2.createInitialGate( center );
			toProcess.addFirst( te2 );
		}
		else
		{
			visibPoly.addHit( e2.getEndPoint1().p, e2.getEndPoint2().p );
		}
		
		if( !e3.isBoundary() )
		{
			TriEdge te3 = triEdgePool.obtain();
			te3.e = e3;
			te3.tri = start;
			te3.createInitialGate( center );
			toProcess.addFirst( te3 );
		}
		else
		{
			visibPoly.addHit( e3.getEndPoint1().p, e3.getEndPoint2().p );
		}
		
		long count = 0;
		
		while( !toProcess.isEmpty() )
		{
			count++;
			final TriEdge current = toProcess.removeFirst();
			
			final Triangle targetTri = current.e.getOtherTriangle( current.tri );
			
			if( targetTri != null )
			{
				addBoundarySlices( center, current, targetTri );
			}
			
			triEdgePool.free(current);

			if(DebugHelper.FRAMETIME_DEBUG) {
				if(count > 1000) {
					Log.debug("processed over 1000 TriEdges");
				}
				else if(count > 10000) {
					Log.debug("processed over 10000 TriEdges");
				}
			}
		}
		long endNano = System.nanoTime();
		FrameTime.visib = endNano - startNano;
	}
	
	private void addBoundarySlices( Vector2 center, TriEdge current, Triangle tri )
	{
		final Vector2 lGate = current.lGate;
		final Vector2 rGate = current.rGate;
		
		Edge edge1 = null;
		Edge edge2 = null;
		
		if( current.e == tri.getEdge( 0 ) )
		{
			edge1 = tri.getEdge( 1 );
			edge2 = tri.getEdge( 2 );
		}
		
		if( current.e == tri.getEdge( 1 ) )
		{
			edge1 = tri.getEdge( 0 );
			edge2 = tri.getEdge( 2 );
		}
		
		if( current.e == tri.getEdge( 2 ) )
		{
			edge1 = tri.getEdge( 0 );
			edge2 = tri.getEdge( 1 );
		}
		
		if( !isClockwise( edge1, edge2 ) )
		{
			Edge tempEdge = edge1;
			edge1 = edge2;
			edge2 = tempEdge;
			tempEdge = null;
		}
		
		final EndPoint oppositeEnd = tri.getThirdEndPoint( current.e );
		
		final boolean centerToLeftOfLeftGate = PolyUtils.isCounterClockwise( 	lGate.x-center.x, lGate.y-center.y,  oppositeEnd.p.x-center.x, oppositeEnd.p.y-center.y);
		final boolean centerToRightOfRightGate = PolyUtils.isClockwise( 		rGate.x-center.x, rGate.y-center.y,  oppositeEnd.p.x-center.x, oppositeEnd.p.y-center.y);
		
		final boolean centerVisible = !centerToLeftOfLeftGate && !centerToRightOfRightGate;
		
		Vector2 p1 = current.e.getEndPoint1().p;
		Vector2 p2 = current.e.getEndPoint2().p;
		
		final boolean p2Right = PolyUtils.isClockwise(p1.x-center.x, p1.y-center.y, p2.x-center.x, p2.y-center.y);
		
		float rx = p1.x;
		float ry = p1.y;
		float lx = p2.x;
		float ly = p2.y;
		if( p2Right )
		{
			float tx = lx;
			float ty = ly;
			ly = ry;
			lx = rx;
			rx = tx;
			ry = ty;
		}
		else
		{
			Vector2 temp = p1;
			p1 = p2;
			p2 = temp;
		}
		
		boolean leftGateNotNarrowed  = Math.abs( lx - lGate.x) < EPS && Math.abs( ly - lGate.y) < EPS;
		boolean rightGateNotNarrowed = Math.abs( rx - rGate.x) < EPS && Math.abs( ry - rGate.y) < EPS;
		
		if( centerVisible )
		{
			if( edge2.isBoundary() )
			{
				if( rightGateNotNarrowed )
				{
					visibPoly.addHit( edge2.getEndPoint1().p, edge2.getEndPoint2().p );
				}
				else
				{
					// right "wing", right gate
					final boolean rwrg = Intersector.intersectLines(center.x, center.y, 
							rGate.x + (rGate.x-center.x)*15.0f, rGate.y + (rGate.y-center.y)*15.0f, 
							edge2.getEndPoint1().p.x,  edge2.getEndPoint1().p.y, 
							edge2.getEndPoint2().p.x, edge2.getEndPoint2().p.y, intersection3 );
					
					
					Vector2 inter2 = new Vector2( intersection3 );

					visibPoly.addHit( oppositeEnd.p, inter2);
				}
			}
			
			if( edge1.isBoundary() )
			{
				if( leftGateNotNarrowed )
				{
					visibPoly.addHit( edge1.getEndPoint1().p, edge1.getEndPoint2().p );
				}
				else
				{
					// left "wing", left gate
					final boolean lwlg = Intersector.intersectLines(center.x, center.y, 
							lGate.x + (lGate.x-center.x)*15.0f, lGate.y + (lGate.y-center.y)*15.0f, 
							edge1.getEndPoint1().p.x,  edge1.getEndPoint1().p.y, 
							edge1.getEndPoint2().p.x, edge1.getEndPoint2().p.y, intersection3 );
					
					Vector2 inter1 = new Vector2( intersection3 );
					
					visibPoly.addHit( oppositeEnd.p, inter1);
				}
			}
			
		}
		
		if( centerToLeftOfLeftGate && edge2.isBoundary())
		{
			if( rightGateNotNarrowed )
			{
				// right "wing", left gate
				final boolean rwlg = Intersector.intersectLines(center.x, center.y, 
						lGate.x + (lGate.x-center.x)*15.0f, lGate.y + (lGate.y-center.y)*15.0f, 
						edge2.getEndPoint1().p.x,  edge2.getEndPoint1().p.y, 
						edge2.getEndPoint2().p.x, edge2.getEndPoint2().p.y, intersection3 );
				
				Vector2 inter1 = new Vector2( intersection3 );
				
				visibPoly.addHit( inter1, p2);
				
			}
			else
			{
				// right "wing", left gate
				final boolean rwlg = Intersector.intersectLines(center.x, center.y, 
						lGate.x + (lGate.x-center.x)*15.0f, lGate.y + (lGate.y-center.y)*15.0f, 
						edge2.getEndPoint1().p.x,  edge2.getEndPoint1().p.y, 
						edge2.getEndPoint2().p.x, edge2.getEndPoint2().p.y, intersection3 );
				
				Vector2 inter1 = new Vector2( intersection3 );
				
				// right "wing", right gate
				final boolean rwrg = Intersector.intersectLines(center.x, center.y, 
						rGate.x + (rGate.x-center.x)*15.0f, rGate.y + (rGate.y-center.y)*15.0f, 
						edge2.getEndPoint1().p.x,  edge2.getEndPoint1().p.y, 
						edge2.getEndPoint2().p.x, edge2.getEndPoint2().p.y, intersection3 );
				
				Vector2 inter2 = new Vector2( intersection3 );
				
				visibPoly.addHit( inter1, inter2);
			}
		}
		
		if( centerToRightOfRightGate && edge1.isBoundary())
		{
			if( leftGateNotNarrowed )
			{
				// left "wing", right gate
				final boolean rwlg = Intersector.intersectLines(center.x, center.y, 
						rGate.x + (rGate.x-center.x)*15.0f, rGate.y + (rGate.y-center.y)*15.0f, 
						edge1.getEndPoint1().p.x,  edge1.getEndPoint1().p.y, 
						edge1.getEndPoint2().p.x, edge1.getEndPoint2().p.y, intersection3 );
				
				Vector2 inter2 = new Vector2( intersection3 );
				
				visibPoly.addHit( p1, inter2 );
			}
			else
			{
				// left "wing", left gate
				final boolean rwlg = Intersector.intersectLines(center.x, center.y, 
						lGate.x + (lGate.x-center.x)*15.0f, lGate.y + (lGate.y-center.y)*15.0f, 
						edge1.getEndPoint1().p.x,  edge1.getEndPoint1().p.y, 
						edge1.getEndPoint2().p.x, edge1.getEndPoint2().p.y, intersection3 );
				
				Vector2 inter1 = new Vector2( intersection3 );
				
				// left "wing", right gate
				final boolean rwrg = Intersector.intersectLines(center.x, center.y, 
						rGate.x + (rGate.x-center.x)*15.0f, rGate.y + (rGate.y-center.y)*15.0f, 
						edge1.getEndPoint1().p.x,  edge1.getEndPoint1().p.y, 
						edge1.getEndPoint2().p.x, edge1.getEndPoint2().p.y, intersection3 );
				
				Vector2 inter2 = new Vector2( intersection3 );
				
				visibPoly.addHit( inter1, inter2);
			}
		}
		
		
		for( int i = 0; i < 3; i++ )
		{
			Edge tempEdge = tri.getEdge(i);
			if( tempEdge != current.e && !tempEdge.isBoundary() )
			{
				if( tempEdge.canSeeFromGate( center, current.lGate, current.rGate ) )
				{
					tempEdge.procRank++;
					// triedge is the edge out from a triangle
					TriEdge triEdge = triEdgePool.obtain();
					triEdge.e = tempEdge;
					triEdge.tri = tri;
					
					triEdge.createInitialGate( center );
					triEdge.narrowGate( center, current );
					 
					toProcess.addFirst( triEdge );
				}
			}	
		}
		
	}
	
	private boolean isClockwise( Edge e1, Edge e2 )
	{
		float center1x = (e1.getEndPoint1().p.x + e1.getEndPoint2().p.x) * 0.5f;
		float center1y = (e1.getEndPoint1().p.y + e1.getEndPoint2().p.y) * 0.5f;
		
		float center2x = (e2.getEndPoint1().p.x + e2.getEndPoint2().p.x) * 0.5f;
		float center2y = (e2.getEndPoint1().p.y + e2.getEndPoint2().p.y) * 0.5f;
		
		boolean e2Right = PolyUtils.isClockwise(center1x-center.x, center1y-center.y, center2x-center.x, center2y-center.y);
		return e2Right;
	}
	
	private static Vector2 intersection3 = new Vector2();
	
	// static final private float EPS = 0.000000000000001f;
	static final private float EPS = 0.000000000001f;
	
	
	// find triangle which contains center
	// lets just brute force it
	private Triangle findStartTriangle()
	{
		final int size = triangles.size();
		for( int i = 0; i < size; i++ )
		{
			final Triangle t = triangles.get( i );

			final EndPoint e0 = t.getPoint(0);
			final float ax = e0.p.x;
			final float ay = e0.p.y;
			
			final EndPoint e1 = t.getPoint(1);
			final float bx = e1.p.x;
			final float by = e1.p.y;
			
			final EndPoint e2 = t.getPoint(2); 
			float cx = e2.p.x;
			float cy = e2.p.y;
			
			boolean found = Intersector.isPointInTriangle(center.x, center.y, ax, ay, bx, by, cx, cy);
			
			if( found )
				return t;
		}
		
		return null;
	}
	
}
