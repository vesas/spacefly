package quadtree;

public class Point
{
	public float x;
	public float y;
	
	public Point( float x, float y )
	{
		this.x = x;
		this.y = y;
	}
	
	public String toString()
	{
		return "XY X: " + x + " Y: " + y;
	}

	public boolean equals(Point other) {
		return this.x == other.x && this.y == other.y;
	}
}
