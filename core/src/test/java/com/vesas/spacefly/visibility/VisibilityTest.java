package com.vesas.spacefly.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class VisibilityTest {
    

    /**
     * Test with a square shape
     */
    @Test
    public void test10By10HasAreaOf100()
    {
        Visibility visib = new Visibility();

        visib.startLoad();
        visib.startConvexArea();

        // square with sides of 10 units
        // bottom left: 0,0
        // bottom right: 10,0
        // top right: 10,10
        // top left: 0,10

        // bottom left --> bottom right
        visib.addSegment(0,0,10,0);

        // bottom right --> top right 
        visib.addSegment(10, 0, 10, 10);

        // top right --> top left
        visib.addSegment(10,10, 0, 10);

        // top left --> bottom left
        visib.addSegment(0,10,0,0);

        
        visib.finishConvexArea();

        visib.finishLoad();

        visib.setLightLocation(5f,5f);
        visib.sweep();
        
        VisibilityPoly poly = visib.getVisibPoly();

        // poly contains the end points of triangles from the light location
        float actualPolyArea = calculateArea(5,5, poly.getTriEndPoints());

        // area is 100, as sides are 10x10
        float expectedArea = 100.0f;
        assertEquals(expectedArea, actualPolyArea);
    }

    @Test
    public void test10By20()
    {
        Visibility visib = new Visibility();

        visib.startLoad();
        visib.startConvexArea();

        // bottom right --> bottom left
        visib.addSegment(0,0,-10,0);
        visib.addSegment(-10, 0, -10, 20);

        visib.addSegment(-10, 20, 0, 20);
        visib.addSegment(0, 20, 0, 0);
        
        visib.finishConvexArea();
        visib.finishLoad();

        visib.setLightLocation(-1f,2f);
        visib.sweep();
        
        VisibilityPoly poly = visib.getVisibPoly();

        // poly contains the end points of triangles from the light location
        float actualPolyArea = calculateArea(-5,5, poly.getTriEndPoints());

        // area is 200, as sides are 10x20
        float expectedArea = 200.0f;
        assertEquals(expectedArea, actualPolyArea);
        
    }

    @Test
    public void testLShape()
    {
        Visibility visib = new Visibility();

        visib.startLoad();
        
        
        // 
        // Like letter L
        // total area 175 units
        // 
        // lower part
        // 

        visib.startConvexArea();
        // top left--> top right
        visib.addSegment(2,2,4,2);

        // top right --> bottom right
        visib.addSegment(4,2, 4, 0);

        // bottom right --> bottom left
        visib.addSegment(4, 0, 2, 0);
        
        visib.finishConvexArea();

        // 
        // upper part
        visib.startConvexArea();

        // bottom 
        visib.addSegment(2, 0, 0, 0);

        // left side up
        visib.addSegment(0, 0, 0, 6);

        // top side
        visib.addSegment(0, 6, 2,6);

        // right side
        visib.addSegment(2, 6, 2, 2);
        
        visib.finishConvexArea();
        visib.finishLoad();

        final Vector2 lightLocation = new Vector2(3,1);
        visib.setLightLocation(lightLocation.x,lightLocation.y);
        visib.sweep();
        
        VisibilityPoly poly = visib.getVisibPoly();

        System.out.println(poly.toString());

        // poly contains the end points of triangles from the light location
        float actualPolyArea = calculateArea(lightLocation.x,lightLocation.y, poly.getTriEndPoints());

        // visible area from light location 3,1 is 10
        float expectedArea = 10.0f;
        assertEquals(expectedArea, actualPolyArea);
        
    }

    @Test
    void testTri() {
        
        Visibility visib = new Visibility();

        visib.startLoad();
        visib.startConvexArea();

        // square with sides of 10 units
        // bottom left: 0,0
        // bottom right: 10,0
        // top right: 10,10
        // top left: 0,10

        // bottom left --> bottom right
        visib.addSegment(0,0,10,0);

        // bottom right --> apex
        visib.addSegment(10, 0, 5, 10);

        // apex --> bottom left
        visib.addSegment(5,10, 0, 0);

        visib.finishConvexArea();
        visib.finishLoad();

        visib.setLightLocation(5f,5f);
        visib.sweep();
        
        VisibilityPoly poly = visib.getVisibPoly();

        Approvals.verify(poly.toString());
    }

    @Test
    void testCenterNotWithinArea() {
        
        Visibility visib = new Visibility();

        visib.startLoad();
        visib.startConvexArea();

        // square with sides of 10 units
        // bottom left: 0,0
        // bottom right: 10,0
        // top right: 10,10
        // top left: 0,10

        // bottom left --> bottom right
        visib.addSegment(0,0,10,0);

        // bottom right --> apex
        visib.addSegment(10, 0, 5, 10);

        // apex --> bottom left
        visib.addSegment(5,10, 0, 0);

        visib.finishConvexArea();
        visib.finishLoad();

        visib.setLightLocation(-55f,-5f);
        visib.sweep();
        
        VisibilityPoly poly = visib.getVisibPoly();

        Approvals.verify(poly.toString());
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
     * @param centerX
     * @param centerY
     * @param triPoints
     * @return
     */
    private float calculateArea(float centerX, float centerY, Array<Vector2> triPoints)
    {
        float sum = 0.0f;
        
        for(int i = 0; i < triPoints.size; i = i + 2 )
        {
            Vector2 first = triPoints.get(i);
            Vector2 second = triPoints.get(i+1);

            sum += triArea(centerX, centerY, first.x, first.y, second.x, second.y);

        }

        return sum;
    }

}
