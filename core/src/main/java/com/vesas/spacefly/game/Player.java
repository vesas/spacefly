package com.vesas.spacefly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.TimeUtils;
import com.vesas.spacefly.DebugHelper;
import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.monster.MonsterBullet;
import com.vesas.spacefly.particles.ImpulseParticleSystem;
import com.vesas.spacefly.particles.Trail;
import com.vesas.spacefly.world.procedural.GenSeed;

public final class Player
{
	private Sprite sprite;
	
	private float bulletFireCooldown = 0;

	private static float THRUST_AMOUNT = 4.86f;
	private static float MAX_VELOCITY = 5.39f;
	
	public static Player INSTANCE = new Player();
	
	private static Vector2 tempVector = new Vector2();
	private static Vector2 tempVector2 = new Vector2();
	
	public Body body;
	
	private int health = 120;
	private int maxHealth = 120;
	
	private int ammo = 120;
	private int maxAmmo = 120;
	
	private float muzzleflash = -0.1f;
	private float maxmuzzleflash = 0.03f;
	
	private static Vector2 bulletDirectionVector = new Vector2();
	
	private long shotTime = 0;
	
	private ImpulseParticleSystem particles = new ImpulseParticleSystem();
	private Trail trail = new Trail();

	private Player()
	{
	}

	public int getHealth()
	{
		return health;
	}
	
	public int getHealthMax()
	{
		return maxHealth;
	}
	
	public int getAmmo()
	{
		return ammo;
	}
	
	public int getAmmoMax()
	{
		return maxAmmo;
	}
	
	public void addAmmo()
	{
		int amount = 30;
		
		if( GenSeed.random.nextBoolean() ) {
			amount += 10;
		}

		ammo = Math.min(ammo + amount, maxAmmo);
	}

	public void init(float x, float y)
	{
		sprite = G.getAtlas().createSprite("ship2");
		sprite.setSize(0.5f,0.5f);
		sprite.setOriginCenter();
//		sprite.setSize(1.5f, 1.5f);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		
		bodyDef.position.set(x, y);

		body = Box2DWorld.world.createBody(bodyDef);

		body.setAwake(true);
		body.setActive(true);

		body.setLinearDamping(0.6f);
		body.setAngularDamping(4.95f);
		body.setFixedRotation(false);

		//CircleShape circle = new CircleShape();o
		//circle.setRadius(16f * Physics.WORLD_TO_BOX);
		
		PolygonShape polyShape = new PolygonShape();
		
		float []v = new float[6];
		
		v[0] = 0;
		v[1] = 0.28f;
		
		v[2] = -0.20f;
		v[3] = -0.23f;
		
		v[4] = 0.20f;
		v[5] = -0.23f;
		
		polyShape.set(v);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polyShape;
		fixtureDef.density = 3.175f;
		fixtureDef.friction = 0.0001f;
		fixtureDef.restitution = 0.05f; // Make it bounce a little bit
		//fixtureDef.filter.groupIndex = Physics.GROUP_PLAYER;
		
		fixtureDef.filter.categoryBits = 2; // 1 wall, 2 player, 4 playerbullet, 8 monsterbullet, 16 monster
		fixtureDef.filter.maskBits = 25;
//		fixtureDef.isSensor = true;

		// Create our fixture and attach it to the body
		body.createFixture(fixtureDef);
		
		body.setUserData(this);

		// Remember to dispose of any shapes after you're done with them!
		// BodyDef and FixtureDef don't need disposing, but shapes do.
		polyShape.dispose();
	}
	
	public boolean recentlyShot()
	{
		long currentTime = TimeUtils.millis();
		return (currentTime - shotTime) < 7000;
	}
	
	public void addMaxHealth()
	{
		maxHealth++;
	}
	
	public void healUp()
	{
		int healUp = 1 + GenSeed.random.nextInt( 4 ); 
		
		health = Math.min(health + healUp, maxHealth);
	}
	
	public void getHit( MonsterBullet b )
	{
		if( health <= 0 ) {
			return;
		}
			
		health--;
		
		if( GenSeed.random.nextBoolean() ) {
			health--;
		}
	}
	
	public Vector2 getPosition()
	{
		return body.getPosition();
	}
	
	public Vector2 getWorldCenter()
	{
		return body.getWorldCenter();
	}
	

	public void fireBullet(GameScreen screen, float floatDelta,PlayerBullets bullets)
	{
		if( bulletFireCooldown > 0 )
		{
			return;
		}
		
		// no ammo, can't shoot
		if( ammo <= 0 ) {
			return;
		}

		ammo--;
		
		muzzleflash = maxmuzzleflash;
		
		bulletDirectionVector.x = 1.0f;
		bulletDirectionVector.y = 0.0f;
		bulletDirectionVector.setAngleRad( body.getAngle() );
		bulletDirectionVector.rotate90(0); // rotate 90 cw
		
		// just add a bit of kickback
		body.applyForceToCenter(-bulletDirectionVector.x, -bulletDirectionVector.y, true);
		
		bulletFireCooldown = 0.085f;
		
		// update shotTime
		shotTime = TimeUtils.millis();
		
		final Vector2 bodyPos = body.getWorldCenter();
		
		/*
		bullets.fireBullet(bodyPos.x  + bulletDirectionVector.x * 0.4f, 
				bodyPos.y + bulletDirectionVector.y * 0.4f, 
							bulletDirectionVector.x * 9.6f + (randomNoise1 - 0.5f)*0.2f,
							+ bulletDirectionVector.y * 9.6f + (randomNoise2 - 0.5f)*0.2f);
		*/
		bullets.fireBullet(bodyPos.x  + bulletDirectionVector.x * 0.4f, 
				bodyPos.y + bulletDirectionVector.y * 0.4f, 
							bulletDirectionVector.x * 11.6f,
							bulletDirectionVector.y * 11.6f);

		G.shot.play( 0.01f );
	}

	
	public void tick( GameScreen screen, float floatDelta )
	{
		// body.applyForceToCenter(0.0f, -0.2f, true);

		final boolean firstFingerTouching = Gdx.input.isTouched(0);
		
		final boolean wpressed =  Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP);
		final boolean apressed =  Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
		final boolean spressed =  Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN);
		final boolean dpressed =  Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);
		
		if( wpressed ) {
			this.moveup( floatDelta );
			// this.thrust(floatDelta);
		}
			
		
		if( apressed ) {
			this.moveleft( floatDelta );
			// this.rotateLeft(floatDelta);
		}
			
		
		if( spressed ) {
			this.movedown( floatDelta );
		}
			
		
		if( dpressed ) {
			this.moveright( floatDelta );
			// this.rotateRight(floatDelta);
		}
			
		
		if( firstFingerTouching )
		{
			Player.INSTANCE.fireBullet(screen, floatDelta,PlayerBullets.INSTANCE);
						
		}
		
		bulletFireCooldown -= floatDelta;
		
		updateGunAngle( screen );
		
		if( muzzleflash > 0.0f ) {
			muzzleflash -= floatDelta;
		}
		
		Vector2 pos = body.getPosition();
		float angle = body.getAngle();
		trail.setEmitterPos( pos.x, pos.y );

		final boolean anyPressed = wpressed | apressed | spressed | dpressed;
		
		particles.setEmitterPos( pos.x, pos.y );
		particles.setReleaseParticles( anyPressed );
		if( anyPressed )
		{
			float xdir = 0;
			float ydir = 0;

			if( apressed ) {
				xdir = 0.045f;
			}

			if( dpressed ) {
				xdir = -0.045f;
			}
			
			if( wpressed ) {
				ydir = -0.045f;
			}
			
			if( spressed ) {
				ydir = 0.045f;
			}
			
			particles.setReleaseDir( xdir, ydir );
		}
		
		particles.tick( floatDelta );
		trail.tick( floatDelta );
		

		screen.updatePosition( pos, floatDelta, angle );
		
	}
	
	private void updateGunAngle( GameScreen screen )
	{
		// if(true)
			// return;

		int mousex = Gdx.input.getX();
		int mousey = Gdx.input.getY();
		
		Vector2 bodyPos = body.getWorldCenter();
		tempVector.x = bodyPos.x;
		tempVector.y = bodyPos.y;// - 0.31f;
		screen.viewport.project(tempVector);

		int directionx = mousex - (int) (tempVector.x);
		int directiony = (int) (tempVector.y) - mousey;
		
		tempVector2.x = directionx;
		tempVector2.y = -directiony;
		tempVector2.nor();
	
		float tempTargetAngle = -tempVector2.angleRad() - 90f * Util.DEGTORAD;
		
		body.setTransform(body.getPosition(), tempTargetAngle);
		/*
		 
		float bodyAngle = body.getAngle() % 360.0f;
		float nextAngle = bodyAngle + body.getAngularVelocity() / 5.0f;
		float totalRotation = tempTargetAngle - nextAngle;
		
		while ( totalRotation < -180 * Util.DEGTORAD ) totalRotation += 360 * Util.DEGTORAD;
		while ( totalRotation >  180 * Util.DEGTORAD ) totalRotation -= 360 * Util.DEGTORAD;
		
		float desiredAngularVelocity = totalRotation * 290f;
		
		float change = 35f * Util.DEGTORAD; //allow n degree rotation per time step
		desiredAngularVelocity = Math.min( change, Math.max(-change, desiredAngularVelocity));
		
		float impulse = body.getInertia() * desiredAngularVelocity;
		body.applyAngularImpulse( impulse, true );
		
		 */	
	}
	
	public void drawMiniMap( GameScreen screen )
	{
		Vector2 pos = body.getPosition();
		
		G.shapeRenderer.begin(ShapeType.Filled);
		G.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		G.shapeRenderer.circle( pos.x, pos.y, 1.0f, 8);
		G.shapeRenderer.end();
		
	}
	
	private void drawTracers(GameScreen screen)
	{
		trail.draw( screen );
		particles.draw( screen );
	}

	public void draw(GameScreen screen)
	{
		
		drawTracers( screen );
		
		int mousex = Gdx.input.getX();
		int mousey = Gdx.input.getY();
		Vector2 bodyPos = body.getWorldCenter();
		tempVector.x = bodyPos.x;
		tempVector.y = bodyPos.y;// - 0.31f;
		screen.viewport.project(tempVector);

		Vector2 pos = body.getPosition();

		
		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		/*
		Sprite whiteDisc = G.effects[2];
		whiteDisc.setColor( 0.001f, 0.001f, 0.001f, 0.1f);
		whiteDisc.setOriginCenter();
		whiteDisc.setScale(0.005f);
		whiteDisc.setPosition(pos.x - whiteDisc.getWidth() * 0.5f, pos.y - whiteDisc.getHeight() * 0.5f);
		whiteDisc.draw(screen.worldBatch);
		*/

//		body.setTransform(body.getPosition(), ( gunangle - 90) * DEGTORAD );
		
		float bodyAngle = body.getAngle();
		sprite.setRotation(bodyAngle*Util.RADTODEG + 90);
		
		sprite.setPosition(pos.x - 0.25f, pos.y - 0.25f);
		
//		screen.worldBatch.setBlendFunction(GL20.GL_BLEND_SRC_ALPHA, GL20.GL_BLEND_DST_ALPHA);
		
		sprite.draw(screen.worldBatch);
		
		
		
		
		if( muzzleflash > 0.0f )
		{
			Sprite flash = G.effects[0];
			
//			Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
//			screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
			
			bulletDirectionVector.x = 1.0f;
			bulletDirectionVector.y = 0.0f;
			bulletDirectionVector.setAngleRad( body.getAngle() );
			bulletDirectionVector.rotate90(0); // rotate 90 cw
			
			flash.setOriginCenter();
			flash.setScale( 0.05f );
			flash.setColor(0.695f, 0.49515f, 0.39511f, 0.9f);
			sprite.setRotation(bodyAngle*Util.RADTODEG);
			flash.setPosition( pos.x - flash.getWidth() * 0.5f + bulletDirectionVector.x * 0.4f, pos.y - flash.getHeight() * 0.5f + bulletDirectionVector.y * 0.4f );
			flash.draw( screen.worldBatch);
			
//			screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		
		// batch.draw( ship, 384, 284 );
		
//		gunSprite.setPosition(posx - 8, posy - 3);
//		gunSprite.setOrigin( 8 , 4f );
//		gunSprite.setRotation(gunangle-90);
		//gunSprite.draw(screen.batch);
		
		//if( health > 0 )
		//	G.font.draw( screen.batch, healthString, posx + 16.0f, posy + 16.0f);
			
			
		//else
			//G.font.draw( screen.batch, "boom,dead", sprite.getX() + 28.0f, sprite.getY() + 32.0f);

			
		screen.worldBatch.end();
		
		screen.screenBatch.begin();
		//G.font.draw( screen.screenBatch, spiceString, 12.0f, 56.0f);
		
		tempVector.x = mousex;
		tempVector.y = mousey;
		screen.viewport.unproject(tempVector);
		
		if(DebugHelper.PLAYER_DEBUG) 
		{
			G.font.draw( screen.screenBatch, "puremousex: " + mousex, 10.0f, 312.0f);
			G.font.draw( screen.screenBatch, "puremousey: " + mousey, 10.0f, 342.0f);
			
			G.font.draw( screen.screenBatch, "mx in world: " + tempVector.x, 10.0f, 212.0f);
			G.font.draw( screen.screenBatch, "my in world: " + tempVector.y, 10.0f, 242.0f);
			
			G.font.draw( screen.screenBatch, "playerw.x: " + pos.x, 10.0f, 272.0f);
			G.font.draw( screen.screenBatch, "playerw.y: " + pos.y, 10.0f, 292.0f);
			
			float worldWidth = screen.viewport.getWorldWidth();
			float worldHeight = screen.viewport.getWorldHeight();
			
			G.font.draw( screen.screenBatch, "worldWidth: " + worldWidth, 10.0f, 132.0f);
			G.font.draw( screen.screenBatch, "worldHeight: " + worldHeight, 10.0f, 152.0f);
			
			float screenHeight = screen.viewport.getScreenHeight();
			float screenWidth = screen.viewport.getScreenWidth();
			
			G.font.draw( screen.screenBatch, "screenWidth: " + screenWidth, 10.0f, 80.0f);
			G.font.draw( screen.screenBatch, "screenHeight: " + screenHeight, 10.0f, 59.0f);
		
		}
		
		screen.screenBatch.end();
		
		screen.worldBatch.begin();

	}

	private void limitSpeed()
	{
		Vector2 vel = body.getLinearVelocity();

		if (vel.len() > MAX_VELOCITY)
		{
			vel.nor();
			vel.scl(MAX_VELOCITY);
			body.setLinearVelocity(vel);
		}
	}

	public void thrust(float delta)
	{
		bulletDirectionVector.x = 1.0f;
		bulletDirectionVector.y = 0.0f;
		bulletDirectionVector.setAngleRad( body.getAngle() );
		bulletDirectionVector.rotate90(0); // rotate 90 cw

		body.applyForceToCenter(bulletDirectionVector.x*THRUST_AMOUNT,bulletDirectionVector.y*THRUST_AMOUNT, true);
		limitSpeed();
	}

	
	public void rotateLeft(float delta)
	{
		body.applyAngularImpulse(delta * 0.1f, true);
	}

	public void rotateRight(float delta)
	{
		body.applyAngularImpulse(-delta * 0.1f, true);
	}

	public void moveup(float delta)
	{
		body.applyForceToCenter(0.0f, THRUST_AMOUNT, true);

		limitSpeed();
	}

	public void movedown(float delta)
	{
		body.applyForceToCenter(0.0f, -THRUST_AMOUNT, true);

		limitSpeed();
	}

	public void moveleft(float delta)
	{
		body.applyForceToCenter(-THRUST_AMOUNT, 0.0f, true);

		limitSpeed();
	}

	public void moveright(float delta)
	{
		body.applyForceToCenter(THRUST_AMOUNT, 0.0f, true);

		limitSpeed();
	}

}
