package quadtree;



import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class QuadTreeTest {
    
    @Test
	public void test() 
    {
        System.out.println("FOO1");
        XY center = new XY(0.0f,5.0f);
        System.out.println("FOO2");
        XY halfDimension = new XY(1.0f,1.0f);
        AABB aabb = new AABB();
        QuadTree tree = new QuadTree(aabb);


    }

}
