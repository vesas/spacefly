package com.vesas.spacefly.world.procedural.room.octaroom;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.room.RoomFeature;

/*
 * Octagonal room
 */
public class OctaRoom extends RoomFeature
{
	// wall width in WORLD units
	public static float WALL_WIDTH = 0.5f;
	
	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();
	
	Texture tex;
	
	public void addBlocks( Array<FeatureBlock> blocks )
	{
	}

	public void draw(SpriteBatch batch)	
	{
		
	}

	public void tick(GameScreen screen, float delta)
	{
	}
	

	@Override
	public void init()
	{
		
	}

	@Override
	public void drawWithVisibility(GameScreen screen) {
		// TODO Auto-generated method stub
		
	}
	

}


