package com.vesas.spacefly.particles;

import com.vesas.spacefly.game.Screen;

public interface ExplosionInterface
{

	public void draw(Screen screen);
	
	public void tick( float delta );
	
	public boolean isFinished();
}
