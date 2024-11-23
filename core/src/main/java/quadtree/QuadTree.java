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
	private AABB boundary;

	public AABB getBoundary() {
		return boundary;
	}

	private List<Point> points = new ArrayList<Point>();

	public List<Point> getPoints() {
		return points;
	}

	// Children
	private QuadTree northWest = null;
	private QuadTree northEast = null;
	private QuadTree southWest = null;
	private QuadTree southEast = null;
	
	public QuadTree getNorthWest() {
		return northWest;
	}

	public QuadTree getNorthEast() {
		return northEast;
	}

	public QuadTree getSouthWest() {
		return southWest;
	}

	public QuadTree getSouthEast() {
		return southEast;
	}

	public QuadTree(AABB _boundary)
	{
		boundary = _boundary;
	}

	public boolean insert( Point p )
	{
		// Ignore objects which do not belong in this quad tree
		if (!boundary.containsPoint(p))
	    	return false; // object cannot be added

		if (northWest == null) {
			// If all the points are the same, we can just add the point to the list, no need to subdivide
			if (points.stream().allMatch(point -> point.x == p.x && point.y == p.y)) {
				points.add( p );
				return true;
			}
			
			if (points.size() < QT_NODE_CAPACITY) {
				points.add(p);
				return true;
			}
		}

		points.add( p );

		// Otherwise, we need to subdivide then add the point to whichever node will accept it

		if (northWest == null)
			subdivide();

		for(Point point : points) {

			if (northWest.insert(point))
				continue; 
			if (northEast.insert(point)) 
				continue;
			if (southWest.insert(point)) 
				continue;
			if (southEast.insert(point)) 
				continue;
		}

		points.clear();
	    return true;	
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
		
		Point half = new Point(halfx, halfy);
		
		northWest = new QuadTree( new AABB(new Point(centerx-halfx, centery+halfy), half) );
		northEast = new QuadTree( new AABB(new Point(centerx+halfx, centery+halfy), half) );
		southWest = new QuadTree( new AABB(new Point(centerx-halfx, centery-halfy), half) );
		southEast = new QuadTree( new AABB(new Point(centerx+halfx, centery-halfy), half) );
	}

	/**
	 * Returns all points within the given range
	 */
	public List<Point> queryRange(AABB range) 
	{
		ArrayList<Point> results = new ArrayList<Point>();
		
	    // Automatically abort if the range does not collide with this quad
	    if (!boundary.intersectsAABB(range))
	      return results; // empty list

	    // Check objects at this quad level
	    for( Point p : points )
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
