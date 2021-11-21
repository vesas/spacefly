package com.vesas.spacefly.particles;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Screen;

public class Explosion implements ExplosionInterface
{
	private ParticleSystem particles;
	private ParticleSystem smoke;
	private ParticleSystem bits;
	
	private float bloomSize = 1.0f; 
	
	private float startx, starty;
	private float ringSize = 0.1f;
	
	public Explosion()
	{
		particles = new ParticleSystem(16);
		particles.setMaxTime( 0.65f );
		particles.setInitialSpeedMod(1.2f);
		
		smoke = new ParticleSystem(16);
		smoke.setMaxTime(1.25f);
		
		smoke.setInitialSpeedMod(0.7f);
		smoke.setSpeedDecay(0.005f);
		
		bits = new ParticleSystem(16);
		bits.setMaxTime( 0.8f );
		bits.setInitialSpeedMod(1.1f);
		bits.setSpeedDecay(0.001f);
		
		ringSize = 0.1f;
	}
	
	public void setBloomSize( float bloomSize )
	{
		this.bloomSize = bloomSize; 
	}
	
	public void tick( float delta )
	{
		ringSize += delta;
		particles.tick(delta);
		smoke.tick( delta );
		bits.tick(delta);
	}
	
	public boolean isFinished()
	{
		return particles.isFinished() && smoke.isFinished();
	}
	
	public void startAt( float x, float y, float _dx, float _dy )
	{
		ringSize = 0.1f;
		
		
		startx = x;
		starty = y;
		particles.startAt( x, y, _dx, _dy, 0.1f, 0.64f );
		smoke.startAt( x, y, _dx * 0.5f, _dy * 0.5f, 0.05f, 1.45f );
		bits.startAt( x, y, _dx * 0.5f, _dy * 0.5f, 0.1f, 2.45f );
	}
	
	
	public void draw(Screen screen)
	{
		if( isFinished() )
		{
			return;
		}
		
		Sprite exploRing = G.effects[1];
		exploRing.setOriginCenter();
		
		Sprite sprite = G.effects[0];
		sprite.setOriginCenter();
		
		
		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		sprite.setScale( 0.02f * bloomSize);
		sprite.setColor(0.22f, 0.22f, 0.22f, smoke.getTime() * 0.24f );
		if( ! smoke.isFinished() )
		{
			
			
			int size = smoke.size;
			for( int i = 0; i < size; i++ )
			{
				sprite.setPosition(smoke.px[i] - sprite.getWidth()*0.5f, smoke.py[i] - sprite.getHeight()*0.5f);
				sprite.draw(screen.worldBatch);
			}
		}

		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		sprite.setScale( 0.015f * bloomSize);
		sprite.setColor(0.28f, 0.18f, 0.06f, particles.getTime() );
		

		
		if( ! particles.isFinished() )
		{
			exploRing.setPosition( startx - exploRing.getWidth() * 0.5f, starty - exploRing.getHeight() * 0.5f);
			exploRing.setScale( 0.022f * ringSize);
			exploRing.setColor(0.71f, 0.58f, 0.36f, particles.getTime() * 0.215f );
			exploRing.draw(screen.worldBatch);
			
			int size = particles.size;
			for( int i = 0; i < size; i++ )
			{
				sprite.setPosition(particles.px[i] - sprite.getWidth()*0.5f, particles.py[i] - sprite.getHeight()*0.5f);
				
				sprite.draw(screen.worldBatch);
			}
		}
		
		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		sprite.setScale( 0.0015f * bloomSize);
		float timescale = bits.getTime();
		
		if( timescale > 0.6f )
			timescale = 0.6f;
		sprite.setColor(0.65f, 0.55f, 0.45f, 0.3f + timescale );
		
		if( !bits.isFinished() )
		{
			int size = bits.size;
			for( int i = 0; i < size; i++ )
			{
				sprite.setPosition(bits.px[i] - sprite.getWidth()*0.5f, bits.py[i] - sprite.getHeight()*0.5f);
				
				sprite.draw(screen.worldBatch);
			}
		}
		
		
		
	}
}
