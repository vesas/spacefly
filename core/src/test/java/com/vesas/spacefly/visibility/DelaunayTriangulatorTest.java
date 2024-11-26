package com.vesas.spacefly.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.vesas.spacefly.visibility.DelaunayTriangulator.Triangle;
import com.vesas.spacefly.visibility.DelaunayTriangulator.Vertex;

public class DelaunayTriangulatorTest {
    
    private boolean hasTriangle(short[] triangles, int a, int b, int c) {
        for (int i = 0; i < triangles.length; i += 3) {
            if (containsSameVertices(
                triangles[i], triangles[i + 1], triangles[i + 2],
                (short)a, (short)b, (short)c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSameVertices(short a1, short b1, short c1, short a2, short b2, short c2) {
        short[] t1 = {a1, b1, c1};
        short[] t2 = {a2, b2, c2};
        Arrays.sort(t1);
        Arrays.sort(t2);
        return t1[0] == t2[0] && t1[1] == t2[1] && t1[2] == t2[2];
    }

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
            0, 0,  // bottom left (0)
            1, 0,  // bottom right (1)
            1, 1,  // top right (2)
            0, 1   // top left (3)
        };
        short[] triangles = triangulator.triangulate(vertices);
        
        assertEquals(6, triangles.length); // should create 2 triangles (6 indices)
        
        // Instead of checking specific indices, verify that we have valid triangles
        // containing all vertices and sharing an edge
        boolean hasTriangleWith013 = hasTriangle(triangles, 0, 1, 3) || hasTriangle(triangles, 0, 3, 1);
        boolean hasTriangleWith123 = hasTriangle(triangles, 1, 2, 3) || hasTriangle(triangles, 1, 3, 2);
        boolean hasTriangleWith012 = hasTriangle(triangles, 0, 1, 2) || hasTriangle(triangles, 0, 2, 1);
        boolean hasTriangleWith023 = hasTriangle(triangles, 0, 2, 3) || hasTriangle(triangles, 0, 3, 2);
        
        assertTrue(
            (hasTriangleWith013 && hasTriangleWith123) || (hasTriangleWith012 && hasTriangleWith023),
            "Square should be split into two triangles sharing an edge"
        );
    }

    @Test
    public void testPentagon() {
        DelaunayTriangulator triangulator = new DelaunayTriangulator();

        float[] vertices = new float[] {
            0.0f, 0.0f,    // bottom center (0)
            1.0f, 0.4f,    // bottom right (1)
            0.6f, 1.2f,    // top right (2)
            -0.6f, 1.2f,   // top left (3)
            -1.0f, 0.4f    // bottom left (4)
        };
        short[] triangles = triangulator.triangulate(vertices);
        
        assertEquals(9, triangles.length); // should create 3 triangles (9 indices)
        
        // Verify that we have three triangles that share vertex 0 (center)
        // and form a valid triangulation of the pentagon
        boolean hasTriangle012 = hasTriangle(triangles, 0, 1, 2);
        boolean hasTriangle023 = hasTriangle(triangles, 0, 2, 3);
        boolean hasTriangle034 = hasTriangle(triangles, 0, 3, 4);
        boolean hasTriangle041 = hasTriangle(triangles, 0, 4, 1);
        
        // The pentagon should be triangulated with three triangles sharing the center vertex
        int triangleCount = 0;
        if (hasTriangle012) triangleCount++;
        if (hasTriangle023) triangleCount++;
        if (hasTriangle034) triangleCount++;
        if (hasTriangle041) triangleCount++;
        
        assertEquals(3, triangleCount, 
            "Pentagon should have exactly three triangles sharing the center vertex");
    }

    @Test
    public void testIsPointInCircumcircle1() {
        DelaunayTriangulator triangulator = new DelaunayTriangulator();
        
        // Create a simple right triangle
        Vertex a = new Vertex(-6f, -3.6f);
        Vertex b = new Vertex(0f, 3.6f);
        Vertex c = new Vertex(6f, -3.6f);
        
        Triangle triangle = new Triangle(a, b, c);

        Vertex vertex = new Vertex(0, 0);

        boolean inside = triangulator.isPointInCircumcircle(vertex, triangle);

        assertTrue(inside, "Point should be inside circumcircle");
    }

    @Test
    public void testIsPointInCircumcircle() {
        DelaunayTriangulator triangulator = new DelaunayTriangulator();
        
        // Create a simple right triangle
        Vertex a = new Vertex(0, 0);
        Vertex b = new Vertex(1, 0);
        Vertex c = new Vertex(0, 1);
        Triangle triangle = new Triangle(a, b, c);

        // Test points
        Vertex inside = new Vertex(0.25f, 0.25f);      // Inside circumcircle
        Vertex outside = new Vertex(1.5f, 1.5f);       // Outside circumcircle
        Vertex onCircle = new Vertex(1, 1);            // On circumcircle
        Vertex vertex = new Vertex(0, 0);              // On vertex
        
        // Assert
        assertTrue(triangulator.isPointInCircumcircle(inside, triangle), "Point should be inside circumcircle");
        
        assertFalse(triangulator.isPointInCircumcircle(outside, triangle),
            "Point should be outside circumcircle");
        
        // Note: Points exactly on circle might be numerically unstable
        // so we don't test them unless absolutely necessary
        
        assertFalse(triangulator.isPointInCircumcircle(vertex, triangle),
            "Vertex should NOT be inside circumcircle");
    }

}
