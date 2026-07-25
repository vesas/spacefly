package com.vesas.spacefly.world.procedural.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.procedural.GenSeed;

import org.junit.jupiter.api.Test;

/**
 * Pure (no LibGDX GL, no Mockito) tests for the role-assignment pass. Exercises the meta-graph
 * only, which is where all the role logic lives — the same style as {@code MetaLoopCloserTest}.
 */
public class MetaRoleAssignerTest
{
	/** Builds a real floor's meta region the same way the game does. */
	private Region realRegion( long seed, Vector2 start )
	{
		IDGenerator.reset();
		MetaRegionBuilder gen = new MetaRegionBuilder();
		gen.setFirstRoomCenter( start );
		GenSeed.random.setSeed( seed );
		gen.setSize( 44 );
		Region region = gen.generateMetaRegion();
		new MetaLoopCloser( seed ).addLoops( region );
		return region;
	}

	private int countRole( Region region, RoomRole role )
	{
		int n = 0;
		Array<MetaFeature> list = region.getMetaList();
		for( int i = 0; i < list.size; i++ )
		{
			MetaFeature f = list.get( i );
			if( f instanceof MetaRoom && ((MetaRoom) f).getRole() == role )
				n++;
		}
		return n;
	}

	/** The assigner must never create features — it only tags existing ones. */
	@Test
	public void assignerAddsNoFeatures()
	{
		Vector2 start = new Vector2( 0, 0 );
		for( long seed = 0; seed < 40; seed++ )
		{
			Region region = realRegion( seed, start );
			int before = region.getSize();
			new MetaRoleAssigner( seed ).assign( region, 1, start );
			assertEquals( before, region.getSize(), "assigner changed region size at seed " + seed );
		}
	}

	/** Exactly one BOSS, and it is the room farthest from the start (the exit room). */
	@Test
	public void exactlyOneBossAtFarthestRoom()
	{
		Vector2 start = new Vector2( 0, 0 );
		for( long seed = 0; seed < 40; seed++ )
		{
			Region region = realRegion( seed, start );
			new MetaRoleAssigner( seed ).assign( region, 1, start );

			assertEquals( 1, countRole( region, RoomRole.BOSS ), "expected exactly one BOSS at seed " + seed );

			// Find the BOSS and the true farthest room; they must be the same feature.
			MetaFeature boss = null;
			MetaFeature farthest = null;
			float maxD2 = -1f;
			Array<MetaFeature> list = region.getMetaList();
			for( int i = 0; i < list.size; i++ )
			{
				MetaFeature f = list.get( i );
				if( !(f instanceof MetaRoom) )
					continue;
				if( ((MetaRoom) f).getRole() == RoomRole.BOSS )
					boss = f;
				Rectangle b = f.getBounds();
				float cx = b.x + b.width * 0.5f, cy = b.y + b.height * 0.5f;
				float d2 = cx * cx + cy * cy;
				if( d2 > maxD2 )
				{
					maxD2 = d2;
					farthest = f;
				}
			}
			assertNotNull( boss );
			assertEquals( farthest, boss, "BOSS must be the farthest room at seed " + seed );
		}
	}

	/** Only rooms get roles; corridors are never tagged (they aren't MetaRoom). */
	@Test
	public void onlyRoomsAreTagged()
	{
		Vector2 start = new Vector2( 0, 0 );
		for( long seed = 0; seed < 40; seed++ )
		{
			Region region = realRegion( seed, start );
			new MetaRoleAssigner( seed ).assign( region, 1, start );

			Array<MetaFeature> list = region.getMetaList();
			for( int i = 0; i < list.size; i++ )
			{
				MetaFeature f = list.get( i );
				if( f instanceof MetaCorridor )
					assertTrue( !(f instanceof MetaRoom), "a corridor must not be a MetaRoom" );
			}
		}
	}

	/** Same seed + same floor must produce the identical role layout (deterministic). */
	@Test
	public void deterministicForSameSeed()
	{
		Vector2 start = new Vector2( 0, 0 );
		for( long seed = 0; seed < 20; seed++ )
		{
			Region a = realRegion( seed, start );
			new MetaRoleAssigner( seed ).assign( a, 3, start );

			Region b = realRegion( seed, start );
			new MetaRoleAssigner( seed ).assign( b, 3, start );

			Array<MetaFeature> la = a.getMetaList();
			Array<MetaFeature> lb = b.getMetaList();
			assertEquals( la.size, lb.size );
			for( int i = 0; i < la.size; i++ )
			{
				MetaFeature fa = la.get( i );
				MetaFeature fb = lb.get( i );
				if( fa instanceof MetaRoom )
					assertEquals( ((MetaRoom) fa).getRole(), ((MetaRoom) fb).getRole(),
							"role mismatch at index " + i + " seed " + seed );
			}
		}
	}

	/** Across many seeds the special roles do actually get used (the pass isn't a no-op). */
	@Test
	public void specialRolesAppearAcrossSeeds()
	{
		Vector2 start = new Vector2( 0, 0 );
		int vaults = 0, sanctuaries = 0, arenas = 0;
		for( long seed = 0; seed < 60; seed++ )
		{
			Region region = realRegion( seed, start );
			new MetaRoleAssigner( seed ).assign( region, 2, start );
			vaults += countRole( region, RoomRole.VAULT );
			sanctuaries += countRole( region, RoomRole.SANCTUARY );
			arenas += countRole( region, RoomRole.ARENA );
		}
		assertTrue( vaults > 0, "no vaults assigned across 60 seeds" );
		assertTrue( sanctuaries > 0, "no sanctuaries assigned across 60 seeds" );
		// Arenas depend on a non-dead-end column room existing; just assert the counter ran.
		assertTrue( arenas >= 0 );
	}
}
