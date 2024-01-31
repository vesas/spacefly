package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.math.Vector2;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.visibility.VisibilityPoly;
import com.vesas.spacefly.world.procedural.room.rectangleroom.RectangleRoomBuilder;

public class TestAreaBuilder {

    public void build(Visibility visib) {

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

        // final Vector2 lightLocation = new Vector2(3,1);
        // visib.setLightLocation(lightLocation.x,lightLocation.y);
        // visib.sweep();
        
        // VisibilityPoly poly = visib.getVisibPoly();

    }
   
}
