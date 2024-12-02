package com.vesas.spacefly.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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

        assertTrue(min >= -1.0f, "Minimum value " + min + " should be >= -1.0");
        assertTrue(max <= 1.0f, "Maximum value " + max + " should be <= 1.0");
    }

}
