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
		// Ignore objects which do not belong in this quad tree
		if (!boundary.contains(x, y))
	    	return false; // object cannot be added

		if (northWest == null) {
			// If all the points are the same, we can just add the point to the list, no need to subdivide
			boolean allSame = true;
            for (int i = 0; i < pointCount; i++) {
                if (xs[i] != x || ys[i] != y) {
                    allSame = false;
                    break;
                }
            }
			
			if (allSame || pointCount < QT_NODE_CAPACITY) {

				if(pointCount >= xs.length) { 

					float[] newXs = new float[xs.length * 2];
					float[] newYs = new float[ys.length * 2];
		
					System.arraycopy(xs, 0, newXs, 0, xs.length);
					System.arraycopy(ys, 0, newYs, 0, ys.length);
		
					this.xs = newXs;
					this.ys = newYs;
				}
				
                xs[pointCount] = x;
                ys[pointCount] = y;
                pointCount++;
                return true;
            }
		}

		// Add point before subdividing

		if(pointCount >= xs.length) { 

			float[] newXs = new float[xs.length * 2];
			float[] newYs = new float[ys.length * 2];

			System.arraycopy(xs, 0, newXs, 0, xs.length);
			System.arraycopy(ys, 0, newYs, 0, ys.length);

			this.xs = newXs;
			this.ys = newYs;
		}

        xs[pointCount] = x;
        ys[pointCount] = y;
        pointCount++;

		// Otherwise, we need to subdivide then add the point to whichever node will accept it

		if (northWest == null) {
            subdivide();
		}
		// Redistribute points to children
		for (int i = 0; i < pointCount; i++) {
			float px = xs[i];
			float py = ys[i];
			if (northWest.insert(px, py)) continue;
			if (northEast.insert(px, py)) continue;
			if (southWest.insert(px, py)) continue;
			if (southEast.insert(px, py)) continue;
		}
		pointCount = 0; // Clear points from this node
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
	public List<Point> queryRange(AABB range) {
		List<Point> results = new ArrayList<Point>();

		queryRange(range, results);

		return results;
	}

	/**
	 * To avoid creating a new list for each internal call, we pass in a list
	 * and add to it.
	 */
	private void queryRange(AABB range, List<Point> results) {

		// Automatically abort if the range does not collide with this quad
	    if (!boundary.intersects(range))
	      return; // empty list

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
	    
	    northWest.queryRange(range, results);
	    northEast.queryRange(range, results);
	    southWest.queryRange(range, results);
	    southEast.queryRange(range, results);
	    
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
