package com.vesas.spacefly.game.cameraeffects;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public abstract class Shake
{
	protected Interpolation interpolation;
	protected float t;
	protected float timeScale;
	protected Vector2 direction;
	protected float strength = 0.01f;
	
	/*
		timescale is approx how long the effect lasts as the inverse of time. ie. 25 is 1/25th of a second.
	*/
	public void setTimeScale( float timeScale ) {
		this.timeScale = timeScale; 
	}
	
	public void setDirection( Vector2 direction ) {
		this.direction = direction;
	}

	public void setInterpolation( Interpolation interpolation ) { 
		this.interpolation = interpolation;
	}

	public void setStrength( float strength ) {
		this.strength = strength;
	}

	public boolean isDead() {
		return t >= 1.0f;
	}

	public void init() {
		t = 0.0f;
	}

	public abstract void performOn( Vector2 pos );
	
	public void tick( float delta ) {
		t += delta * timeScale ;
	}
}
