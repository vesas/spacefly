package quadtree;

public class AABB
{
	
	public final Point center;
	public final Point halfDimension;
	
	
	/**
	 * Creates an axis-aligned bounding box with the given center and half-dimension.
	 * Half dimension is the distance from the center to the edge of the box. Box dimensions are twice the half-dimension.
	 */
	public AABB( Point center, Point halfDimension )
	{
		this.center = center;
		this.halfDimension = halfDimension;
		
	}

	/**
	 * Creates an axis-aligned bounding box with the given bottom-left and top-right points.
	 */
	public AABB(float x1, float y1, float x2, float y2) {
		this.center = new Point((x1 + x2) * 0.5f, (y1 + y2) * 0.5f);
		this.halfDimension = new Point(Math.abs(x2 - x1) * 0.5f, Math.abs(y2 - y1) * 0.5f);
	}
	
	public boolean contains(Point p) 
	{
		return p.x >= (center.x - halfDimension.x) &&
				p.x <= (center.x + halfDimension.x) &&
				p.y >= (center.y - halfDimension.y) &&
				p.y <= (center.y + halfDimension.y);
	}

	public boolean contains(float x, float y) {
        return x >= (center.x - halfDimension.x) &&
               x <= (center.x + halfDimension.x) &&
               y >= (center.y - halfDimension.y) &&
               y <= (center.y + halfDimension.y);
    }
 
	public boolean intersects(AABB other) 
	{
		if( other.center.x + other.halfDimension.x < center.x - halfDimension.x )
			return false;
		
		if( other.center.x - other.halfDimension.x > center.x + halfDimension.x )
			return false;
		
		if( other.center.y + other.halfDimension.y < center.y - halfDimension.y )
			return false;
		
		if( other.center.y - other.halfDimension.y > center.y + halfDimension.y )
			return false;
		
		
		return true;
	}

	public boolean contains(AABB other) {
		return (other.center.x + other.halfDimension.x) <= (center.x + halfDimension.x) &&
				(other.center.x - other.halfDimension.x) >= (center.x - halfDimension.x) &&
				(other.center.y + other.halfDimension.y) <= (center.y + halfDimension.y) &&
				(other.center.y - other.halfDimension.y) >= (center.y - halfDimension.y);
	}
	
	public String toString()
	{
		return "AABB center: " + center.toString() + " halfDimension: " + halfDimension.toString();
	}
}
