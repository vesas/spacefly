package com.vesas.spacefly.particles;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Screen;
import com.vesas.spacefly.world.procedural.GenSeed;

public class BigExplosion implements ExplosionInterface
{
	private ParticleSystem particles;
	private ParticleSystem smoke;
	private ParticleSystem bits;
	
	private float bloomSize = 1.0f; 
	
	private float startx, starty;
	private float ringSize = 0.1f;
	
	private boolean randomBool;
	
	public BigExplosion()
	{
		particles = new ParticleSystem(18);
		particles.setMaxTime( 0.72f );
		particles.setInitialSpeedMod(1.55f);
		
		smoke = new ParticleSystem(16);
		smoke.setMaxTime(1.35f);
		
		smoke.setInitialSpeedMod(0.8f);
		smoke.setSpeedDecay(0.006f);
		
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
		
		randomBool = GenSeed.random.nextBoolean();
		
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
		sprite.setScale( 0.024f * bloomSize);
		sprite.setColor(0.18f, 0.18f, 0.18f, smoke.getTime() * 0.24f );
		if( ! smoke.isFinished() )
		{
			
			
			int size = smoke.size;
			for( int i = 0; i < size; i++ )
			{
				sprite.setPosition(smoke.px[i] - sprite.getWidth()*0.5f, smoke.py[i] - sprite.getHeight()*0.5f);
				sprite.draw(screen.worldBatch);
			}
		}
		
		float trans = 1.0f - (ringSize * 1.8f - 0.1f) ;
		
		if( trans > 1.0f )
			trans = 1.0f;
		
			
		if( trans > 0.0f )
		{
			Sprite whiteDisc = G.effects[2];
			whiteDisc.setOriginCenter();
			
			if( randomBool )
				whiteDisc.setScale(0.02f + 0.0192f * ringSize);
			else
				whiteDisc.setScale(0.01f + 0.0122f * ringSize);
			
			whiteDisc.setColor(0.71f, 0.58f, 0.39926f, trans );
			whiteDisc.setPosition( startx- whiteDisc.getWidth() * 0.5f, starty- whiteDisc.getHeight() * 0.5f );
			whiteDisc.draw( screen.worldBatch );
		}
		
		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		
		
		
		
		sprite.setScale( 0.025f * bloomSize);
		sprite.setColor(0.58f, 0.38f, 0.06f, particles.getTime() );
		
		
		
		if( ! particles.isFinished() )
		{
			exploRing.setPosition( startx - exploRing.getWidth() * 0.5f, starty - exploRing.getHeight() * 0.5f);
			exploRing.setScale( 0.042f * ringSize);
			exploRing.setColor(0.75f, 0.58f, 0.36f, particles.getTime() * 0.415f );
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

