package com.vesas.spacefly.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.vesas.spacefly.GameScreen;

public interface AnimateEntity
{
	public void tick( float delta );

	public void draw(GameScreen screen);
	
	public void destroy();
	
	public Body getBody();
}
