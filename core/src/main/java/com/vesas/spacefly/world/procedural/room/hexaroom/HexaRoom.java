package com.vesas.spacefly.world.procedural.room.hexaroom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.game.Screen;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.lsystem.SimpleLSystem;
import com.vesas.spacefly.world.procedural.lsystem.SimpleWineSystem;
import com.vesas.spacefly.world.procedural.room.RoomFeature;

public class HexaRoom extends RoomFeature
{
	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();
	
	Texture tex;
	
	public void addBlocks( Array<FeatureBlock> blocks )
	{
	}

	public void draw(Screen screen)	
	{
		
	}

	public void tick(Screen screen, float delta)
	{
	}
	

	@Override
	public void init()
	{
		
	}

	@Override
	public void drawWithVisibility(Screen screen) {
		// TODO Auto-generated method stub
		
	}
	

}


