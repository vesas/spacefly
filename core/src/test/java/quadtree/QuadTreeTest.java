package quadtree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import util.Timing;


public class QuadTreeTest {
    

    @Test
    public void testInsertAndQuery() {
        // Create a boundary box centered at (0,0) with dimensions 10x10
        AABB boundary = new AABB(new Point(0, 0), new Point(10, 10));
        QuadTree tree = new QuadTree(boundary);

        // Insert some test points
        Point p1 = new Point(2, 3);
        Point p2 = new Point(-4, 2);
        Point p3 = new Point(6, -5);

        tree.insert(p1.x, p1.y);
        tree.insert(p2.x, p2.y);
        tree.insert(p3.x, p3.y);

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
            tree.insert(p.x, p.y);
        }

        // Query a small region that should contain exactly 2 points
        AABB queryRegion = new AABB(1.9f, 0.9f,2.1f, 2.1f);
        List<Point> found = tree.queryRange(queryRegion);
        
        assertEquals(2, found.size());
        assertTrue(found.contains(points[2])); // Should find point (2,1) and (2, 2)
        
        // Query larger region that should contain all points
        AABB allRegion = new AABB(new Point(0, 0), new Point(10, 10));
        List<Point> allFound = tree.queryRange(allRegion);
        
        assertEquals(points.length, allFound.size());
        for (Point p : points) {
            assertTrue(allFound.contains(p));
        }
    }

    @Test
    public void testSubdivision() {

        float center_x = 15f;
        float center_y = 15f;

        QuadTree tree = new QuadTree(new AABB(new Point(center_x,center_y), new Point(5,5)), 2);

        float xx = 15f;
        float yy = 15f;
        tree.insert(xx+1,yy+1);
        tree.insert(xx+1,yy+1);
        tree.insert(xx+1,yy+1);
        tree.insert(xx+1,yy+1);
        tree.insert(xx+1,yy+1);
        tree.insert(xx+1.001f,yy+1.001f);

        int qew = 0;
    }

    @Test
    public void testPerformance() {

        // Create a quad tree with a large number of points
        // Test the performance of inserting a point
        // Test the performance of querying a point

        int numPoints = 1000000;
        int numQueries = 100000;

        QuadTree tree = new QuadTree(new AABB(new Point(0,0), new Point(100,100)), 4);

        Timing t1 = Timing.startNew();
        
        // Insert points
        for (int i = 0; i < numPoints; i++) {
            tree.insert((float)(Math.random() * 100.0), (float)(Math.random() * 100.0));
        }

        t1.stop();

        AABB queryRegion = new AABB(new Point(0,0), new Point(100,100));

        // Query points
        Timing t2 = Timing.startNew();
        for (int i = 0; i < numQueries; i++) {
            queryRegion.center.x = (float)(Math.random() * 100.0);
            queryRegion.center.y = (float)(Math.random() * 100.0);
            queryRegion.halfDimension.x = 0.1f;
            queryRegion.halfDimension.y = 0.1f;

            tree.queryRange(queryRegion);
        }

        t2.stop();

        System.out.println("Time taken to insert " + numPoints + " points: " + t1.getElapsedMillis() + "ms");
        System.out.println("Time taken to query " + numQueries + " points: " + t2.getElapsedMillis() + "ms");

        // Query should be faster than insert
        assertTrue(t2.getElapsedMillis() < t1.getElapsedMillis());

        // Querying 100000 times should be less than 200ms
        assertTrue(t2.getElapsedMillis() < 200);
    }

}
