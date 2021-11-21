package com.vesas.spacefly.particles;

import java.util.Random;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Screen;

public class Trail
{
	private static int MAXSIZE = 16;
	
	public float px[] = new float[ MAXSIZE ];
	public float py[] = new float[ MAXSIZE ];
	
	private float rate = 0.016f;
	private float rateCounter = 0.0f;
	
	private float emitterX;
	private float emitterY;
	
	private float scaling = 1.0f / MAXSIZE;
	
	public Trail()
	{
		for( int i = 0; i < MAXSIZE ; i++ )
		{
			px[i] = 0.0f;
			py[i] = 0.0f;
		}
	}
	
	public void setEmitterPos( float x, float y )
	{
		emitterX = x;
		emitterY = y;
	}

	public void tick( float delta )
	{
		rateCounter += delta;
		
		if( rateCounter > rate )
		{
			rateCounter = 0.0f;
			
			px[0] = emitterX;
			py[0] = emitterY;
			
			int count = MAXSIZE;
			
			while( count-- > 1 )
			{
				px[count] = px[count-1];
				py[count] = py[count-1];
			}
		}
	}
	
	public void draw(Screen screen)
	{
		Sprite sprite = G.effects[0];
		sprite.setOriginCenter();
		
		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		sprite.setScale( 0.012f );
		
		
		for( int i = 0; i < MAXSIZE; i++ )
		{
			sprite.setColor(0.42f, 0.42f, 0.52f, scaling * (MAXSIZE-i) * 0.18f );
			sprite.setPosition(px[i] - sprite.getWidth()*0.5f, py[i] - sprite.getHeight()*0.5f);
			sprite.draw(screen.worldBatch);
		}
		
		
	}
	
}
