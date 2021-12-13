package com.vesas.game.util;

import com.vesas.spacefly.game.Util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {
    
    @Test
    public void test0()
    {
        float diff = Util.angleDiff(0, 0);
        assertEquals(0, diff);

        float diff2 = Util.angleDiff(-10, 10);
        assertEquals(-20, diff2);
    }

    @Test
    public void test1()
    {
        float diff = Util.angleDiff(-190, 0);
        assertEquals(170, diff);
    }

    @Test
    public void test2()
    {
        float diff = Util.angleDiff(370, 20);
        assertEquals(-10, diff);
    }

    @Test
    public void test3()
    {
        float diff = Util.angleDiff(760, 20);
        assertEquals(20, diff);
    }
}
