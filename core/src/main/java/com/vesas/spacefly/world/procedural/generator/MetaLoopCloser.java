package com.vesas.spacefly.world.procedural.generator;

import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

/**
 * Post-processes a generated {@link Region} tree by adding a few extra corridors that
 * connect two already-placed rooms, turning the pure tree into a graph with loops.
 *
 * <p>The base generator only ever grows a tree — a portal that would overlap an existing
 * feature is abandoned — so there is exactly one path between any two rooms, which forces
 * a lot of dead-end backtracking. This pass bridges nearby rooms whose facing walls are
 * still solid, giving the player shortcuts.
 *
 * <p>It runs on the meta graph <em>before</em> the real (Box2D / visibility) pass and has
 * two important properties:
 * <ul>
 *   <li>It uses its own {@link Random}, so it never disturbs the {@code GenSeed.random}
 *       stream that drives floor tiles, pipes and monster placement.</li>
 *   <li>New corridors are appended to the <em>end</em> of the region, so they are built
 *       last and consume RNG last. A floor that gains no loops is therefore byte-identical
 *       to what the generator produced before this pass existed.</li>
 * </ul>
 *
 * <p>Only plain rectangle rooms are used as bridge endpoints (column rooms keep their
 * centred-door-only rendering, octagon rooms use a different builder). The off-centre
 * doorway support this relies on lives in {@code RectangleRoomBuilder.doorBegin}.
 */
public class MetaLoopCloser
{
	/** Baseline cap on how many bridges we add, and the odds a viable candidate is taken. */
	private static final int   BASE_MAX_LOOPS = 6;
	private static final int   LOOP_CAP        = 10;
	private static final float LOOP_CHANCE = 0.8f;

	/** Effective bridge cap this floor: grows one per depth level, clamped to LOOP_CAP. */
	private final int maxLoops;

	/** A bridge must read as a corridor (not a shared wall) but not span the whole map. */
	private static final float MIN_LEN = 1.0f;
	private static final float MAX_LEN = 14.0f;

	/** Widest bridge doorway we will cut. */
	private static final float MAX_DOOR = 5.0f;

	/**
	 * Fractions of the slide range to try for the doorway centre, in order. The midpoint
	 * (0) is preferred; the offsets let a bridge route around a feature that clips the
	 * centred path — which is only possible because rooms now support off-centre doors.
	 */
	private static final float[] SLIDE = { 0.0f, 0.32f, -0.32f, 0.60f, -0.60f };

	private final Random rnd;

	public MetaLoopCloser( long seed )
	{
		this( seed, 1 );
	}

	public MetaLoopCloser( long seed, int floor )
	{
		// Independent, reproducible stream derived from the floor seed. Deliberately NOT
		// GenSeed.random, so downstream generation is unaffected when no loop is added.
		this.rnd = new Random( seed * 0x9E3779B97F4A7C15L + 0x632BE59BD9B4E019L );
		this.maxLoops = Math.min( LOOP_CAP, BASE_MAX_LOOPS + Math.max( 0, floor - 1 ) );
	}

	public void addLoops( Region region )
	{
		// Snapshot the eligible rooms up front, because we mutate the region as we go.
		Array<MetaRectangleRoom> rooms = new Array<MetaRectangleRoom>();
		Array<MetaFeature> metaList = region.getMetaList();
		for( int i = 0; i < metaList.size; i++ )
		{
			MetaFeature f = metaList.get(i);
			if( f instanceof MetaRectangleRoom )
			{
				MetaRectangleRoom r = (MetaRectangleRoom) f;
				if( !r.hasColumns() )
					rooms.add(r);
			}
		}

		int added = 0;
		// Only probe North and East from each room; that visits every room pair once.
		for( int i = 0; i < rooms.size && added < maxLoops; i++ )
		{
			MetaRectangleRoom r = rooms.get(i);

			if( r.getPortal(ExitDir.N) == null && tryBridge(region, rooms, r, ExitDir.N) )
				added++;

			if( added >= maxLoops )
				break;

			if( r.getPortal(ExitDir.E) == null && tryBridge(region, rooms, r, ExitDir.E) )
				added++;
		}
	}

	/**
	 * Tries to bridge room {@code r}'s solid wall on {@code side} to the nearest facing
	 * room across a clear gap. Returns true if a corridor was added.
	 */
	private boolean tryBridge( Region region, Array<MetaRectangleRoom> rooms,
			MetaRectangleRoom r, ExitDir side )
	{
		final Rectangle rb = r.getBounds();
		final ExitDir otherSide = side.getOpposite();

		MetaRectangleRoom best = null;
		float bestLen = Float.MAX_VALUE;
		float bestCenter = 0f, bestWidth = 0f;

		for( int j = 0; j < rooms.size; j++ )
		{
			MetaRectangleRoom o = rooms.get(j);
			if( o == r )
				continue;

			// The facing wall on the other room must be solid too (one door per wall).
			if( o.getPortal(otherSide) != null )
				continue;

			final Rectangle ob = o.getBounds();

			float len, lo, hi;

			if( side == ExitDir.N )
			{
				len = ob.y - (rb.y + rb.height);                 // vertical gap
				lo = Math.max(rb.x, ob.x);                        // shared span, x axis
				hi = Math.min(rb.x + rb.width, ob.x + ob.width);
			}
			else // ExitDir.E
			{
				len = ob.x - (rb.x + rb.width);                  // horizontal gap
				lo = Math.max(rb.y, ob.y);                        // shared span, y axis
				hi = Math.min(rb.y + rb.height, ob.y + ob.height);
			}

			if( len < MIN_LEN || len > MAX_LEN || len >= bestLen )
				continue;

			float width = doorWidthFor(hi - lo);
			if( width <= 0f )
				continue;

			// Find a doorway centre whose bridge corridor is clear of every other feature,
			// sliding along the shared wall to route around blockers.
			float center = clearCenter(region, side, rb, r, o, lo, hi, width, len);
			if( Float.isNaN(center) )
				continue;

			best = o;
			bestLen = len;
			bestCenter = center;
			bestWidth = width;
		}

		if( best == null )
			return false;

		// Gate only once a real candidate exists, so RNG use stays predictable.
		if( rnd.nextFloat() >= LOOP_CHANCE )
			return false;

		connect(region, r, best, side, bestCenter, bestWidth, bestLen);
		return true;
	}

	/** Largest integer door width leaving a >=1 jamb on both walls; 0 means "won't fit". */
	private float doorWidthFor( float overlap )
	{
		float w = (float) Math.floor(overlap - 2f);
		if( w < 1f )
			return 0f;
		return Math.min(MAX_DOOR, w);
	}

	/**
	 * Picks a doorway centre in the shared span [{@code lo},{@code hi}] whose bridge
	 * corridor clears every other feature, trying the midpoint first then sliding toward
	 * each end. Returns {@link Float#NaN} if every candidate position is blocked.
	 */
	private float clearCenter( Region region, ExitDir side, Rectangle rb,
			MetaFeature a, MetaFeature b, float lo, float hi, float width, float len )
	{
		float mid = (lo + hi) * 0.5f;
		// How far the centre may slide from the midpoint while keeping a >=1 jamb on both
		// rooms' walls.
		float half = (hi - lo) * 0.5f - 1f - width * 0.5f;
		if( half < 0f )
			half = 0f;

		for( int i = 0; i < SLIDE.length; i++ )
		{
			float center = mid + SLIDE[i] * half;
			Rectangle probe = corridorBounds(side, rb, center, width, len);
			if( isClear(region, probe, a, b) )
				return center;
		}
		return Float.NaN;
	}

	/** Full (wall-inclusive) bounds of the bridge corridor, for the overlap test. */
	private Rectangle corridorBounds( ExitDir side, Rectangle rb,
			float center, float width, float len )
	{
		if( side == ExitDir.N )
			return new Rectangle(center - width * 0.5f - 0.5f, rb.y + rb.height, width + 1.0f, len);
		else // E
			return new Rectangle(rb.x + rb.width, center - width * 0.5f - 0.5f, len, width + 1.0f);
	}

	/** True if {@code probe} overlaps no feature other than the two rooms it connects. */
	private boolean isClear( Region region, Rectangle probe, MetaFeature a, MetaFeature b )
	{
		Array<MetaFeature> list = region.getMetaList();
		for( int i = 0; i < list.size; i++ )
		{
			MetaFeature f = list.get(i);
			if( f == a || f == b )
				continue;
			if( f.overlaps(probe) )
				return false;
		}
		return true;
	}

	/**
	 * Builds the bridge corridor and the two off-centre doorways (one per room) and adds
	 * the corridor to the region. Both room doors share the same world-space centre so the
	 * straight corridor lines up with both openings.
	 */
	private void connect( Region region, MetaRectangleRoom r, MetaRectangleRoom o,
			ExitDir side, float center, float width, float len )
	{
		final Rectangle rb = r.getBounds();
		final Rectangle ob = o.getBounds();

		MetaCorridor corr = new MetaCorridor();

		MetaPortal startPortal = new MetaPortal(width);
		startPortal.START_TYPE = MetaPortal.RECTANGLE_ROOM;
		MetaPortal endPortal = new MetaPortal(width);
		endPortal.START_TYPE = MetaPortal.RECTANGLE_ROOM;

		if( side == ExitDir.N )
		{
			corr.setSize(center - width * 0.5f - 0.5f, rb.y + rb.height, width + 1.0f, len);
			corr.setLengthWidth(len, width);

			startPortal.centerX = center;
			startPortal.centerY = rb.y + rb.height;
			startPortal.setSource(r);

			endPortal.centerX = center;
			endPortal.centerY = ob.y;
			endPortal.setSource(o);

			// startPortal.exit = N drives the corridor's orientation; the builder ignores
			// endPortal's exit. Both portals non-null => both ends stay open.
			corr.addStartPortal(ExitDir.N, startPortal);
			corr.addEndPortal(ExitDir.N, endPortal);

			startPortal.setTarget(corr);
			endPortal.setTarget(corr);

			r.addPortal(ExitDir.N, startPortal);   // door in r's north wall
			o.addPortal(ExitDir.S, endPortal);     // door in o's south wall
		}
		else // ExitDir.E
		{
			corr.setSize(rb.x + rb.width, center - width * 0.5f - 0.5f, len, width + 1.0f);
			corr.setLengthWidth(len, width);

			startPortal.centerX = rb.x + rb.width;
			startPortal.centerY = center;
			startPortal.setSource(r);

			endPortal.centerX = ob.x;
			endPortal.centerY = center;
			endPortal.setSource(o);

			corr.addStartPortal(ExitDir.E, startPortal);
			corr.addEndPortal(ExitDir.E, endPortal);

			startPortal.setTarget(corr);
			endPortal.setTarget(corr);

			r.addPortal(ExitDir.E, startPortal);   // door in r's east wall
			o.addPortal(ExitDir.W, endPortal);     // door in o's west wall
		}

		region.add(corr);
	}
}
