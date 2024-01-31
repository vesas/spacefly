package util;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;

public class PolyUtilTest {
    
    @Test
    public void test1() {

        Vector2 center = new Vector2(3,1);
        Vector2 p1 = new Vector2(2,0);
        Vector2 p2 = new Vector2(3.999f,2);

        final boolean p2Right = PolyUtils.isClockwise(p1.x-center.x, p1.y-center.y, p2.x-center.x, p2.y-center.y);

        int qwe = 0;
    }
}
