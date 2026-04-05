package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.graphics.Color;

public enum FloorTheme {

    STATION ("tile64",         "tile642",         "edgeA10",         "edge_tri",
             new Color(0.24f, 0.25f, 0.33f, 1.0f), 0.50f, 0.50f, 0.70f),

    BIOLAB  ("tile64_biolab",  "tile642_biolab",  "edgeA10_biolab",  "edge_tri_biolab",
             new Color(0.17f, 0.27f, 0.20f, 1.0f), 0.35f, 0.62f, 0.38f),

    REACTOR ("tile64_reactor", "tile642_reactor", "edgeA10_reactor", "edge_tri_reactor",
             new Color(0.28f, 0.17f, 0.15f, 1.0f), 0.65f, 0.35f, 0.22f),

    CRYO    ("tile64_cryo",    "tile642_cryo",    "edgeA10_cryo",    "edge_tri_cryo",
             new Color(0.19f, 0.22f, 0.34f, 1.0f), 0.38f, 0.52f, 0.82f);

    /** Atlas key for the primary floor stamp tile. */
    public final String floorTile1;
    /** Atlas key for the secondary floor stamp tile. */
    public final String floorTile2;
    /** Atlas key for the wall block texture. */
    public final String wallTex;
    /** Atlas key for the octaroom wall corner wedge sprite. */
    public final String cornerTex;
    /** Base floor fill colour used when building the floor Pixmap. */
    public final Color floorColor;
    /** Visibility shader ambient light colour (RGB). */
    public final float ambR, ambG, ambB;

    FloorTheme(String floorTile1, String floorTile2, String wallTex, String cornerTex,
               Color floorColor, float ambR, float ambG, float ambB) {
        this.floorTile1  = floorTile1;
        this.floorTile2  = floorTile2;
        this.wallTex     = wallTex;
        this.cornerTex   = cornerTex;
        this.floorColor  = floorColor;
        this.ambR        = ambR;
        this.ambG        = ambG;
        this.ambB        = ambB;
    }
}
