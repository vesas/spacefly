package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vesas.spacefly.game.G;

public class PipeSegment {

    public static final float PIPE_DIAMETER = 12f / 64f; // 12px at 64px/unit = 0.1875 wu
    private static final float TILE_LONG    = 1.0f;      // 64px at 64px/unit

    private final TextureRegion region;
    private final float x, y, length;
    private final boolean horizontal;

    private PipeSegment(float x, float y, float length, boolean horizontal) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.horizontal = horizontal;
        this.region = G.getAtlas().findRegion("pipe_h");
    }

    /** EW pipe: runs left-right. x=visual left, y=visual bottom, length along X. */
    public static PipeSegment makeHorizontal(float x, float y, float length) {
        return new PipeSegment(x, y, length, true);
    }

    /** NS pipe: runs up-down. x=visual left, y=visual bottom, length along Y. */
    public static PipeSegment makeVertical(float x, float y, float length) {
        return new PipeSegment(x, y, length, false);
    }

    public void draw(Batch batch) {
        if (horizontal) {
            drawHorizontal(batch);
        } else {
            drawVertical(batch);
        }
    }

    private void drawVertical(Batch batch) {
        float pos = 0f;
        while (pos < length - 0.001f) {
            float tileLen = Math.min(TILE_LONG, length - pos);
            TextureRegion drawReg = tileRegion(tileLen);
            batch.draw(drawReg, x, y + pos, PIPE_DIAMETER, tileLen);
            pos += TILE_LONG;
        }
    }

    private void drawHorizontal(Batch batch) {
        // 90° CCW rotation around sprite centre maps visual tile [x+pos, x+pos+tileLen] x [y, y+D]
        // to sprite bottom-left at (sprX, sprY) with origin (D/2, tileLen/2).
        float pos = 0f;
        while (pos < length - 0.001f) {
            float tileLen = Math.min(TILE_LONG, length - pos);
            TextureRegion drawReg = tileRegion(tileLen);
            float sprX = x + pos + tileLen / 2f - PIPE_DIAMETER / 2f;
            float sprY = y        - tileLen / 2f + PIPE_DIAMETER / 2f;
            batch.draw(drawReg, sprX, sprY,
                       PIPE_DIAMETER / 2f, tileLen / 2f,
                       PIPE_DIAMETER, tileLen,
                       1f, 1f, 90f);
            pos += TILE_LONG;
        }
    }

    /** Returns a TextureRegion covering the first (tileLen / TILE_LONG) fraction of the sprite height. */
    private TextureRegion tileRegion(float tileLen) {
        if (tileLen >= TILE_LONG - 0.001f) return region;
        int pixH = Math.max(1, (int)(tileLen / TILE_LONG * region.getRegionHeight()));
        return new TextureRegion(region.getTexture(),
                region.getRegionX(), region.getRegionY(),
                region.getRegionWidth(), pixH);
    }
}
