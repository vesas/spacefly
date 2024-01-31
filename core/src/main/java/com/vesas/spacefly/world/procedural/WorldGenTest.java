package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.AddMonsterCallback;

public class WorldGenTest implements WorldGenInterface {

    Visibility visib;

    public WorldGenTest( AddMonsterCallback world, Visibility visib ) {
        this.visib = visib;
    }

    @Override
    public Array<Feature> generate() {
        
        TestAreaBuilder areaBuilder = new TestAreaBuilder();
        areaBuilder.build(visib);
        
        Array<Feature> feats = new Array<Feature>();

        TestFeature testFeature = new TestFeature();
        feats.add(testFeature);
        
        return feats;
    }

    @Override
    public void setFirstRoomCenter(Vector2 firstRoomCenter) {
        
    }
    
}
