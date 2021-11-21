package com.vesas.spacefly.particles;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Screen;

public class Goo implements ExplosionInterface
{
	private ParticleSystem smoke;
	
	private float startx, starty;
	private float t;
	
	public Goo()
	{
		smoke = new ParticleSystem(16);
		smoke.setMaxTime( 5.65f );
		smoke.setInitialSpeedMod(0.2f);
		t = 0.0f;
	}
	
	public void startAt( float x, float y, float _dx, float _dy )
	{
		t = 0.0f;
		startx = x;
		starty = y;
		smoke.startAt( x, y, _dx, _dy, 0.1f, 0.64f );
	}

	@Override
	public void draw(Screen screen)
	{
		if( isFinished() )
		{
			return;
		}
		
		Sprite ring = G.effects[2];
		ring.setOriginCenter();
		ring.setScale( 0.0062f + t * 0.001f );
		
		float trans = 0.4f - t * 0.028f;
				
		if( trans < 0.0f )
			return;
		

		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_BLEND_COLOR);
		ring.setColor(0.11f, 0.11f, 0.11f, trans );
		
		int size = smoke.size;
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
		
		return( trans < 0.0f );
	}

}
