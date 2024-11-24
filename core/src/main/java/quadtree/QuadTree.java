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

	private float[] xs;
    private float[] ys;
    private int pointCount = 0;  // Current number of points

	// private List<Point> points = new ArrayList<Point>();

	// public List<Point> getPoints() {
		// return points;
	// }

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

	/**
	 * Create a quad tree with the given boundary and default node capacity
	 */
	public QuadTree(AABB _boundary)
	{
		boundary = _boundary;
		this.xs = new float[QT_NODE_CAPACITY];
        this.ys = new float[QT_NODE_CAPACITY];
	}

	/**
	 * Create a quad tree with the given boundary and node capacity
	 */
	public QuadTree(AABB _boundary, int maxNodeCapacity)
	{
		boundary = _boundary;
		QT_NODE_CAPACITY = maxNodeCapacity;
		this.xs = new float[QT_NODE_CAPACITY];
        this.ys = new float[QT_NODE_CAPACITY];
	}

	public boolean insert(float x, float y)
	{
		if (!boundary.contains(x, y))
        return false;

		// If we're at a leaf node and not full, add the point
		if (northWest == null && pointCount < QT_NODE_CAPACITY) {
			xs[pointCount] = x;
			ys[pointCount] = y;
			pointCount++;
			return true;
		}

		// If we don't have subtrees yet, subdivide
		if (northWest == null) {
			subdivide();
			
			// Move existing points to children
			for (int i = 0; i < pointCount; i++) {
				insertToChildren(xs[i], ys[i]);
			}
			pointCount = 0; // Clear this node's points
			xs = null;
			ys = null;
		}

		// Add a minimum size check to prevent infinite subdivision
		if (boundary.halfWidth < 0.01f || boundary.halfHeight < 0.01f) {
			// Just add to current node if we're too small
			if (xs == null) {
				xs = new float[QT_NODE_CAPACITY];
				ys = new float[QT_NODE_CAPACITY];
			}
			if (pointCount < QT_NODE_CAPACITY) {
				xs[pointCount] = x;
				ys[pointCount] = y;
				pointCount++;
				return true;
			}
			return false;
		}

		// Insert into appropriate child
		return insertToChildren(x, y);
	}

	private boolean insertToChildren(float x, float y) {
		if (northWest.boundary.contains(x, y)) return northWest.insert(x, y);
		if (northEast.boundary.contains(x, y)) return northEast.insert(x, y);
		if (southWest.boundary.contains(x, y)) return southWest.insert(x, y);
		if (southEast.boundary.contains(x, y)) return southEast.insert(x, y);
		return false; // Should never happen if boundary check passed
	}

	public void nullify() {
		if(northWest != null) {
			xs = null;
			ys = null;

			northWest.nullify();
			northEast.nullify();
			southWest.nullify();
			southEast.nullify();
		}
	}

	/**
	 * create four children which fully divide this quad into four quads of equal area
	 */
	public void subdivide()
	{
		float halfx = this.boundary.halfWidth;
		float halfy = this.boundary.halfHeight;
		
		float centerx = this.boundary.centerX;
		float centery = this.boundary.centerY;
		
		 // Remove Point usage, just pass floats directly
		 northWest = new QuadTree(new AABB(centerx-halfx, centery, centerx, centery+halfy));
		 northEast = new QuadTree(new AABB(centerx, centery, centerx+halfx, centery+halfy));
		 southWest = new QuadTree(new AABB(centerx-halfx, centery-halfy, centerx, centery));
		 southEast = new QuadTree(new AABB(centerx, centery-halfy, centerx+halfx, centery+halfy));
	}

	/**
	 * Returns all points within the given range
	 */
	public void queryRange(AABB range, List<Point> results) {

		if (!boundary.intersects(range))
			return;

		queryRangeImpl(range, results);

		return;
	}

	/**
	 * To avoid creating a new list for each internal call, we pass in a list
	 * and add to it.
	 */
	private void queryRangeImpl(AABB range, List<Point> results) {

		if (range.contains(boundary)) {
			getAllPoints(results);
			return;
		}

	    // Check objects at this quad level
		for (int i = 0; i < pointCount; i++) {
			if (range.contains(xs[i], ys[i])) {
				results.add(new Point(xs[i], ys[i]));
			}
		}

	    // Terminate here, if there are no children
	    if (northWest == null)
	      return;

	    // Otherwise, add the points from the children
	    
		if(northWest.boundary.intersects(range))
	    	northWest.queryRangeImpl(range, results);
	    if(northEast.boundary.intersects(range))
	    	northEast.queryRangeImpl(range, results);
	    if(southWest.boundary.intersects(range))
	    	southWest.queryRangeImpl(range, results);
	    if(southEast.boundary.intersects(range))
	    	southEast.queryRangeImpl(range, results);
	    
	    return;	
	}

	// Helper method to get all points in this quad and its children
	public void getAllPoints(List<Point> results) {
		for (int i = 0; i < pointCount; i++) {
			results.add(new Point(xs[i], ys[i]));
		}
		
		if (northWest == null)
			return;
			
		northWest.getAllPoints(results);
		northEast.getAllPoints(results);
		southWest.getAllPoints(results);
		southEast.getAllPoints(results);
	}
}
