package com.vesas.spacefly.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TriangulatorTest {
    

    @Test
    public void testEmptyInputReturnsEmptyTriangleList() {
        DelaunayTriangulator triangulator = new DelaunayTriangulator();
        short[] triangles = triangulator.triangulate(new float[0]);
        assertEquals(0, triangles.length);
    }

    @Test
    public void testOneTriangle() {
        DelaunayTriangulator triangulator = new DelaunayTriangulator();

        float[] vertices = new float[] {
            0, 0,
            1, 0,
            0, 1
        };
        short[] triangles = triangulator.triangulate(vertices);
        
        assertEquals(3, triangles.length);

        assertEquals(0, triangles[0]);
        assertEquals(1, triangles[1]);
        assertEquals(2, triangles[2]);

    }

    @Test
    public void testSquare() {
        DelaunayTriangulator triangulator = new DelaunayTriangulator();

        float[] vertices = new float[] {
            0, 0,  // bottom left
            1, 0,  // bottom right
            1, 1,  // top right
            0, 1   // top left
        };
        short[] triangles = triangulator.triangulate(vertices);
        
        assertEquals(6, triangles.length); // should create 2 triangles (6 indices)
        
        // First triangle
        assertEquals(0, triangles[0]);
        assertEquals(1, triangles[1]);
        assertEquals(2, triangles[2]);
        
        // Second triangle
        assertEquals(0, triangles[3]);
        assertEquals(2, triangles[4]);
        assertEquals(3, triangles[5]);
    }

    /*
    @Test
    public void testSlope() {

        Vector2 v1 = new Vector2(0, 0);
        Vector2 v2 = new Vector2(0, 0);

        for(float x = -1.5f; x <= 1.5f; x += 0.25f) {
            for(float y = -1.5f; y <= 1.5f; y += 0.25f) {
            
                float x2 = 0;
                float y2 = 0;

                x2 = y;
                y2 = -x;
                
                
                v1.set(x, y);
                v2.set(x2, y2);

                float dot = v1.dot(v2);

                System.out.println("dot: " + dot + "  x: " + x + " y: " + y + " x2: " + x2 + " y2: " + y2);
            }
        }
        

    }
         */
}
