package com.vesas.spacefly.world.procedural.generator;

/**
 * Marks a {@link MetaFeature} that is a room (rectangle or octagon) and can therefore carry a
 * {@link RoomRole}. Corridors deliberately do not implement this — they never carry a role.
 * Keeping the role off {@link MetaFeature} keeps the change surface minimal.
 */
public interface MetaRoom extends MetaFeature
{
	RoomRole getRole();

	void setRole( RoomRole role );
}
