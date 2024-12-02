package com.vesas.spacefly.quadtree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.vesas.spacefly.quadtree.AABB;
import com.vesas.spacefly.quadtree.Point;

public class AABBTest {
    
    @Test
    void testIntersects() {
        AABB box = new AABB(0, 0, 10, 10); // center (5,5), half-dimensions (5,5)

        // Test all four early return false conditions
        // 1. Right edge of other < left edge of this
        assertFalse(box.intersects(new AABB(-15, 5, -11, 8))); // box completely to left

        // 2. Left edge of other > right edge of this
        assertFalse(box.intersects(new AABB(15, 5, 20, 8))); // box completely to right

        // 3. Top edge of other < bottom edge of this
        assertFalse(box.intersects(new AABB(5, -15, 8, -11))); // box completely below

        // 4. Bottom edge of other > top edge of this
        assertFalse(box.intersects(new AABB(5, 15, 8, 20))); // box completely above

        // Test true conditions
        // Overlapping cases
        assertTrue(box.intersects(new AABB(-5, -5, 5, 5))); // overlap bottom-left
        assertTrue(box.intersects(new AABB(5, 5, 15, 15))); // overlap top-right
        assertTrue(box.intersects(new AABB(2, 2, 8, 8))); // completely inside
        assertTrue(box.intersects(new AABB(-5, -5, 15, 15))); // completely contains
        
        // Edge touching cases (should intersect)
        assertTrue(box.intersects(new AABB(-5, 0, 0, 10))); // touching left edge
        assertTrue(box.intersects(new AABB(10, 0, 15, 10))); // touching right edge
        assertTrue(box.intersects(new AABB(0, -5, 10, 0))); // touching bottom edge
        assertTrue(box.intersects(new AABB(0, 10, 10, 15))); // touching top edge
        
        // Corner touching cases (should intersect)
        assertTrue(box.intersects(new AABB(-5, -5, 0, 0))); // touching bottom-left corner
        assertTrue(box.intersects(new AABB(10, 10, 15, 15))); // touching top-right corner
        
        // Same box
        assertTrue(box.intersects(new AABB(0, 0, 10, 10))); // identical box
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
        AABB box = new AABB(0, 0, 10, 10); // center (5,5), half-dimensions (5,5)
        
        // Test points inside
        assertTrue(box.contains(new Point(5, 5))); // center
        assertTrue(box.contains(new Point(0, 0))); // bottom-left corner
        assertTrue(box.contains(new Point(10, 10))); // top-right corner
        assertTrue(box.contains(new Point(0, 10))); // top-left corner
        assertTrue(box.contains(new Point(10, 0))); // bottom-right corner

        // Test points outside
        assertFalse(box.contains(new Point(-0.1f, 5))); // left
        assertFalse(box.contains(new Point(10.1f, 5))); // right
        assertFalse(box.contains(new Point(5, -0.1f))); // bottom
        assertFalse(box.contains(new Point(5, 10.1f))); // top
        assertFalse(box.contains(new Point(-1, -1))); // diagonal outside
    }

    @Test
    void testContainsXY() {
        AABB box = new AABB(0, 0, 10, 10); // center (5,5), half-dimensions (5,5)

        // Test points inside
        assertTrue(box.contains(5, 5)); // center
        assertTrue(box.contains(0, 0)); // bottom-left corner
        assertTrue(box.contains(10, 10)); // top-right corner
        assertTrue(box.contains(0, 10)); // top-left corner
        assertTrue(box.contains(10, 0)); // bottom-right corner

        // Test points on edges
        assertTrue(box.contains(0, 5)); // left edge
        assertTrue(box.contains(10, 5)); // right edge
        assertTrue(box.contains(5, 0)); // bottom edge
        assertTrue(box.contains(5, 10)); // top edge

        // Test points outside
        assertFalse(box.contains(-0.1f, 5)); // left
        assertFalse(box.contains(10.1f, 5)); // right
        assertFalse(box.contains(5, -0.1f)); // bottom
        assertFalse(box.contains(5, 10.1f)); // top
    }

    @Test
    void testEdgeCases() {

        AABB box = new AABB(0, 0, 10, 10); // center (5,5), half-dimensions (5,5)

        // Test zero-size boxes
        AABB zeroBox = new AABB(5, 5, 5, 5); // zero-width/height box
        assertTrue(box.contains(zeroBox));
        assertTrue(zeroBox.contains(5, 5));
        assertFalse(zeroBox.contains(5.1f, 5));

        // Test very small boxes
        AABB tinyBox = new AABB(5, 5, 5.0001f, 5.0001f);
        assertTrue(box.contains(tinyBox));
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
        final String actual = box.toString();
        assertEquals(expected, box.toString(), "actual was: " + actual + " expected is: " + expected);
    }
}
