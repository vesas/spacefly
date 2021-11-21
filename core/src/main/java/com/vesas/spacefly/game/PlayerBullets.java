package com.vesas.spacefly.game;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.PooledLinkedList;
import com.badlogic.gdx.utils.TimeUtils;
import com.vesas.spacefly.box2d.Box2DWorld;

public class PlayerBullets extends BaseBullets
{
	
	public static PlayerBullets INSTANCE = new PlayerBullets();
	
	private PlayerBullets() { }
	
	public void fireBullet( float posx, float posy, float dirx, float diry )
	{
		if( deadPool.size > 0 )
		{
    		AbstractBullet bul = deadPool.removeIndex( deadPool.size - 1);
    		if( bul != null )
    		{
    			bul.body.setTransform(posx, posy, 0f);
    			bul.body.setLinearVelocity( dirx, diry );
    			
    			bul.body.setAwake( true );
    			bul.body.setActive( true );
    			
    			bul.resetCreationTime();
    			bullets.add( bul );
    			return;
    		}
		
		}
		
		Bullet b = new Bullet( posx, posy, dirx, diry );
	
		bullets.add( b );
		
	}
	
}
