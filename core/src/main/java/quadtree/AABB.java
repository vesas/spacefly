package quadtree;

public class AABB
{
	
	public XY center;
	public XY halfDimension;
	
	public AABB() { }
	
	public AABB( XY center, XY halfDimension )
	{
		this.center = center;
		this.halfDimension = halfDimension;
		
	}
	
	public boolean containsPoint(XY p) 
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
