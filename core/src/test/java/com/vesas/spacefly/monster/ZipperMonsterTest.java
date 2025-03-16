package com.vesas.spacefly.monster;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.box2d.WorldWrapper;
import com.vesas.spacefly.game.Player;

class ZipperMonsterTest {
 
    
    public static class MockBody extends Body {

        public MockBody() {
            super(null, 0);
        }

        @Override
        public float getAngle() {
            return 0f;
        }

        @Override
        public Vector2 getPosition() {
            return new Vector2(0, 0);
        }

        @Override
        public Vector2 getWorldCenter() {
            return new Vector2(0, 0);
        }
    }
    
    private BodyBuilder getBodyBuilderMock() {
        BodyBuilder bodyBuilderMock = mock(BodyBuilder.class);

        when(bodyBuilderMock.setBodyType(any(BodyType.class))).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.setPosition(anyFloat(), anyFloat())).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.circle(anyFloat())).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.setDensity(anyFloat())).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.setFriction(anyFloat())).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.setRestitution(anyFloat())).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.setFilterCategoryBits(any(short.class))).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.setFilterMaskBits(any(short.class))).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.setLinearDamping(anyFloat())).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.setAngularDamping(anyFloat())).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.setUserdata(any(Object.class))).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.construct()).thenReturn(new MockBody());

        return bodyBuilderMock;
    }
    
    @Test
    public void test1() {

        BodyBuilder bodyBuilderMock = getBodyBuilderMock();

        Player player = mock(Player.class);
        when(player.getPosition()).thenReturn(new Vector2(10,0));
        when(player.getWorldCenter()).thenReturn(new Vector2(10,0));
        Player.setInstance(player);

        WorldWrapper worldWrapper = mock(WorldWrapper.class);
        Box2DDebugRenderer debugRenderer = mock(Box2DDebugRenderer.class);
        Box2DWorld.init(worldWrapper, debugRenderer);

        ZipperMonster monster = new ZipperMonster(0, 0, 1, bodyBuilderMock);

        monster.tick(null, 0);
        
        // TODO: tests
    }
}
