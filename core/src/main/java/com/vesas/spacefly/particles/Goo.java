package com.vesas.spacefly.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.game.G;

public class Goo implements ExplosionInterface
{
	private ParticleSystem smoke;
	
	private float t;
	
	public Goo()
	{
		smoke = new ParticleSystem(16);
		smoke.setMaxTime( 5.65f );
		smoke.setInitialSpeedMod(0.1f);
		t = 0.0f;
	}
	
	public void startAt( float x, float y, float dx, float dy )
	{
		t = 0.0f;
		smoke.startAt( x, y, dx, dy, 0.1f, 0.64f );
	}

	@Override
	public void draw(GameScreen screen)
	{
		if( isFinished() )
		{
			return;
		}
		
		float trans = 0.5f - t * 0.032f;
				
		if( trans < 0.0f )
			return;
		
		Sprite ring = G.effects[2];
		ring.setOriginCenter();
		ring.setScale( 0.0152f + t * 0.001f );
		ring.setColor(0.11f, 0.11f, 0.11f, trans );
		
		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		int size = smoke.size;
		Color temp = ring.getColor();
		for( int i = 0; i < size; i++ )
		{
			ring.setPosition(smoke.px[i] - ring.getWidth()*0.5f, smoke.py[i] - ring.getHeight()*0.5f);
			ring.draw(screen.worldBatch);
		}
	}

	@Override
	public void tick(float delta)
	{
		smoke.tick(delta);
		
		t += delta;
		
	}

	@Override
	public boolean isFinished()
	{
		float trans = 0.4f - t * 0.028f;
		
		return trans < 0.0f;
	}

}
