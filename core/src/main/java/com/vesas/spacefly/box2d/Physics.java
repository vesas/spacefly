package com.vesas.spacefly.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Physics
{
	public static World world = new World(new Vector2(0, 0), true); 
	
	final static int CATEGORY_PLAYER = 0x0001;  
	final static int CATEGORY_MONSTER = 0x0002; 
	final static int CATEGORY_SCENERY = 0x0004; 
	final static int CATEGORY_PLAYERBULLET = 0x0008;
	final static int CATEGORY_MONSTERBULLET = 0x00016;
	
	final static int GROUP_PLAYER = -1;
	final static int GROUP_MONSTER = -2;
	
	public static Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

	
	public static void safeDestroyBody( Body body )
	{
		final Array<JointEdge> joints = body.getJointList();
		
		for( int i = 0, size = joints.size; i < size; i++ )
		{
			JointEdge edge = joints.get( i );
			world.destroyJoint(edge.joint);
		}
	    
		world.destroyBody( body );
		
		body.setUserData(null);
		body = null;
	}
	
	
}
