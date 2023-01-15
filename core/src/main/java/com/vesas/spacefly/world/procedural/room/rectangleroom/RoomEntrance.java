package com.vesas.spacefly.world.procedural.room.rectangleroom;

import com.badlogic.gdx.math.Rectangle;

public class RoomEntrance {
 
    public Rectangle rect = new Rectangle();

    public RoomEntrance(float x, float y, float width, float height) {
        rect.x = x;
        rect.y = y;
        rect.width = width;
        rect.height = height;
    }
}
