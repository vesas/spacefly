package com.vesas.spacefly.world.procedural.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

import org.junit.jupiter.api.Test;

/**
 * Pure (no LibGDX GL, no Mockito) tests for the loop-closing pass. Exercises the meta-graph
 * geometry only, which is where all the loop logic lives.
 */
public class MetaLoopCloserTest {

    private MetaRectangleRoom room(float x, float y, float w, float h) {
        MetaRectangleRoom r = new MetaRectangleRoom();
        r.setSize(x, y, w, h);
        return r;
    }

    private Region regionOf(MetaRectangleRoom... rooms) {
        Region region = new Region();
        for (MetaRectangleRoom r : rooms)
            region.add(r);
        return region;
    }

    private MetaCorridor firstCorridor(Region region) {
        Array<MetaFeature> list = region.getMetaList();
        for (int i = 0; i < list.size; i++)
            if (list.get(i) instanceof MetaCorridor)
                return (MetaCorridor) list.get(i);
        return null;
    }

    /**
     * Two rooms with one above the other and a clear gap between them: for at least some
     * seeds a vertical bridge corridor is added, and whenever it is, the geometry lines up
     * with both rooms' new off-centre doorways.
     */
    @Test
    public void bridgesTwoStackedRooms() {
        int loopsSeen = 0;

        for (long seed = 0; seed < 60; seed++) {
            MetaRectangleRoom a = room(0, 0, 8, 6);   // bottom, top edge at y=6
            MetaRectangleRoom b = room(1, 10, 8, 6);  // top, bottom edge at y=10
            Region region = regionOf(a, b);

            new MetaLoopCloser(seed).addLoops(region);

            MetaCorridor corr = firstCorridor(region);
            if (corr == null) {
                // Gate declined this seed; rooms must be untouched.
                assertNull(a.getPortal(ExitDir.N), "no corridor but room A gained a door");
                assertNull(b.getPortal(ExitDir.S), "no corridor but room B gained a door");
                continue;
            }
            loopsSeen++;

            // Both facing walls got a doorway, at the same world-space centre.
            MetaPortal pa = a.getPortal(ExitDir.N);
            MetaPortal pb = b.getPortal(ExitDir.S);
            assertNotNull(pa, "room A should get a north door");
            assertNotNull(pb, "room B should get a south door");
            assertEquals(pa.centerX, pb.centerX, 1e-4, "door centres must align for a straight corridor");
            assertEquals(pa.getWidth(), pb.getWidth(), 1e-4, "both doors share the corridor width");

            // Expected door centre: middle of the x-overlap [1, 8].
            assertEquals(4.5f, pa.centerX, 1e-4);

            // Corridor spans exactly the gap between the two rooms.
            Rectangle cb = corr.getBounds();
            float w = corr.getWidth();
            assertEquals(6f, cb.y, 1e-4, "corridor starts at room A's top wall");
            assertEquals(4f, cb.height, 1e-4, "corridor length equals the gap (10 - 6)");
            assertEquals(4f, corr.getLength(), 1e-4);
            assertEquals(w + 1.0f, cb.width, 1e-4, "bounds include a 0.5 wall each side");
            // Walkable interior [centre - w/2, centre + w/2] must equal the door span.
            assertEquals(pa.centerX - w * 0.5f, cb.x + 0.5f, 1e-4);

            // Corridor orientation is vertical and both ends are open (loop, not dead end).
            assertEquals(ExitDir.N, corr.getStartPortal().getExit());
            assertNotNull(corr.getEndPortal(), "a loop corridor keeps both ends open");
        }

        assertTrue(loopsSeen > 0, "expected at least one seed to add a loop across 60 tries");
    }

    /** Rooms too far apart (gap beyond MAX_LEN) must never be bridged. */
    @Test
    public void doesNotBridgeDistantRooms() {
        for (long seed = 0; seed < 60; seed++) {
            MetaRectangleRoom a = room(0, 0, 8, 6);    // top edge at y=6
            MetaRectangleRoom b = room(1, 40, 8, 6);   // 34 units away, well over MAX_LEN
            Region region = regionOf(a, b);

            new MetaLoopCloser(seed).addLoops(region);

            assertNull(firstCorridor(region), "distant rooms must not be bridged");
            assertEquals(2, region.getSize(), "region should be unchanged");
        }
    }

    /**
     * Runs the real generator (same size the game uses) through the loop closer over many
     * seeds: a meaningful fraction of real floors should gain at least one loop, and no
     * floor should ever exceed the cap. This exercises the actual generated topology, not
     * hand-built rooms.
     */
    @Test
    public void addsLoopsToRealFloors() {
        final int SEEDS = 80;
        int floorsWithLoops = 0;

        for (long seed = 0; seed < SEEDS; seed++) {
            // Mirror the game's per-floor conditions (it resets the id counter each floor).
            IDGenerator.reset();
            MetaRegionBuilder gen = new MetaRegionBuilder();
            gen.setFirstRoomCenter(new Vector2(0, 0));
            GenSeed.random.setSeed(seed);
            gen.setSize(44);
            Region region = gen.generateMetaRegion();

            int before = region.getSize();
            new MetaLoopCloser(seed).addLoops(region);
            int added = region.getSize() - before;

            assertTrue(added >= 0 && added <= 6, "loops added out of range: " + added);
            if (added > 0)
                floorsWithLoops++;
        }

        assertTrue(floorsWithLoops > 0, "loop closer added no loops to any real floor");
    }

    /**
     * Regression for the "gap at the loop connection" bug. Rooms are NOT on a 0.5 grid: the
     * first room starts at a fractional offset and octagon rooms inject (1+sqrt2) factors,
     * so a bridge's length is an arbitrary real (e.g. 1.2071). The corridor must span exactly
     * that gap and its doorway must line up with the corridor's walkable centre — the wall
     * builders now cover fractional lengths, so nothing is snapped to 0.5 and left short of
     * the far room.
     */
    @Test
    public void loopCorridorSpansExactGapWithOffGridRooms() {
        final float gap = 1.2071f; // (1+sqrt2)/2 — octagon-derived spacing seen in-game
        boolean checked = false;

        for (long seed = 0; seed < 60 && !checked; seed++) {
            MetaRectangleRoom a = room(0.45f, 0.45f, 8, 6);            // top edge at 6.45
            MetaRectangleRoom b = room(1.45f, 0.45f + 6 + gap, 8, 6);  // bottom edge = a.top + gap
            Region region = regionOf(a, b);
            new MetaLoopCloser(seed).addLoops(region);

            MetaCorridor corr = firstCorridor(region);
            if (corr == null) continue;
            checked = true;

            Rectangle cb = corr.getBounds();
            float aTop = a.getBounds().y + a.getBounds().height;
            float bBottom = b.getBounds().y;

            // Corridor spans exactly the (fractional) gap, not a snapped 0.5 multiple.
            assertEquals(gap, corr.getLength(), 1e-3, "corridor length must equal the true gap");
            assertEquals(aTop, cb.y, 1e-3, "corridor starts at room A's top wall");
            assertEquals(bBottom, cb.y + cb.height, 1e-3, "corridor reaches room B's bottom wall");

            // The doorway lines up with the corridor's walkable interior centre.
            MetaPortal pa = a.getPortal(ExitDir.N);
            assertEquals(pa.centerX, cb.x + cb.width * 0.5f, 1e-3, "door centre must match corridor centre");
        }

        assertTrue(checked, "expected at least one bridge to check geometry");
    }

    /** A wall that already has a door is never given a second opening. */
    @Test
    public void skipsWallsThatAlreadyHaveADoor() {
        for (long seed = 0; seed < 60; seed++) {
            MetaRectangleRoom a = room(0, 0, 8, 6);
            MetaRectangleRoom b = room(1, 10, 8, 6);
            // Pre-occupy the facing walls: A's north and B's south.
            a.addPortal(ExitDir.N, new MetaPortal(2));
            b.addPortal(ExitDir.S, new MetaPortal(2));
            Region region = regionOf(a, b);

            new MetaLoopCloser(seed).addLoops(region);

            assertNull(firstCorridor(region), "occupied facing walls must not be bridged");
        }
    }
}
