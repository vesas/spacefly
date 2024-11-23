package com.vesas.spacefly.world.procedural.room.rectangleroom;

import com.vesas.spacefly.world.procedural.Feature;
import com.vesas.spacefly.world.procedural.generator.MetaFeature;

public interface FeatureBuilder<T extends MetaFeature> {
    Feature buildFrom(T metaFeature);
}
