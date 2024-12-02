package com.vesas.spacefly.particles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.screen.GameScreen;

public class ImpulseParticleSystem implements Poolable
{
	private static int MAXSIZE = 32;
	
	public float px[] = new float[ MAXSIZE ];
	public float py[] = new float[ MAXSIZE ];
	
	private float dx[] = new float[ MAXSIZE ];
	private float dy[] = new float[ MAXSIZE ];
	
	private float life[] = new float[ MAXSIZE ];
	
	private float emitterX;
	private float emitterY;
	
	private float oldEmitterX;
	private float oldEmitterY;
	
	private boolean release = false;
	private Vector2 releaseDir = new Vector2();
	
	private float maxLife = 0.12f;
	
	private float speedDecay = 0.0f;
	private float initialSpeedMod = 1.0f;
	
	private float rate = 0.03f;
	private float rateCounter = 0.0f;
	
	private static Vector2 tmpVector = new Vector2();
	
	public ImpulseParticleSystem()
	{
		for( int i = 0; i < MAXSIZE; i++ )
		{
			life[i] = -1.0f;
		}
	}
	
	public void setMaxLife( float maxLife )
	{
		this.maxLife = maxLife;
	}
	
	public void setEmitterPos( float x, float y )
	{
		oldEmitterX = emitterX;
		oldEmitterY = emitterY;
		
		emitterX = x;
		emitterY = y;
	}
	
	public void setReleaseParticles( boolean val )
	{
		release = val;
	}
	
	public void setReleaseDir( float x, float y )
	{
		releaseDir.x = x + (emitterX-oldEmitterX) * 1.1f;
		releaseDir.y = y + (emitterY-oldEmitterY) * 1.1f;
	}
	
	public void tick( float delta )
	{ 
		rateCounter += delta;
		
		for( int i = 0; i < MAXSIZE; i++ )
		{
			if( life[i] < 0.0f )
				continue;
			
			life[i] -= delta;
			
			px[i] += dx[i] * delta;
			py[i] += dy[i] * delta;
		}
		
		if( rateCounter > rate && release )
		{
    		rateCounter = 0.0f;
    		
			for( int i = 0; i < MAXSIZE; i++ )
			{
				if( life[i] < 0.0f )
				{
					life[i] = maxLife;
					
					dx[i] = releaseDir.x;
					dy[i] = releaseDir.y;
					
					px[i] = emitterX;
					py[i] = emitterY;
					
					break;
				}
			}
		}
		
	}
	
	public void draw(GameScreen screen)
	{
		Sprite sprite = G.effects[0];
		sprite.setOriginCenter();
		sprite.setScale(0.012f, 0.012f);
		
		float maxLifeScale = 1.0f / maxLife;
		
		for( int i = 0; i < MAXSIZE; i++ )
		{
			if( life[i] < 0.0f )
				continue;
			
			sprite.setColor(0.92f, 0.72f, 0.52f, life[i] * maxLifeScale * 0.99f );
			sprite.setPosition(px[i] - sprite.getWidth()*0.5f, py[i] - sprite.getHeight()*0.5f);
			sprite.draw(screen.worldBatch);
		}
		
		
	}
	

	@Override
	public void reset()
	{
		
	}

	public float getSpeedDecay()
	{
		return speedDecay;
	}

	public void setSpeedDecay(float speedDecay)
	{
		this.speedDecay = speedDecay;
	}

	public float getInitialSpeedMod()
	{
		return initialSpeedMod;
	}

	public void setInitialSpeedMod(float initialSpeedMod)
	{
		this.initialSpeedMod = initialSpeedMod;
	}
}
