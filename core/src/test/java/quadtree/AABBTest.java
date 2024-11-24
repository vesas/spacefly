package quadtree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AABBTest {
    
    @Test
    public void testIntersects() {
        AABB a = new AABB(0, 0, 10, 10);
        AABB b = new AABB(5, 5, 15, 15);
        AABB c = new AABB(20, 20, 30, 30);
        
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));

        assertFalse(a.intersects(c));
        assertFalse(c.intersects(a));
    }

    @Test
    public void testContains() {
        AABB box = new AABB(0, 0, 10, 10);
        
        assertTrue(box.contains(5, 5));
        assertTrue(box.contains(0, 0));
        assertTrue(box.contains(10, 10));
        assertFalse(box.contains(11, 11));
        assertFalse(box.contains(-1, 5));
    }

    @Test
    void testContainsAABB3() {
        AABB container = new AABB(0, 0, 10, 10); // 5,5 center, 5,5 half-dimensions

        // Test fully contained box
        AABB contained = new AABB(2, 2, 8, 8);
        assertTrue(container.contains(contained));

        // Test box extending beyond right edge
        AABB extendRight = new AABB(2, 2, 11, 8);
        assertFalse(container.contains(extendRight));

        // Test box extending beyond left edge
        AABB extendLeft = new AABB(-1, 2, 8, 8);
        assertFalse(container.contains(extendLeft));

        // Test box extending beyond top edge
        AABB extendTop = new AABB(2, 2, 8, 11);
        assertFalse(container.contains(extendTop));

        // Test box extending beyond bottom edge
        AABB extendBottom = new AABB(2, -1, 8, 8);
        assertFalse(container.contains(extendBottom));

        // Test exact same box
        AABB same = new AABB(0, 0, 10, 10);
        assertTrue(container.contains(same));
    }

    @Test
    public void testContainsAABB() {
        AABB large = new AABB(0, 0, 20, 20);
        AABB small = new AABB(5, 5, 15, 15);
        AABB outside = new AABB(25, 25, 30, 30);
        
        assertTrue(large.contains(small));
        assertFalse(small.contains(large));
        assertFalse(large.contains(outside));
    }

    @Test
    public void testCenter() {
        AABB box = new AABB(0, 0, 10, 10);
        assertEquals(5.0, box.centerX, 0.001);
        assertEquals(5.0, box.centerY, 0.001);
    }

    @Test
    public void testDimensions() {
        AABB box = new AABB(0, 0, 10, 20);

        assertEquals(5.0, box.halfWidth, 0.001);
        assertEquals(10.0, box.halfHeight, 0.001);
        
    }

    @Test
    public void testNegativeCoordinates() {
        AABB box = new AABB(-10, -10, -5, -5);
        
        assertTrue(box.contains(-7, -7));
        assertFalse(box.contains(-12, -7));
        assertEquals(-7.5, box.centerX, 0.001);
        assertEquals(-7.5, box.centerY, 0.001);
    }

    @Test
    public void testZeroSize() {
        AABB point = new AABB(5, 5, 5, 5);
        AABB box = new AABB(0, 0, 10, 10);

        assertTrue(box.contains(point));
        assertTrue(point.intersects(point));
        assertEquals(0.0, point.halfWidth, 0.001);
        assertEquals(0.0, point.halfHeight, 0.001);
    }

    @Test
    public void testEdgeIntersections() {
        AABB center = new AABB(10, 10, 20, 20);
        
        // Touching exactly at edges
        AABB left = new AABB(0, 10, 10, 20);
        AABB top = new AABB(10, 0, 20, 10);
        
        // Touching at corner
        AABB cornerTouch = new AABB(20, 20, 30, 30);
        
        // Should all intersect since we consider edges/corners as intersecting
        assertTrue(center.intersects(left));
        assertTrue(center.intersects(top));
        assertTrue(center.intersects(cornerTouch));
        
        // Verify symmetry
        assertTrue(left.intersects(center));
        assertTrue(top.intersects(center));
        assertTrue(cornerTouch.intersects(center));
    }

    @Test
    public void testConstructorWithPoints() {
        Point center = new Point(5, 5);
        Point halfDim = new Point(2, 3);
        AABB box = new AABB(center, halfDim);
        
        assertEquals(5f, box.centerX, 0.001f);
        assertEquals(5f, box.centerY, 0.001f);
        assertEquals(2f, box.halfWidth, 0.001f);
        assertEquals(3f, box.halfHeight, 0.001f);
    }
    
    @Test
    public void testConstructorWithCoordinates() {
        AABB box = new AABB(1, 1, 5, 7);
        
        assertEquals(3f, box.centerX, 0.001f);
        assertEquals(4f, box.centerY, 0.001f);
        assertEquals(2f, box.halfWidth, 0.001f);
        assertEquals(3f, box.halfHeight, 0.001f);
    }
    
    @Test
    public void testUpdate() {
        AABB box = new AABB(0, 0, 2, 2);
        box.update(2, 2, 6, 8);
        
        assertEquals(4f, box.centerX, 0.001f);
        assertEquals(5f, box.centerY, 0.001f);
        assertEquals(2f, box.halfWidth, 0.001f);
        assertEquals(3f, box.halfHeight, 0.001f);
    }
    
    @Test
    public void testContainsPoint() {
        AABB box = new AABB(0, 0, 4, 4);
        
        assertTrue(box.contains(new Point(2, 2)));  // Center
        assertTrue(box.contains(new Point(0, 0)));  // Corner
        assertTrue(box.contains(new Point(4, 4)));  // Corner
        assertFalse(box.contains(new Point(5, 5))); // Outside
    }
    
    @Test
    public void testContainsCoordinates() {
        AABB box = new AABB(0, 0, 4, 4);
        
        assertTrue(box.contains(2f, 2f));   // Center
        assertTrue(box.contains(0f, 0f));   // Corner
        assertTrue(box.contains(4f, 4f));   // Corner
        assertFalse(box.contains(5f, 5f));  // Outside
    }
    
    @Test
    public void testIntersects2() {
        AABB box1 = new AABB(0, 0, 4, 4);
        
        // Overlapping
        assertTrue(box1.intersects(new AABB(2, 2, 6, 6)));
        // Touching
        assertTrue(box1.intersects(new AABB(4, 4, 8, 8)));
        // Not intersecting
        assertFalse(box1.intersects(new AABB(6, 6, 8, 8)));
    }
    
    @Test
    public void testContainsAABB2() {
        AABB box1 = new AABB(0, 0, 4, 4);
        
        // Fully contained
        assertTrue(box1.contains(new AABB(1, 1, 3, 3)));
        // Same box
        assertTrue(box1.contains(new AABB(0, 0, 4, 4)));
        // Partially outside
        assertFalse(box1.contains(new AABB(2, 2, 6, 6)));
        // Completely outside
        assertFalse(box1.contains(new AABB(6, 6, 8, 8)));
    }
    
    @Test
    public void testToString() {
        AABB box = new AABB(1, 2, 5, 8);
        String expected = "AABB center: (3,00, 5,00) half-dimensions: (2,00, 3,00)";
        assertEquals(expected, box.toString());
    }
}
