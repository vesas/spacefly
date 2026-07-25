package com.vesas.spacefly.world.procedural.generator;

/**
 * A generated room's purpose. Assigned by {@link MetaRoleAssigner} after the meta graph is
 * built. The role never draws an icon or a marker. It is expressed through systems that already
 * exist and read seamlessly:
 * <ul>
 *   <li><b>Ambient light</b> — the live world lerps the global ambient toward a per-role tint as
 *       the player enters the room, so the mood shifts <em>temporally</em> with no hard floor
 *       seam at the doorway.</li>
 *   <li><b>Architecture / shadow</b> — the assigner clears a column (ARENA, open sightlines) or
 *       adds one (large VAULT, an occluding mass that casts line-of-sight shadow).</li>
 *   <li><b>Decor density</b> — a VAULT gains an extra grate + junction box ("instrumented").</li>
 *   <li><b>Gated loot</b> — a VAULT's reward appears only after its guardians are cleared.</li>
 * </ul>
 *
 * <p>The floor Pixmap itself is left as the theme base for every role, so ordinary rooms — and
 * the {@code GenSeed.random} stream — are untouched, and no two adjacent floors ever show a
 * baked colour border.
 */
public enum RoomRole
{
	STANDARD,
	ARENA,
	SANCTUARY,
	VAULT,
	BOSS
}
