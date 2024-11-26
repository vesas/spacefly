package com.vesas.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import util.SimplexNoise;

public class SimplexNoiseTest {
    
    @Test
    public void testValuesShouldBeInRange() {
        boolean belowZero = false;
        boolean aboveZero = false;
        for (int i = 0; i < 200; i++) {
            double value = SimplexNoise.noise(i * 0.1f, i * 1.3f);
            if (value < 0) {
                belowZero = true;
            }
            if (value > 0) {
                aboveZero = true;
            }
        }
        assertTrue(belowZero);
        assertTrue(aboveZero);
    }

    @Test
    public void testNoiseRange() {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        
        for (int i = 0; i < 10000; i++) {
            float value = SimplexNoise.noise(i * 0.1f, i * 0.1f);
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        System.out.println("Min: " + min + ", Max: " + max);
    }
}
