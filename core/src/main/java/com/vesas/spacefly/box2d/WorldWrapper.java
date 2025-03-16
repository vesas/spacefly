package com.vesas.spacefly.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class WorldWrapper {
    
    public World world;

    public void setWorld(World world) {
        this.world = world;
    }

    public WorldWrapper(Vector2 gravity, boolean allowSleep) {
        world = new World(gravity, allowSleep);
    }

    public Body createBody (BodyDef def) {
        return world.createBody(def);
    }

    public void destroyBody (Body body) {
        world.destroyBody(body);
    }

    public void rayCast (RayCastCallback callback, Vector2 point1, Vector2 point2) {
        world.rayCast(callback, point1, point2);
    }

    public void step (float timeStep, int velocityIterations, int positionIterations) {
        world.step(timeStep, velocityIterations, positionIterations);
    }

    public void setContactListener (ContactListener listener) {
        world.setContactListener(listener);
    }

    public void dispose () {
        world.dispose();
    }
}
