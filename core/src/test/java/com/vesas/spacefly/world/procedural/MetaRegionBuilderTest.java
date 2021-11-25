package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.math.Vector2;
import com.vesas.spacefly.world.procedural.generator.MetaRegionBuilder;
import com.vesas.spacefly.world.procedural.generator.Region;

import org.junit.Test;

public class MetaRegionBuilderTest {

    @Test
    public void test1()
    {
        MetaRegionBuilder gen = new MetaRegionBuilder();
        gen.setFirstRoomCenter(new Vector2(0,0));

        Region region = gen.generateMetaRegion();
        
        System.out.println("" + region.toString());
    }
    
}
