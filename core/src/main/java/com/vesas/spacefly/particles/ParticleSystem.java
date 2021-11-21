package com.vesas.spacefly.particles;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ParticleSystem implements Poolable
{
	private static int MAXSIZE = 128;
	
	static private Random rand = new Random( 123 );
	
	public float px[] = new float[ MAXSIZE ];
	public float py[] = new float[ MAXSIZE ];
	
	private float dx[] = new float[ MAXSIZE ];
	private float dy[] = new float[ MAXSIZE ];
	
	public int size = 0;
	
	private float time;
	private float maxtime = 0.95f;
	
	private float speedDecay = 0.0f;
	private float initialSpeedMod = 1.0f;
	
	private static Vector2 tmpVector = new Vector2();
	
	public ParticleSystem()
	{
	}
	
	public void setMaxTime( float maxTime )
	{
		this.maxtime = maxTime;
	}
	
	public ParticleSystem( int size )
	{
		this.size = size;
	}
	
	public boolean isFinished()
	{
		return time < 0.0;
	}
	
	public float getTime()
	{
		return time;
	}
	
	public void startAt( float x, float y, float _dx, float _dy, float distribution, float Drandomness )
	{
//		size = MAXSIZE;
		time = maxtime;
		
		for( int i = 0; i < size; i++ )
		{
			float angle = (float) (rand.nextFloat() * Math.PI * 2.0f); 
			Vector2 dir = ParticleSystem.tmpVector;
			dir.x = (float) Math.cos(angle);
			dir.y = (float) Math.sin(angle);
			
			px[i] = x + dir.x * distribution;
			py[i] = y + dir.y * distribution;
			
			dir.scl( rand.nextFloat() * Drandomness);
			
			dx[i] = (dir.x + _dx * 0.1f)*initialSpeedMod;
			dy[i] = (dir.y + _dy * 0.1f)*initialSpeedMod;
			
		}
	}
	
	public void tick( float delta )
	{
		for( int i = 0; i < size; i++ )
		{
			px[i] += (dx[i] * delta);
			py[i] += (dy[i] * delta);
			
			if( dx[i] > 0.0f )
				dx[i] = dx[i] - speedDecay;
			
			if( dy[i] > 0.0f )
				dy[i] = dy[i] - speedDecay;
			 
		}
		
		time = time - delta;
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
