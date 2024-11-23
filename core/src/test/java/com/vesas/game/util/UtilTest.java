package com.vesas.game.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.vesas.spacefly.game.Util;

import org.junit.jupiter.api.Test;

public class UtilTest {
    
    @Test
    public void angleDiffShouldBeZeroForSameAngles() {
        float diff = Util.angleDiff(0, 0);
        assertEquals(0, diff);
    }

    @Test
    public void angleDiffShouldHandleSmallNegativeToPositive()
    {
        float diff2 = Util.angleDiff(-10, 10);
        assertEquals(-20, diff2);
    }

    @Test
    public void angleDiffShouldHandleLargeNegativeToZero()
    {
        float diff = Util.angleDiff(-190, 0);
        assertEquals(170, diff);
    }

    @Test
    public void angleDiffShouldHandleAnglesOver360()
    {
        float diff = Util.angleDiff(370, 20);
        assertEquals(-10, diff);
    }

    @Test
    public void angleDiffShouldHandleAnglesOver720()
    {
        float diff = Util.angleDiff(760, 20);
        assertEquals(20, diff);
    }

}
