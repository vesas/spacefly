package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public interface WorldGenInterface {

    public Array<Feature> generate();

    public void setFirstRoomCenter(Vector2 firstRoomCenter);
}
