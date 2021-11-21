package com.vesas.spacefly.game.cameraeffects;

import com.badlogic.gdx.math.Vector2;

public class ShakeExplo extends Shake
{
	

	public void tick( float delta ) 
	{ 
		super.tick( delta );
	}

	@Override
	public void performOn(Vector2 pos)
	{
		float val = interpolation.apply( 1.0f - t );
		
		pos.x += direction.x * val * strength;
		pos.y += direction.y * val * strength;
	}
}
