package com.vesas.spacefly.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.game.AnimateEntity;
import com.vesas.spacefly.game.Floater;
import com.vesas.spacefly.game.Powerup;
import com.vesas.spacefly.monster.Monster;
import com.vesas.spacefly.monster.ZipperMonster;
import com.vesas.spacefly.particles.BigExplosion;
import com.vesas.spacefly.particles.Explosion;
import com.vesas.spacefly.particles.ExplosionInterface;
import com.vesas.spacefly.particles.Goo;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.ProceduralGameWorld;

public abstract class AbstractGameWorld implements AddMonsterCallback
{
	static public AbstractGameWorld INSTANCE = new ProceduralGameWorld();
	
	protected Array<Monster> monsters = new Array<Monster>(false, 64);
	protected Array<Monster> monstersToBeRemoved = new Array<Monster>(false, 8);
	
	protected Array<AnimateEntity> resources = new Array<AnimateEntity>(false, 32);
	protected Array<AnimateEntity> resourcesToBeRemoved = new Array<AnimateEntity>(false, 8);
	
	protected Array<ExplosionInterface> systems = new Array<ExplosionInterface>(false, 8);
	protected Array<ExplosionInterface> removeSystems = new Array<ExplosionInterface>(false,8);
	
	protected Array<Floater> floaters = new Array<Floater>(false, 8);
	protected Array<Floater> removeFloaters = new Array<Floater>(false, 8);
	
	public void cleanup()
	{
		removeParticleSystems();
		removeMonsters();
		removeSpices();
	}
	
	public Array<Monster> getMonsterList()
	{
		return monsters;
	}
	
	@Override
	public void addMonster( Monster m )
	{
		monsters.add( m );
	}

	public int getMonsterCount()
	{
		return monsters.size;
	}
	
	public void addLittleExplosion(  Vector2 pos, Vector2 vel, float velocityScale, float bloomSize )
	{
		// System.out.println("Adding small explosion");

		Explosion explo = new Explosion();
		explo.setBloomSize( bloomSize );
		explo.startAt(pos.x, pos.y, vel.x * velocityScale, vel.y * velocityScale );
		systems.add( explo );
	}
	
	public void addBigExplosion(  Vector2 pos, Vector2 vel, float velocityScale, float bloomSize )
	{
	
		BigExplosion explo = new BigExplosion();
		explo.setBloomSize( bloomSize );
		explo.startAt(pos.x, pos.y, vel.x * velocityScale, vel.y * velocityScale);
		systems.add( explo );
		
		Goo goo = new Goo();
		goo.startAt( pos.x, pos.y, vel.x * velocityScale, vel.y * velocityScale );
		systems.add( goo );
	}

	public void removeMonster(Monster m)
	{
		final Vector2 pos = m.getBody().getWorldCenter();
		final Vector2 vel = m.getBody().getLinearVelocity(); 
		
		if( m instanceof ZipperMonster)
		{
			addLittleExplosion(pos,vel, 0.25f, 2.0f );
		}
		else
		{
			addBigExplosion(pos,vel, 0.25f, 1.5f );
		}
		
		if (!monstersToBeRemoved.contains(m, true))
			monstersToBeRemoved.add(m);
	}

	public void removeMonsters()
	{
		final int size = monstersToBeRemoved.size;
		
		if( size == 0 )
			return;
		
		for( int i = 0; i < size; i++ )
		{
			Monster m = monstersToBeRemoved.get( i );
			
			m.destroy();

			if (monsters.contains(m, true))
				monsters.removeValue(m, true);
			
			dropPowerup(m.getBody().getWorldCenter());

			m = null;
		}
		
		monstersToBeRemoved.clear();
	}
	
	public void dropPowerup( Vector2 boxPos )
	{
		if( GenSeed.random.nextFloat() > 0.4 )
			return;
//		
		float worldX = boxPos.x;
		float worldY = boxPos.y;
		
		int type = Powerup.AMMO1;

		if( GenSeed.random.nextFloat() > 0.7 )
		{
			type = Powerup.HEAL;
		}
			
		
		Powerup pwr = new Powerup(worldX, worldY, type );
		resources.add(pwr);
		
		
	}
	
	public void addResource( AnimateEntity e )
	{
		resources.add( e );
	}
	
	public Array<AnimateEntity> getSpiceList()
	{
		return resources;
	}
	
	public int getSpiceCount()
	{
		return resources.size;
	}
	
	public void removeSpice( AnimateEntity s)
	{
		if (!resourcesToBeRemoved.contains(s, true))
			resourcesToBeRemoved.add(s);
	}

	public void removeSpices()
	{
		final int size = resourcesToBeRemoved.size;
		
		if( size == 0 )
			return;
		
		for( int i = 0; i < size; i++ )
		{
			AnimateEntity s = resourcesToBeRemoved.get( i );
		
			s.destroy();

			if (resources.contains(s, true))
				resources.removeValue(s, true );

			s = null;
		}

		resourcesToBeRemoved.clear();
	}
	
	public void monsterTick( float delta )
	{
		for( int i = 0, size = monsters.size; i < size; i++ )
		{
			Monster m = monsters.get(i);
			m.tick(delta);
		}
	}
	
	public void particleTick( float delta )
	{
		for (int i = 0, size = systems.size; i < size; i++)
		{
			ExplosionInterface explo = systems.get(i);

			explo.tick(delta);

			if (explo.isFinished())
			{
				removeSystems.add(explo);
			}
		}
	}
	
	public void removeParticleSystems()
	{
		if( removeSystems.size > 0 )
		{
			for( int i = 0, size = removeSystems.size; i < size ; i++ )
			{
				ExplosionInterface p = removeSystems.get( i );
				
				if (systems.contains(p, true))
					systems.removeValue(p, true);
			}
			
			removeSystems.clear();
		}
	}
	
	public abstract void init();
	
	public abstract void draw( GameScreen screen );
	
	public abstract void drawMiniMap( GameScreen screen );
	
	public abstract void tick( GameScreen screen, float delta );
	
}
