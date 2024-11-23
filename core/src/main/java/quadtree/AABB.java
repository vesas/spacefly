package quadtree;

public class AABB
{
	
	public Point center;
	public Point halfDimension;
	
	public AABB() { }
	
	/**
	 * Creates an axis-aligned bounding box with the given center and half-dimension.
	 * Half dimension is the distance from the center to the edge of the box. Box dimensions are twice the half-dimension.
	 */
	public AABB( Point center, Point halfDimension )
	{
		this.center = center;
		this.halfDimension = halfDimension;
		
	}
	
	public boolean containsPoint(Point p) 
	{
		return p.x > center.x - halfDimension.x &&
				p.x < center.x + halfDimension.x &&
				p.y > center.y - halfDimension.y &&
				p.y < center.y + halfDimension.y;
	}
 
	public boolean intersectsAABB(AABB other) 
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
	
	public String toString()
	{
		return "AABB center: " + center.toString() + " halfDimension: " + halfDimension.toString();
	}
}
