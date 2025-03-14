package com.vesas.spacefly.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.vesas.spacefly.screen.GameScreen;

public interface AnimateEntity
{
	void tick(GameScreen screen, float delta );
	void draw(GameScreen screen);
	void destroy();
	Body getBody();
}
