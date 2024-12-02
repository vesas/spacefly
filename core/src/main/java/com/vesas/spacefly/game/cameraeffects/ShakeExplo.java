package com.vesas.spacefly.game.cameraeffects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.vesas.spacefly.util.SimplexNoise;

public class ShakeExplo extends Shake
{
	private float trauma = 1.0f; // Trauma decreases over time
    private float traumaPower = 2; // Higher = more sudden falloff
    private float noiseOffset = MathUtils.random(0, 1000); // Random starting point
    
	@Override
    public void performOn(Vector2 pos) {
        // Decay trauma over time
        trauma *= (1.0f - t);
        
        // Calculate shake intensity using trauma
        float shake = trauma * trauma; // Square for more dramatic falloff
        
        // Use noise for more natural random movement
        float noise1 = (SimplexNoise.noise(noiseOffset + t * 10, 0) * 2 - 1);
        float noise2 = (SimplexNoise.noise(noiseOffset + 823.1f + t * 10, 0) * 2 - 1);
        
        pos.x += noise1 * shake * strength;
        pos.y += noise2 * shake * strength;
    }

	/*
	@Override
	public void performOn(Vector2 pos)
	{
		final float val = interpolation.apply( 1.0f - t );
		
		pos.x -= direction.x * val * strength;
		pos.y -= direction.y * val * strength;
	}
		 */
}
