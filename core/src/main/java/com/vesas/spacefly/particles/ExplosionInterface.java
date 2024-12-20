package com.vesas.spacefly.particles;

import com.vesas.spacefly.screen.GameScreen;

public interface ExplosionInterface
{

	public void draw(GameScreen screen);
	
	public void tick( float delta );
	
	public boolean isFinished();
}
