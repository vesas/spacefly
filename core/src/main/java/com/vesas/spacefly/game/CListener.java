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
import com.vesas.spacefly.monster.Monster;
import com.vesas.spacefly.monster.MonsterBullet;
import com.vesas.spacefly.monster.MonsterBullets;
import com.vesas.spacefly.monster.SlurgMonster;
import com.vesas.spacefly.monster.ZipperMonster;
import com.vesas.spacefly.world.AbstractGameWorld;

public class CListener implements ContactListener
{
	
	private void playerBulletHit( Bullet bullet, Monster monster )
	{
		monster.getHit( bullet );
		
		if( monster.isDead() )
		{
			AbstractGameWorld.INSTANCE.removeMonster( monster );
			
			float explosionStrength = 0.20f;
			if( monster instanceof SlurgMonster )
			{
				explosionStrength = 0.15f;
				G.explo1.play( 0.05f );
				
			}
				
			else if( monster instanceof ZipperMonster )
			{
				explosionStrength = 0.08f;
				G.explo1.play( 0.03f );
			}
				
			
			ShakeExplo shake = new ShakeExplo();
			shake.setTimeScale( 10.0f );
			shake.setDirection( bullet.body.getLinearVelocity() );
			shake.setInterpolation( Interpolation.circleIn );
			shake.setStrength( explosionStrength ); 
			CameraPositionState.addEffect(shake);
		}
		else
		{
			// not dead yet
			AbstractGameWorld.INSTANCE.addLittleExplosion( bullet.body.getPosition(), bullet.body.getLinearVelocity(), 0.5f, 1.0f);
			
			G.explo1.play( 0.02f );
			
			ShakeExplo shake = new ShakeExplo();
			shake.setTimeScale( 20.0f );
			shake.setDirection( bullet.body.getLinearVelocity() );
			shake.setInterpolation( Interpolation.circleIn );
			shake.setStrength( 0.06f ); 
			CameraPositionState.addEffect(shake);
		}
		
		PlayerBullets.INSTANCE.preRemove(bullet);
	}
	

	@Override
	public void beginContact(Contact contact)
	{
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
		
		if(o1 instanceof Monster)
		{
			if(o2 instanceof Bullet )
			{
				Bullet b = (Bullet)o2;
				Monster m = (Monster)o1;
				playerBulletHit( b, m);
				
			}
		}
		
		
		
		if(o1 instanceof Bullet )
		{
			if(o2 instanceof Monster)
			{
				Bullet bullet = (Bullet)o1;
				Monster m = (Monster)o2;
				playerBulletHit(bullet, m );
			}
		}
		
		if(o1 instanceof MonsterBullet)
		{
			MonsterBullet b = (MonsterBullet)o1;
			
			if(o2 instanceof Player)
			{
				Player.INSTANCE.getHit( b );
			}
			
			MonsterBullets.INSTANCE.preRemove( b );
			AbstractGameWorld.INSTANCE.addLittleExplosion( b.body.getPosition(), b.body.getLinearVelocity(), 0.2f, 2.5f);
		}
		
		if(o2 instanceof MonsterBullet)
		{
			MonsterBullet b = (MonsterBullet)o2;
				
			if(o1 instanceof Player)
			{
				Player.INSTANCE.getHit( b );
				
			}
			MonsterBullets.INSTANCE.preRemove( b );
			AbstractGameWorld.INSTANCE.addLittleExplosion( b.body.getPosition(), b.body.getLinearVelocity(), 0.5f, 1.0f);
		}
		
		if(o2 instanceof Bullet)
		{
			Bullet b = (Bullet)o2;
			
			AbstractGameWorld.INSTANCE.addLittleExplosion( b.body.getPosition(), b.body.getLinearVelocity(), 0.5f, 2.2f);
			PlayerBullets.INSTANCE.preRemove( b );
			
			G.explo1.play( 0.02f );
			
			ShakeExplo shake = new ShakeExplo();
			shake.setTimeScale( 25f );
			shake.setDirection( b.body.getLinearVelocity() );
			shake.setInterpolation( Interpolation.circleIn  );
			shake.setStrength( 0.06f ); 
			CameraPositionState.addEffect(shake);
		}
		
		if(o1 instanceof Powerup && o2 instanceof Player )
		{
			Powerup p = (Powerup)o1;
		
			if( p.getType() == Powerup.HEAL )
			{
				Player.INSTANCE.healUp();
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
		
		if(o1 instanceof Player && o2 instanceof Powerup )
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

		if(o1 instanceof Player || o2 instanceof Player)
		{
			if( o1 instanceof Player )
			{
				if( o2 instanceof Bullet || o2 instanceof MonsterBullet ) {
					return;
				}
			}
			
			if( o2 instanceof Player )
			{
				if( o1 instanceof Bullet || o1 instanceof MonsterBullet ) {
					return;
				}
			}
			
			
			Player player = Player.INSTANCE;
			
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
		}
	}
	
	@Override
	public void endContact(Contact contact) {
		// Nothing for now
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// Nothing for now
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// Nothing for now
	}

}
