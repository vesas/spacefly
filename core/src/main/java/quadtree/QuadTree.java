package quadtree;

import java.util.ArrayList;
import java.util.List;

public class QuadTree
{

	// Arbitrary constant to indicate how many elements can be stored in this
	// quad tree node
	static private int QT_NODE_CAPACITY = 4;

	// Axis-aligned bounding box stored as a center with half-dimensions
	// to represent the boundaries of this quad tree
	AABB boundary;

	private ArrayList<XY> points = new ArrayList<XY>();

	// Children
	QuadTree northWest = null;
	QuadTree northEast = null;
	QuadTree southWest = null;
	QuadTree southEast = null;
	
	public QuadTree(AABB _boundary)
	{
		boundary = _boundary;
	}

	public boolean insert( XY p )
	{
		// Ignore objects which do not belong in this quad tree
		if (!boundary.containsPoint(p))
	    	return false; // object cannot be added

	    // If there is space in this quad tree, add the object here
		if (points.size() < QT_NODE_CAPACITY)
	    {
	    	points.add( p );
	    	return true;
	    }

	    // Otherwise, we need to subdivide then add the point to whichever node will accept it
	    if (northWest == null)
	      subdivide();

	    if (northWest.insert(p)) 
	    	return true;
	    if (northEast.insert(p)) 
	    	return true;
	    if (southWest.insert(p)) 
	    	return true;
	    if (southEast.insert(p)) 
	    	return true;

	    // Otherwise, the point cannot be inserted for some unknown reason (which should never happen)
	    return false;
	}

	/**
	 * create four children which fully divide this quad into four quads of equal area
	 */
	public void subdivide()
	{
		float halfx = this.boundary.halfDimension.x * 0.5f;
		float halfy = this.boundary.halfDimension.y * 0.5f;
		
		float centerx = this.boundary.center.x;
		float centery = this.boundary.center.y;
		
		XY half = new XY(halfx, halfy);
		
		northWest = new QuadTree( new AABB(new XY(centerx-halfx, centery-halfy), half) );
		northEast = new QuadTree( new AABB(new XY(centerx+halfx, centery-halfy), half) );
		southWest = new QuadTree( new AABB(new XY(centerx-halfx, centery+halfy), half) );
		southEast = new QuadTree( new AABB(new XY(centerx+halfx, centery+halfy), half) );
	}

	public List<XY> queryRange(AABB range) 
	{
		ArrayList<XY> results = new ArrayList<XY>();
		
	    // Automatically abort if the range does not collide with this quad
	    if (!boundary.intersectsAABB(range))
	      return results; // empty list

	    // Check objects at this quad level
	    for( XY p : points )
	    {
	    	if (range.containsPoint(p))
	    		results.add( p );
	    }
	    // Terminate here, if there are no children
	    if (northWest == null)
	      return results;

	    // Otherwise, add the points from the children
	    
	    results.addAll(northWest.queryRange(range));
	    results.addAll(northEast.queryRange(range));
	    results.addAll(southWest.queryRange(range));
	    results.addAll(southEast.queryRange(range));
	    
	    return results;	
		
	}
}
