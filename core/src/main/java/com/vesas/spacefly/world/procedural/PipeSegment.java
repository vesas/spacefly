package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vesas.spacefly.game.G;

public class PipeSegment {

    public static final float PIPE_DIAMETER = 12f / 64f; // 12px at 64px/unit = 0.1875 wu
    private static final float TILE_LONG    = 1.0f;      // 64px at 64px/unit

    /** How a run terminates at one end. */
    public enum End {
        TUCK,   // runs flush into a wall corner — nothing drawn
        CAP,    // flange cap closes an open end
        ELBOW   // 90° bend turning toward the adjacent wall, so the pipe "goes somewhere"
    }

    // End-cap ("pipe_cap") sprite: 18x8px flange. CAP_LONG spans across the pipe,
    // CAP_SHORT runs along the pipe axis.
    private static final float CAP_LONG  = 18f / 64f;
    private static final float CAP_SHORT =  8f / 64f;

    // Pipes are background detail, not a highlight. The source sprite is near-white,
    // so multiply it down to a dim, low-contrast grey-blue and let a little floor
    // bleed through. Tune here if pipes read too strong / too faint.
    private static final float TINT_R = 0.42f, TINT_G = 0.48f, TINT_B = 0.56f, TINT_A = 0.85f;

    private final TextureRegion region;
    private final TextureRegion capRegion;
    private final TextureRegion elbowRegion;
    private final float x, y, length;
    private final boolean horizontal;

    // End treatments. lo = low-coordinate end (left for EW, bottom for NS); hi = high end.
    private End endLo = End.TUCK;
    private End endHi = End.TUCK;
    // Direction toward the parallel wall this run hugs, used to orient elbows.
    // Horizontal: +1 wall is above (N), -1 below (S). Vertical: +1 wall right (E), -1 left (W).
    private int wallSide = 0;

    private PipeSegment(float x, float y, float length, boolean horizontal) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.horizontal = horizontal;
        this.region = G.getAtlas().findRegion("pipe_h");
        this.capRegion = G.getAtlas().findRegion("pipe_cap");
        this.elbowRegion = G.getAtlas().findRegion("pipe_elbow");
    }

    /** EW pipe: runs left-right. x=visual left, y=visual bottom, length along X. */
    public static PipeSegment makeHorizontal(float x, float y, float length) {
        return new PipeSegment(x, y, length, true);
    }

    /** NS pipe: runs up-down. x=visual left, y=visual bottom, length along Y. */
    public static PipeSegment makeVertical(float x, float y, float length) {
        return new PipeSegment(x, y, length, false);
    }

    /**
     * Sets how each end terminates. lo = low-coordinate end, hi = high end.
     * wallSide points toward the wall this run hugs (see field doc) and orients ELBOW ends.
     */
    public PipeSegment ends(End lo, End hi, int wallSide) {
        this.endLo = lo;
        this.endHi = hi;
        this.wallSide = wallSide;
        return this;
    }

    public void draw(Batch batch) {
        float prev = batch.getPackedColor();
        batch.setColor(TINT_R, TINT_G, TINT_B, TINT_A);
        if (horizontal) {
            drawHorizontal(batch);
        } else {
            drawVertical(batch);
        }
        renderEnd(batch, false);
        renderEnd(batch, true);
        batch.setPackedColor(prev);
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

    /** Draws the terminator (cap or elbow) for one end. hi = high-coordinate end. */
    private void renderEnd(Batch batch, boolean hi) {
        End e = hi ? endHi : endLo;
        if (e == End.TUCK) return;
        float d = PIPE_DIAMETER;
        if (horizontal) {
            float cy = y + d / 2f;
            float endX = hi ? x + length : x;
            if (e == End.CAP) {
                drawCap(batch, endX, cy);
            } else {
                drawElbow(batch, hi ? endX + d / 2f : endX - d / 2f, cy, elbowRot(hi));
            }
        } else {
            float cx = x + d / 2f;
            float endY = hi ? y + length : y;
            if (e == End.CAP) {
                drawCap(batch, cx, endY);
            } else {
                drawElbow(batch, cx, hi ? endY + d / 2f : endY - d / 2f, elbowRot(hi));
            }
        }
    }

    /** CCW rotation for the elbow sprite (canonical openings LEFT+TOP) turning toward wallSide. */
    private float elbowRot(boolean hi) {
        if (horizontal) {
            if (wallSide > 0) return hi ? 0f   : 270f; // wall above (N)
            else              return hi ? 90f  : 180f; // wall below (S)
        } else {
            if (wallSide < 0) return hi ? 90f  : 0f;   // wall left (W)
            else              return hi ? 180f : 270f; // wall right (E)
        }
    }

    /** Draws the flange cap centred on (cx, cy), oriented across the pipe. */
    private void drawCap(Batch batch, float cx, float cy) {
        if (capRegion == null) return;
        float bx = cx - CAP_LONG / 2f;
        float by = cy - CAP_SHORT / 2f;
        if (horizontal) {
            batch.draw(capRegion, bx, by,
                       CAP_LONG / 2f, CAP_SHORT / 2f,
                       CAP_LONG, CAP_SHORT,
                       1f, 1f, 90f);
        } else {
            batch.draw(capRegion, bx, by, CAP_LONG, CAP_SHORT);
        }
    }

    /** Draws the quarter-turn elbow in a D×D box centred on (cx, cy), rotated `rot` degrees CCW. */
    private void drawElbow(Batch batch, float cx, float cy, float rot) {
        if (elbowRegion == null) return;
        float d = PIPE_DIAMETER;
        batch.draw(elbowRegion, cx - d / 2f, cy - d / 2f,
                   d / 2f, d / 2f, d, d, 1f, 1f, rot);
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
