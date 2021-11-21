package com.vesas.spacefly.game;

import com.badlogic.gdx.physics.box2d.Body;

public interface AnimateEntity
{
	public void tick( float delta );

	public void draw(Screen screen);
	
	public void destroy();
	
	public Body getBody();
}
