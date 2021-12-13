package com.vesas.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import util.PolyUtils;

public class PolyUtilsTest {
    
    @Test
    public void test1()
    {
        // unit vector point up
        float x1 = 0.0f;
        float y1 = 1.0f;

        // unit vector pointing to right
        float x2 = 1.0f;
        float y2 = 0.0f;

        boolean cw = PolyUtils.isClockwise(x1,y1,x2,y2);
        boolean ccw = PolyUtils.isCounterClockwise(x1,y1,x2,y2);

        assertEquals( true, cw);
        assertEquals( false, ccw);
    }
}
