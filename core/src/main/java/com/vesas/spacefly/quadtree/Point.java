package com.vesas.spacefly.quadtree;

import java.util.Objects;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Point other = (Point) obj;
		return Float.compare(other.x, x) == 0 
			&& Float.compare(other.y, y) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
