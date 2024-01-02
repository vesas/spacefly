package com.vesas.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import util.PolyUtils;

public class PolyUtilsTest {
    
    @Test
    public void test1()
    {
        // unit vector point up
        float up_x = 0.0f;
        float up_y = 1.0f;

        // unit vector pointing to right
        float right_x = 1.0f;
        float right_y = 0.0f;

        boolean cw = PolyUtils.isClockwise(up_x,up_y,right_x,right_y);
        boolean ccw = PolyUtils.isCounterClockwise(up_x,up_y,right_x,right_y);

        assertEquals( true, cw);
        assertEquals( false, ccw);
    }
}
