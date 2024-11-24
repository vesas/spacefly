package quadtree;

public class AABB
{
	public float centerX;
    public float centerY;
    public float halfWidth;
    public float halfHeight;
	
	
	/**
	 * Creates an axis-aligned bounding box with the given center and half-dimension.
	 * Half dimension is the distance from the center to the edge of the box. Box dimensions are twice the half-dimension.
	 */
	public AABB(final Point center, final Point halfDimension )
	{
		this.centerX = center.x;
        this.centerY = center.y;
        this.halfWidth = halfDimension.x;
        this.halfHeight = halfDimension.y;
	}

	/**
	 * Creates an axis-aligned bounding box with the given bottom-left and top-right points.
	 */
	public AABB(float x1, float y1, float x2, float y2) {
		this.centerX = (x1 + x2) * 0.5f;
        this.centerY = (y1 + y2) * 0.5f;
        this.halfWidth = Math.abs(x2 - x1) * 0.5f;
        this.halfHeight = Math.abs(y2 - y1) * 0.5f;
	}

	public void update(float minX, float minY, float maxX, float maxY) {
		this.centerX = (minX + maxX) * 0.5f;
        this.centerY = (minY + maxY) * 0.5f;
        this.halfWidth = Math.abs(maxX - minX) * 0.5f;
        this.halfHeight = Math.abs(maxY - minY) * 0.5f;
	}
	
	public boolean contains(final Point p) 
	{
		return p.x >= (centerX - halfWidth) &&
               p.x <= (centerX + halfWidth) &&
               p.y >= (centerY - halfHeight) &&
               p.y <= (centerY + halfHeight);
	}

	public boolean contains(float x, float y) {
        return x >= (centerX - halfWidth) &&
               x <= (centerX + halfWidth) &&
               y >= (centerY - halfHeight) &&
               y <= (centerY + halfHeight);
    }
 
	public boolean intersects(final AABB other) 
	{
		if ((other.centerX + other.halfWidth) < (centerX - halfWidth)) return false;
        if ((other.centerX - other.halfWidth) > (centerX + halfWidth)) return false;
        if ((other.centerY + other.halfHeight) < (centerY - halfHeight)) return false;
        if ((other.centerY - other.halfHeight) > (centerY + halfHeight)) return false;
        return true;
	}

	public boolean contains(final AABB other) {
		return (other.centerX + other.halfWidth) <= (centerX + halfWidth) &&
               (other.centerX - other.halfWidth) >= (centerX - halfWidth) &&
               (other.centerY + other.halfHeight) <= (centerY + halfHeight) &&
               (other.centerY - other.halfHeight) >= (centerY - halfHeight);
	}
	
	public String toString()
	{
		return String.format("AABB center: (%.2f, %.2f) half-dimensions: (%.2f, %.2f)", 
            centerX, centerY, halfWidth, halfHeight);
	}
}
