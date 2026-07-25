package com.vesas.spacefly.world.procedural.generator;

import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Post-processes a generated {@link Region} by tagging a handful of rooms with a
 * {@link RoomRole}. Runs <em>after</em> {@link MetaLoopCloser}, so a room's true connectivity
 * (its open-portal count, including any loop doors) is known, and <em>before</em> the real
 * (Box2D / visibility) pass, so the builders can read the role while constructing geometry.
 *
 * <p>Like {@link MetaLoopCloser}, it uses its own {@link Random} seeded from the floor seed and
 * never touches {@code GenSeed.random}, so role <em>selection</em> does not perturb the tile /
 * pipe / monster stream. The two architecture edits it makes ({@link RoomRole#ARENA} clears a
 * central column, large {@link RoomRole#VAULT} adds one) do change what a couple of special
 * rooms build, and therefore the stream from those rooms onward — this is deterministic and
 * intended: the column is the shadow signal for "open hall" vs "occluded vault".
 *
 * <p>It creates no {@link MetaFeature} or {@link MetaPortal}, so the {@code IDGenerator} counter
 * is untouched.
 */
public class MetaRoleAssigner
{
	/** Widest half-column an added vault column uses; leaves a walkable ring on an 8-wide room. */
	private static final float VAULT_COLUMN_HALF = 2.0f;
	/** A rect vault only gains an occluding column if its smaller side is at least this wide. */
	private static final float VAULT_COLUMN_MIN_DIM = 8.0f;

	private final Random rnd;

	public MetaRoleAssigner( long seed )
	{
		// Independent, reproducible stream derived from the floor seed. NOT GenSeed.random.
		this.rnd = new Random( seed * 0x2545F4914F6CDD1DL + 0x9E3779B97F4A7C15L );
	}

	public void assign( Region region, int floor, Vector2 firstRoomCenter )
	{
		final int d = Math.max( 0, floor - 1 );

		Array<MetaFeature> list = region.getMetaList();
		if( list.size == 0 )
			return;

		MetaFeature firstRoom = list.get( 0 );

		// Rooms only (skip corridors).
		Array<MetaFeature> rooms = new Array<MetaFeature>();
		for( int i = 0; i < list.size; i++ )
		{
			MetaFeature f = list.get( i );
			if( f instanceof MetaRoom )
				rooms.add( f );
		}
		if( rooms.size == 0 )
			return;

		// BOSS = room whose centre is farthest from the start — same rule the LevelExit uses,
		// so BOSS is always the exit room. Exactly one.
		MetaFeature boss = null;
		float maxDist2 = -1f;
		for( int i = 0; i < rooms.size; i++ )
		{
			MetaFeature f = rooms.get( i );
			float d2 = centerDist2( f, firstRoomCenter );
			if( d2 > maxDist2 )
			{
				maxDist2 = d2;
				boss = f;
			}
		}
		if( boss != null )
			((MetaRoom) boss).setRole( RoomRole.BOSS );

		// Dead-end pool: exactly one open portal, excluding BOSS and the first room.
		Array<MetaFeature> deadEnds = new Array<MetaFeature>();
		for( int i = 0; i < rooms.size; i++ )
		{
			MetaFeature f = rooms.get( i );
			if( f == boss || f == firstRoom )
				continue;
			if( f.getPortalArray( null ).size == 1 )
				deadEnds.add( f );
		}

		// VAULT(s): the largest-area dead-ends. Count grows slowly with depth.
		int vaultCount = 1 + d / 3;
		for( int v = 0; v < vaultCount; v++ )
		{
			MetaFeature best = null;
			float bestArea = -1f;
			for( int i = 0; i < deadEnds.size; i++ )
			{
				MetaFeature f = deadEnds.get( i );
				if( ((MetaRoom) f).getRole() != RoomRole.STANDARD )
					continue; // already a vault
				float area = area( f );
				// tie-break on lowest id for determinism
				if( area > bestArea || (area == bestArea && best != null && f.getId() < best.getId()) )
				{
					bestArea = area;
					best = f;
				}
			}
			if( best == null )
				break;

			((MetaRoom) best).setRole( RoomRole.VAULT );

			// Architecture: a large rectangular vault gets an occluding central column so its
			// loot sits in shadow. Small vaults lean on floor material alone.
			if( best instanceof MetaRectangleRoom )
			{
				MetaRectangleRoom r = (MetaRectangleRoom) best;
				Rectangle b = r.getBounds();
				if( !r.hasColumns() && Math.min( b.width, b.height ) >= VAULT_COLUMN_MIN_DIM )
				{
					r.setHasColumns( true );
					r.setHalfColumnWidth( VAULT_COLUMN_HALF );
				}
			}
		}

		// SANCTUARY: one remaining dead-end, preferring an octagon (its symmetry reads as a calm
		// chamber). Appearance chance rises gently with depth.
		float sanctChance = Math.min( 0.85f, 0.5f + 0.08f * d );
		if( rnd.nextFloat() < sanctChance )
		{
			MetaFeature sanct = pickSanctuary( deadEnds );
			if( sanct != null )
				((MetaRoom) sanct).setRole( RoomRole.SANCTUARY );
		}

		// ARENA: a non-dead-end big room (a column room), opened up by clearing its column for
		// long, clean sightlines. Cap grows slowly with depth.
		int arenaCap = 1 + d / 3;
		int arenaCount = 0;
		for( int i = 0; i < rooms.size && arenaCount < arenaCap; i++ )
		{
			MetaFeature f = rooms.get( i );
			if( f == boss || f == firstRoom )
				continue;
			if( ((MetaRoom) f).getRole() != RoomRole.STANDARD )
				continue;
			if( f.getPortalArray( null ).size <= 1 )
				continue; // dead end — not an arena
			if( !(f instanceof MetaRectangleRoom) )
				continue;
			MetaRectangleRoom r = (MetaRectangleRoom) f;
			if( !r.hasColumns() )
				continue; // "big" is signalled by the meta builder giving it a column

			r.setRole( RoomRole.ARENA );
			r.setHasColumns( false ); // open the floor
			arenaCount++;
		}
	}

	/** Prefer an octagon dead-end (lowest id); else the smallest-area still-STANDARD dead-end. */
	private MetaFeature pickSanctuary( Array<MetaFeature> deadEnds )
	{
		MetaFeature octa = null;
		for( int i = 0; i < deadEnds.size; i++ )
		{
			MetaFeature f = deadEnds.get( i );
			if( ((MetaRoom) f).getRole() != RoomRole.STANDARD )
				continue;
			if( f instanceof MetaOctaRoom && (octa == null || f.getId() < octa.getId()) )
				octa = f;
		}
		if( octa != null )
			return octa;

		MetaFeature smallest = null;
		float bestArea = Float.MAX_VALUE;
		for( int i = 0; i < deadEnds.size; i++ )
		{
			MetaFeature f = deadEnds.get( i );
			if( ((MetaRoom) f).getRole() != RoomRole.STANDARD )
				continue;
			float area = area( f );
			if( area < bestArea || (area == bestArea && smallest != null && f.getId() < smallest.getId()) )
			{
				bestArea = area;
				smallest = f;
			}
		}
		return smallest;
	}

	private static float area( MetaFeature f )
	{
		Rectangle b = f.getBounds();
		return b.width * b.height;
	}

	private static float centerDist2( MetaFeature f, Vector2 from )
	{
		Rectangle b = f.getBounds();
		float cx = b.x + b.width * 0.5f;
		float cy = b.y + b.height * 0.5f;
		float dx = cx - from.x;
		float dy = cy - from.y;
		return dx * dx + dy * dy;
	}
}
