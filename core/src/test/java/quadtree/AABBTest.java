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
        assertEquals(5.0, box.center.x, 0.001);
        assertEquals(5.0, box.center.y, 0.001);
    }

    @Test
    public void testDimensions() {
        AABB box = new AABB(0, 0, 10, 20);

        assertEquals(5.0, box.halfDimension.x, 0.001);
        assertEquals(10.0, box.halfDimension.y, 0.001);
        
    }
}
