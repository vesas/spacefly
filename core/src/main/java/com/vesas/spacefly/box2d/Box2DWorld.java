package com.vesas.spacefly.box2d;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Box2DWorld
{
	public static World world = new World(new Vector2(0, 0), true); 
	
	public final static int CATEGORY_PLAYER = 0x0001;  
	public final static int CATEGORY_MONSTER = 0x0002; 
	public final static int CATEGORY_SCENERY = 0x0004; 
	public final static int CATEGORY_PLAYERBULLET = 0x0008;
	public final static int CATEGORY_MONSTERBULLET = 0x00010;
	
	public final static int CATEGORY_FOR_FUTURE1 = 0x00020;
	public final static int CATEGORY_FOR_FUTURE2 = 0x00040;
	public final static int CATEGORY_FOR_FUTURE3 = 0x00080;
	public final static int CATEGORY_FOR_FUTURE4 = 0x00100;
	
	final static int GROUP_PLAYER = -1;
	final static int GROUP_MONSTER = -2;
	
	private static Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

	public static void renderDebug( Matrix4 combinedMatrix )
	{
		debugRenderer.render(Box2DWorld.world, combinedMatrix );
	}
	
	public static void safeDestroyBody( Body body )
	{
		// first remove joints
		final Array<JointEdge> joints = body.getJointList();
		
		for( int i = 0, size = joints.size; i < size; i++ )
		{
			JointEdge edge = joints.get( i );
			world.destroyJoint(edge.joint);
		}
	    
		// then destroy body
		world.destroyBody( body );
		
		// and set references to null
		body.setUserData(null);
		body = null;
	}
	
	
}
