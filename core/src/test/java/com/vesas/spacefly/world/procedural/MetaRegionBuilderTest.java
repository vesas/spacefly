package com.vesas.spacefly.world.procedural;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.procedural.generator.MetaFeature;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;
import com.vesas.spacefly.world.procedural.generator.MetaRegionBuilder;
import com.vesas.spacefly.world.procedural.generator.Region;

public class MetaRegionBuilderTest {

    @Test
    public void firstItemIsRectangleRoom()
    {
        MetaRegionBuilder gen = new MetaRegionBuilder();
        gen.setFirstRoomCenter(new Vector2(0,0));
        gen.setSize(1);

        Region region = gen.generateMetaRegion();
        
        // region should be size 1
        assertEquals(1, region.getSize());

        Array<MetaFeature> metaList = region.getMetaList();

        MetaFeature firstItem = metaList.get(0);

        // first item should be a normal rectangle room
        assertEquals(firstItem.getClass(), MetaRectangleRoom.class);
    }

    @Test
    public void regionSizeMatchesSpecifiedSize() {
        MetaRegionBuilder gen = new MetaRegionBuilder();
        gen.setFirstRoomCenter(new Vector2(0, 0));
        
        // set the see so we have repeatable results
        GenSeed.random.setSeed(151);
        gen.setSize(5);

        Region region = gen.generateMetaRegion();
        
        assertEquals(5, region.getSize(), "Region size was: " + region.getSize() + " expected: 5");
        assertEquals(5, region.getMetaList().size, "Region meta list size was: " + region.getMetaList().size + " expected: 5");
    }
    
}
