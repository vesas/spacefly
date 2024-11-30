package util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.math.Vector2;

public class PolyUtilTest {
    
    @Test
    public void testClockwise() {
        Vector2 center = new Vector2(3,1);
        Vector2 p1 = new Vector2(2,0);
        Vector2 p2 = new Vector2(3.999f,2);

        boolean isClockwise = PolyUtils.isClockwise(p1.x-center.x, p1.y-center.y, p2.x-center.x, p2.y-center.y);
        assertTrue(isClockwise, "Points should be clockwise relative to center");
    }

    @Test
    public void testCounterClockwise() {
        Vector2 center = new Vector2(0,0);
        Vector2 p1 = new Vector2(1,0);
        Vector2 p2 = new Vector2(0,1);

        boolean isClockwise = PolyUtils.isClockwise(p1.x-center.x, p1.y-center.y, p2.x-center.x, p2.y-center.y);
        assertFalse(isClockwise, "Points should be counter-clockwise relative to center");
    }

    @Test
    public void testAlmostCollinearPoints() {
        Vector2 center = new Vector2(0,0);
        Vector2 p1 = new Vector2(1,1);
        Vector2 p2 = new Vector2(2,2.0000001f);  // Tiny deviation from collinear

        boolean isClockwise = PolyUtils.isClockwise(p1.x-center.x, p1.y-center.y, p2.x-center.x, p2.y-center.y);
        assertTrue(isClockwise, "Nearly collinear points should still determine orientation");
    }
}
