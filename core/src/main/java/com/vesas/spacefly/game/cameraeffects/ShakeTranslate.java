package com.vesas.spacefly.game.cameraeffects;

import com.badlogic.gdx.math.Vector2;

public class ShakeTranslate extends Shake
{
	public void performOn( Vector2 pos )
	{
		pos.x = pos.x + 0.0f;
		pos.y = pos.y + 0.0f;
	}
	
}
