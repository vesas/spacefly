package com.vesas.spacefly.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.junit.jupiter.api.Test;

public class VisibilityTest {
    
    /**
     * Test with a square shape
     */
    @Test
    public void test1()
    {
        Visibility visib = new Visibility();

        visib.startLoad();
        visib.startConvexArea();

        // bottom left --> bottom right
        visib.addSegment(0,0,10,0);

        // bottom right --> top right 
        visib.addSegment(10, 0, 10, 10);

        // top right --> top left
        visib.addSegment(10,10, 0, 10);

        // top left --> bottom left
        visib.addSegment(0,10,0,0);

        // area is 100, as sides are 10x10
        float expected_area = 100.0f;
        visib.finishConvexArea();

        visib.finishLoad();

        visib.setLightLocation(5f,5f);
        visib.sweep();
        
        VisibilityPoly poly = visib.getVisibPoly();

        // poly contains the end points of triangles from the light location
        float actual_poly_area = calculateArea(5,5, poly.getTriEndPoints());

        assertEquals(expected_area, actual_poly_area);
    }

    /**
     * Calculate triangle area from it's vertices
     * TODO: This could be done with the perp-dot as well (A = perpDot(a,b)/2)
     */
    private float triArea(float x1, float y1, float x2, float y2, float x3, float y3)
    {
        return Math.abs( 0.5f * ( x1*(y2-y3) + x2*(y3-y1) + x3*(y1-y2) ));
    }

    /**
     * Calculate the area of the whole visibilty poly (which is composed of triangles)
     * @param center_x
     * @param center_y
     * @param triPoints
     * @return
     */
    private float calculateArea(float center_x, float center_y, Array<Vector2> triPoints)
    {
        float sum = 0.0f;
        for(int i = 0; i < triPoints.size; i = i + 2 )
        {
            Vector2 first = triPoints.get(i);
            Vector2 second = triPoints.get(i+1);

            sum += triArea(center_x, center_y, first.x, first.y, second.x, second.y);

        }

        return sum;
    }
}
