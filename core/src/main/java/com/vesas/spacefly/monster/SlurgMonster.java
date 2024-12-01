package com.vesas.spacefly.monster;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.AnimateEntity;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.G.MonsterType;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.RayCastClosestCB;
import com.vesas.spacefly.game.Util;

public class SlurgMonster extends Monster
{
	private float cooldown = 0 + random.nextFloat();
	
	private static Vector2 tmpVector = new Vector2();
	private static Vector2 tmpVector2 = new Vector2();

	private float distanceToBlock = 100.0f;
	static private Vector2 blockNormal = new Vector2();

	private Vector2 targetDir = new Vector2(0.0f, 1.0f);
	private Vector2 dir = new Vector2(0.0f, 1.0f);
	
	private float fireCooldown = 0;
	
	private boolean searchForSpice = false;

	private int debug_tx = 0;
	private int debug_ty = 0;

	private int debug_sx = 0;
	private int debug_sy = 0;
	
	float halo = 0.0f;

	public SlurgMonster(float posx, float posy)
	{
		cooldown = 0 + random.nextFloat() * 0.01f;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(posx, posy);

		body = Box2DWorld.world.createBody(bodyDef);

		CircleShape shape = new CircleShape();
		shape.setRadius(0.43f);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 2.25f;
		fixtureDef.friction = 0.000001f;
		fixtureDef.restitution = 0.75f; // Make it bounce a little bit
		// fixtureDef.filter.groupIndex = Physics.GROUP_MONSTER;

		fixtureDef.filter.categoryBits = 16; // 1 wall, 2 player, 4
											 // playerbullet, 8 monsterbullet,
											 // 16 monster
		fixtureDef.filter.maskBits = 23;

		body.createFixture(fixtureDef);

		body.setAwake(true);
		body.setActive(true);

		body.setLinearDamping(0.30f);
		body.setAngularDamping(1.90f);

		// body.setTransform(body.getPosition().x, body.getPosition().y, 90.0f *
		// DEGREES_TO_RADIANS);

		body.setUserData(this);

		targetDir.setAngleDeg(body.getAngle() * Util.RADTODEG);
		dir.setAngleDeg(body.getAngle() * Util.RADTODEG);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		shape.dispose();

		setHealth(4);

	}

	@Override
	public void tick( float delta )
	{
		halo += delta * 2.0f;
		
		if( halo > 2.0f ) {
			halo = 0.0f;
		}
		
		brain.tick( delta );

		if (body == null) {
			return;
		}
		
		fireCooldown -= delta;

		cooldown -= delta;

		if (cooldown <= 0.0f)
		{
			//cooldown = 0.0015f;
			cooldown = 0.10f + cooldown;
			
			if (this.brain.canHearPlayer || this.brain.canSeePlayer)
			{
				Vector2 playerPos = Player.INSTANCE.getWorldCenter();
				Vector2 pos = body.getWorldCenter();
				Vector2 veloc = Player.INSTANCE.body.getLinearVelocity();

				Vector2 tmp = SlurgMonster.tmpVector;
				tmp.x = playerPos.x + veloc.x * 0.15f - pos.x;
				tmp.y = playerPos.y + veloc.y * 0.15f - pos.y;
				tmp.nor();

				targetDir.x = tmp.x;
				targetDir.y = tmp.y;
			}

			
			// Vector2 vel = body.getLinearVelocity();
			/*
			 * if( vel.len() > MAX_VELOCITY ) { vel.nor(); vel.mul( MAX_VELOCITY
			 * ); body.setLinearVelocity( vel ); }
			 */

			if (random.nextInt(100) < 50)
			{
				dir.setAngleDeg(body.getAngle() * Util.RADTODEG);
				dir.nor();
				dir.scl(random.nextFloat() * 358.45f * delta );

				body.applyForceToCenter(dir, true);

			}

			if (random.nextInt(100) < 30)
			{
				makeRayCast();
			}
			
			if(random.nextInt(100) < 18 && brain.canSeePlayer && fireCooldown <= 0)
			{
				fireBullet();
				fireCooldown = 0.08f;
			}

			if (random.nextInt(100) < 60)
			{
				turnToTarget( delta );
			}

			if (random.nextInt(100) < 20 && !brain.canSeePlayer && !brain.canHearPlayer)
			{
				moveDirToRandom();
				
			}

			if (random.nextInt(100) < 20 && !searchForSpice)
			{
				startSpiceSearch();
			}

		}

	}

	private void startSpiceSearch()
	{
		// Mover mover = new Mover();

		AnimateEntity closestSpice = findClosestSpice();

		if (closestSpice == null) {
			return;
		}

		Vector2 temp = body.getWorldCenter();
		Vector2 temp2 = closestSpice.getBody().getWorldCenter();

		int sx = (int) ((temp.x) / 32.0f);
		int sy = (int) ((temp.y) / 32.0f);

		int tx = (int) ((temp2.x) / 32.0f);
		int ty = (int) ((temp2.y) / 32.0f);

		this.debug_sx = sx;
		this.debug_sy = sy;
		this.debug_tx = tx;
		this.debug_ty = ty;


	}

	private AnimateEntity findClosestSpice()
	{

		Array<AnimateEntity> spices = new Array<AnimateEntity>();//  ProceduralGameWorld.INSTANCE.getSpiceList();

		float closestDist = 50000.0f;

		AnimateEntity closestSpice = null;
		Vector2 thisPos = body.getWorldCenter();

		for( int i = 0, size = spices.size; i < size; i++ )
		{
			AnimateEntity e = spices.get( i );
		
			Body b = e.getBody();

			float dist = b.getWorldCenter().dst2(thisPos);

			if (dist < closestDist && dist < 64.0f)
			{
				closestDist = dist;
				closestSpice = e;
			}

		}

		return closestSpice;
	}

	private Monster findClosestBigMonster()
	{
		Array<Monster> monsters = new Array<Monster>();// ProceduralGameWorld.INSTANCE.getMonsterList();

		float closestDist = 50000.0f;

		Monster closestBigMonster = null;
		Vector2 thisPos = body.getWorldCenter();

		for( int i = 0, size = monsters.size; i < size; i++ )
		{
			Monster e = monsters.get( i );
		
			if (e instanceof ShellMonster)
			{
				Body b = e.getBody();

				float dist = b.getWorldCenter().dst2(thisPos);

				if (dist < closestDist && dist < 25.0f)
				{
					closestDist = dist;
					closestBigMonster = e;
				}
			} else {
				continue;
			}

		}

		return closestBigMonster;
	}

	private void makeRayCast()
	{
		Vector2 pos = body.getPosition();

		Vector2 feeler = SlurgMonster.tmpVector;

		feeler.x = pos.x;
		feeler.y = pos.y;

		feeler.x += targetDir.x * 2.5f;
		feeler.y += targetDir.y * 2.5f;

		Vector2 diff = SlurgMonster.tmpVector2;
		diff.x = feeler.x - pos.x;
		diff.y = feeler.y - pos.y;

		RayCastClosestCB cb = new RayCastClosestCB(body.getWorldCenter());

		if (diff.len2() > 0.0f) {
			Box2DWorld.world.rayCast(cb, body.getPosition(), feeler);
		}

		distanceToBlock = cb.closest;

		blockNormal.x = cb.blockNormal.x;
		blockNormal.y = cb.blockNormal.y;

		if (distanceToBlock < 0.3f)
		{
			float adjust = 0.0f;
			float angle = targetDir.angleDeg();

			if (angle < 0) {
				adjust = 180.0f;
			}
			else {
				adjust = -180f;
			}

			targetDir.rotateDeg(adjust);

		} else if (distanceToBlock < 0.8f)
		{
			final float scalingFactor = 120.0f;
			targetDir.rotateDeg((random.nextFloat() - 0.5f) * scalingFactor);
		}

	}


	private void moveDirToRandom()
	{
		// if( distanceToBlock < 0.3f )
		// {
		// targetDir.x = -targetDir.x;
		// targetDir.y = -targetDir.y;
		// return;
		// }

		// if( distanceToBlock < 1.5f )
		// {
		// targetDir.x = targetDir.x + blockNormal.x;
		// targetDir.y = targetDir.y + blockNormal.y;
		// targetDir.nor();
		// }

//		final float scalingFactor = 55.0f;
//		targetDir.rotate((random.nextFloat() - 0.5f) * scalingFactor);

	}

	private void turnToTarget( float delta )
	{
		float bodyAngle = body.getAngle() * Util.RADTODEG;
		float nextAngle = (float) (bodyAngle + body.getAngularVelocity() / 60.0f);
		float targetAngle = targetDir.angleDeg();
		
		float diff = Util.angleDiff(targetAngle, nextAngle);
		
		float absDiff = Math.abs(diff);
		
		if( absDiff < 0.1f ) {
			return;
		}

		float desiredAngularVelocity = diff * 60.0f;
		float inertia = body.getInertia();
		float torque = (float) (inertia * desiredAngularVelocity / (1.0f/60.0f));
		body.applyTorque( torque * 0.0005f, true );

		/*
		float scaling = 24.50f * delta;

		float absDiff = Math.abs(diff);

		if (absDiff < 140.0)
			scaling = scaling * 1.2f;

		if (absDiff < 65.0)
			scaling = scaling * 0.8f;

		if (absDiff < 35.0)
			scaling = scaling * 0.6f;

		if (absDiff < 15.0)
			scaling = scaling * 0.4f;

		if (absDiff < 5.0)
			scaling = scaling * 0.3f;

		if (diff < 0)
		{
			body.applyTorque(-scaling, true );
		} else
		{
			body.applyTorque(scaling, true );
		}
		*/
		
	}
	

	private void fireBullet()
	{
		fireBulletAtPlayer(0.42f, 8.2f, 1);
	}
	
	@Override
	protected void fireBulletAtPlayer( float scatter, float speed, int type )
	{
		Vector2 pos = body.getWorldCenter();
		
		float angle = body.getAngle();
		Vector2 temp = SlurgMonster.tmpVector;
		temp.nor();
		temp.setAngleDeg(angle * Util.RADTODEG );
		
		Vector2 temp2 = SlurgMonster.tmpVector2;
		temp2.x = temp.x;
		temp2.y = temp.y;
		temp2.scl(0.1f );
		
		MonsterBullets.INSTANCE.fireBullet(pos.x + temp2.x, pos.y + temp2.y , 
				temp.x * speed, temp.y * speed, type );
		
		//G.shot.play( 0.04f );
		
	}

	@Override
	public void draw(GameScreen screen)
	{
		// TODO Auto-generated method stub

		// sprite.setPosition(camera.position.x+384, camera.position.y+284);

		if (body == null) {
			return;
		}

		final Vector2 pos = body.getPosition();

		if (screen.outSideScreen(pos, 16)) {
			return;
		}

//		pos.scl(Physics.BOX_TO_WORLD);

		final Sprite sprite = G.monsters.get(MonsterType.BASIC);
		final Sprite haloSprite = G.monsters.get(MonsterType.BASIC_HALO);
		
		sprite.setOriginCenter();
		sprite.setSize(0.84f, 0.84f);
		sprite.setPosition(pos.x - sprite.getWidth()*0.5f, pos.y - sprite.getHeight()*0.5f);
		
		float angle = body.getAngle();
		
		haloSprite.setOriginCenter();
		
		float val = 0.0f;
		if( halo > 1.0f ) {
			val = Interpolation.pow3Out.apply( 2.0f- halo);
		}
		else {
			val = Interpolation.pow3In.apply(halo);
		}
		
		if( brain.canSeePlayer ) {
			haloSprite.setColor( 0.6f + val * 0.4f, 0.1f + val * 0.4f, 0.1f + val * 0.4f,0.2f + val * 0.8f );
		}
		else {
			haloSprite.setColor( 0.0f + val * 0.6f, 0.1f + val * 0.6f, 0.5f + val * 0.5f,0.2f + val * 0.8f );
		}

		haloSprite.setSize(0.92f + val * 0.08f, 0.92f + val * 0.08f);
		haloSprite.setPosition(pos.x - haloSprite.getWidth()*0.5f, pos.y - haloSprite.getHeight()*0.5f);
		haloSprite.setRotation(angle * Util.RADTODEG - 90.0f);

		sprite.setRotation(angle * Util.RADTODEG - 90.0f);

		sprite.draw(screen.worldBatch);
		
		haloSprite.draw(screen.worldBatch);

		//G.font.draw(screen.batch, healthString, sprite.getX() + 26, sprite.getY() + 32);
		
		G.font.setColor( 0.7f, 0.7f, 0.7f, 0.7f );

		debugDraw();

	}

	private void debugDraw()
	{
		if(true) {
			return;
		}
		
		targetDir.nor();
		G.shapeRenderer.begin(ShapeType.Line);
		G.shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 0.1f);
		Vector2 pos = body.getWorldCenter();
		G.shapeRenderer.line(pos.x, pos.y,

		pos.x + targetDir.x * 30.0f, pos.y + targetDir.y * 30.0f);
		G.shapeRenderer.end();
		
		if (debug_sx == 0)
			return;

		G.shapeRenderer.begin(ShapeType.Line);
		G.shapeRenderer.setColor(0.5f, 0.8f, 0.6f, 0.1f);
		G.shapeRenderer.circle(debug_sx * 32 + 16, debug_sy * 32 + 16, 5.0f);

		G.shapeRenderer.setColor(0.9f, 0.8f, 0.6f, 0.1f);
		G.shapeRenderer.circle(debug_tx * 32 + 16, debug_ty * 32 + 16, 5.0f);

		G.shapeRenderer.end();

	}

}
