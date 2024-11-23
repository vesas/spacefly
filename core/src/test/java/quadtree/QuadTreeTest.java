package quadtree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;


public class QuadTreeTest {
    
    @Test
	public void test() 
    {
        Point center = new Point(0.0f,5.0f);
        Point halfDimension = new Point(1.0f,1.0f);
        AABB aabb = new AABB();
        QuadTree tree = new QuadTree(aabb);

    }

    @Test
    public void testInsertAndQuery() {
        // Create a boundary box centered at (0,0) with dimensions 10x10
        AABB boundary = new AABB(new Point(0, 0), new Point(10, 10));
        QuadTree tree = new QuadTree(boundary);

        // Insert some test points
        Point p1 = new Point(2, 3);
        Point p2 = new Point(-4, 2);
        Point p3 = new Point(6, -5);

        tree.insert(p1);
        tree.insert(p2);
        tree.insert(p3);

        // Query a region that should contain p1
        AABB queryRegion = new AABB(new Point(0, 0), new Point(5, 5));
        List<Point> found = tree.queryRange(queryRegion);

        assertNotNull(found);
        assertTrue(found.contains(p1));
        assertFalse(found.contains(p3)); // Should be outside the query region
    }

    @Test
    public void testCapacityAndSubdivision() {
        AABB boundary = new AABB(new Point(0, 0), new Point(10, 10));
        QuadTree tree = new QuadTree(boundary);
        
        // Insert more points than the default capacity
        Point[] points = {
            new Point(1, 1),
            new Point(1, 2),
            new Point(2, 1),
            new Point(2, 2),
            new Point(1.5f, 1.5f)
        };
        
        for (Point p : points) {
            tree.insert(p);
        }

        // Query a small region that should contain exactly 2 points
        AABB queryRegion = new AABB(new Point(0.9f, 0.9f), new Point(1.1f, 1.1f));
        List<Point> found = tree.queryRange(queryRegion);
        
        assertEquals(2, found.size());
        assertTrue(found.contains(points[0])); // Should find point (1,1) and (1.5f, 1.5f)
        
        // Query larger region that should contain all points
        AABB allRegion = new AABB(new Point(0, 0), new Point(10, 10));
        List<Point> allFound = tree.queryRange(allRegion);
        
        assertEquals(points.length, allFound.size());
        for (Point p : points) {
            assertTrue(allFound.contains(p));
        }
    }

}
