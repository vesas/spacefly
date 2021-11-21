package com.vesas.spacefly.game;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.vesas.spacefly.game.cameraeffects.CameraPositionState;
import com.vesas.spacefly.game.cameraeffects.ShakeExplo;
import com.vesas.spacefly.monster.BaseMonster;
import com.vesas.spacefly.monster.MonsterBullet;
import com.vesas.spacefly.monster.MonsterBullets;
import com.vesas.spacefly.monster.SlurgMonster;
import com.vesas.spacefly.monster.ZipperMonster;
import com.vesas.spacefly.world.AbstractGameWorld;

public class CListener implements ContactListener
{
	
	private void playerBulletHit( Bullet bullet, BaseMonster monster )
	{
		monster.getHit( bullet );
		
		if( monster.isDead() )
		{
			AbstractGameWorld.INSTANCE.removeMonster( monster );
			
			float str = 0.020f;
			if( monster instanceof SlurgMonster )
			{

				G.explo1.play( 0.18f );
				str = 0.024f;
			}
				
			else if( monster instanceof ZipperMonster )
			{
				str = 0.008f;
				G.explo1.play( 0.10f );
			}
				
			
			ShakeExplo shake = new ShakeExplo();
			shake.setTimeScale( 12.0f );
			shake.setDirection( bullet.body.getLinearVelocity() );
			shake.setInterpolation( Interpolation.elasticOut );
			shake.setStrength( str ); 
			CameraPositionState.addEffect(shake);
		}
		else
		{
			// not dead yet
			
			AbstractGameWorld.INSTANCE.addLittleExplosion( bullet.body.getPosition(), bullet.body.getLinearVelocity(), 0.5f, 1.0f);
			
			G.explo1.play( 0.05f );
			
			ShakeExplo shake = new ShakeExplo();
			shake.setTimeScale( 25.0f );
			shake.setDirection( bullet.body.getLinearVelocity() );
			shake.setInterpolation( Interpolation.elasticOut );
			shake.setStrength( 0.006f ); 
			CameraPositionState.addEffect(shake);
		}
		
		PlayerBullets.INSTANCE.preRemove(bullet);
	}
	

	@Override
	public void beginContact(Contact contact)
	{
		// TODO Auto-generated method stub
		
		//Object o = contact.getFixtureA().getUserData();
		//Object o2 = contact.getFixtureB().getUserData();
		
		Object o1 = null;
		Object o2 = null;
		
		final Fixture fa = contact.getFixtureA();
		final Fixture fb = contact.getFixtureB();
		
		final Body b1 = fa.getBody();
		final Body b2 = fb.getBody();
		
		if( b1 != null )
		{
			o1 = b1.getUserData();
		}
		
		if( b2 != null )
		{
			o2 = b2.getUserData();
		}
		
		if( (o1 != null && o1 instanceof BaseMonster) )
		{
			if( o2 != null && o2 instanceof Bullet )
			{
				Bullet b = (Bullet)o2;
				BaseMonster m = (BaseMonster)o1;
				playerBulletHit( b, m);
				
			}
		}
		
		
		
		if( o1 != null && o1 instanceof Bullet )
		{
			if( (o2 != null && o2 instanceof BaseMonster) )
			{
				Bullet bullet = (Bullet)o1;
				BaseMonster m = (BaseMonster)o2;
				playerBulletHit(bullet, m );
			}
		}
		
		if( o1 != null && o1 instanceof MonsterBullet )
		{
			MonsterBullet b = (MonsterBullet)o1;
			
			if( (o2 != null && o2 instanceof Player) )
			{
				Player.INSTANCE.getHit( b );
			}
			
			MonsterBullets.INSTANCE.preRemove( b );
			AbstractGameWorld.INSTANCE.addLittleExplosion( b.body.getPosition(), b.body.getLinearVelocity(), 0.2f, 2.5f);
		}
		
		if( o2 != null && o2 instanceof MonsterBullet )
		{
			MonsterBullet b = (MonsterBullet)o2;
				
			if( (o1 != null && o1 instanceof Player) )
			{
				Player.INSTANCE.getHit( b );
				
			}
			MonsterBullets.INSTANCE.preRemove( b );
			AbstractGameWorld.INSTANCE.addLittleExplosion( b.body.getPosition(), b.body.getLinearVelocity(), 0.5f, 1.0f);
		}
		
		if( (o2 != null && o2 instanceof Bullet) )
		{
			Bullet b = (Bullet)o2;
			
			AbstractGameWorld.INSTANCE.addLittleExplosion( b.body.getPosition(), b.body.getLinearVelocity(), 0.5f, 1.0f);
			PlayerBullets.INSTANCE.preRemove( b );
			
			G.explo1.play( 0.05f );
			
			ShakeExplo shake = new ShakeExplo();
			shake.setTimeScale( 25.0f );
			shake.setDirection( b.body.getLinearVelocity() );
			shake.setInterpolation( Interpolation.elasticOut );
			shake.setStrength( 0.006f ); 
			CameraPositionState.addEffect(shake);
			
		}
		
		if( o1 != null && o1 instanceof Player &&
			o2 != null && o2 instanceof Spice )
		{
			Spice s = (Spice)o2;
//			ProceduralGameWorld.INSTANCE.removeSpice( s );
			Player.INSTANCE.addSpice();
		}
		
		if( o1 != null && o1 instanceof Spice && o2 != null && o2 instanceof Player )
		{
			Spice s = (Spice)o1;
//			ProceduralGameWorld.INSTANCE.removeSpice(s);
			
			Player.INSTANCE.addSpice();
		}
		
		
		if( o1 != null && o1 instanceof Powerup && o2 != null && o2 instanceof Player )
		{
			Powerup p = (Powerup)o1;
		
			if( p.getType() == Powerup.HEAL )
			{
				Player.INSTANCE.healUp();
//				ProceduralGameWorld.INSTANCE.removeSpice( p );
			}
			else if( p.getType() == Powerup.AMMO1 )
			{
				Player.INSTANCE.addAmmo();	
			}
			else if( p.getType() == Powerup.ADD_HEALTH )
			{
				Player.INSTANCE.addMaxHealth();
//				ProceduralGameWorld.INSTANCE.removeSpice( p );
			}
		}
		
		if( o1 != null && o1 instanceof Player && o2 != null && o2 instanceof Powerup )
		{
			Powerup p = (Powerup)o2;
		
			if( p.getType() == Powerup.HEAL )
			{
				Player.INSTANCE.healUp();
				AbstractGameWorld.INSTANCE.removeSpice( p );
			}
			else if( p.getType() == Powerup.AMMO1 )
			{
				Player.INSTANCE.addAmmo();
				AbstractGameWorld.INSTANCE.removeSpice( p );
			}
			else if( p.getType() == Powerup.ADD_HEALTH )
			{
				Player.INSTANCE.addMaxHealth();
				AbstractGameWorld.INSTANCE.removeSpice( p );
			}
		}

		if( (o1 != null && o1 instanceof Player) || 
			(o2 != null && o2 instanceof Player)
				)
		{
			if( o1 instanceof Player )
			{
				if( o2 instanceof Bullet || o2 instanceof MonsterBullet )
					return;
			}
			
			if( o2 instanceof Player )
			{
				if( o1 instanceof Bullet || o1 instanceof MonsterBullet )
					return;
			}
			
			
			Player player = Player.INSTANCE;
			
			boolean touching = contact.isTouching();
			boolean enabled = contact.isEnabled();
			
			Vector2 dir = player.body.getLinearVelocity();
			
			Vector2 normal = contact.getWorldManifold().getNormal();
			Vector2 normNormal = new Vector2( normal );
			normNormal.nor();
			
			
			float dot = dir.dot( normNormal );
			
			Vector2 temp = new Vector2(normNormal);
			temp.scl( 2.0f );
			temp.scl( dot );
			
			temp.scl( -1.0f );
			temp.add( dir );
			temp.scl( 0.95f );
			
//			player.body.setLinearVelocity( temp );
			
			int contactCount = contact.getWorldManifold().getNumberOfContactPoints();
			
			int qwe = 0;
			//player.dx = temp2.x * 5.0f;
			//player.dy = temp2.y  * 5.0f;
			
			//player.thrust = 0.0f;
			
			
			//player.dx = player.body.getLinearVelocity().x * 0.15f;
			//player.dy = player.body.getLinearVelocity().y * 0.15f;
			
			//player.dirx = player.body.getLinearVelocity().x * 0.05f;
			//player.diry = player.body.getLinearVelocity().y * 0.05f;
			
//			Vector2 contactPoint = contact.getWorldManifold().getPoints()[0];
			
		
		}

	}
	
	
	@Override
	public void endContact(Contact contact)
	{
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold)
	{
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse)
	{
	}

}
