package com.vesas.spacefly.monster;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.vesas.spacefly.box2d.BodyBuilder;

class SlurgMonsterTest {
 
    public static class TestBody extends Body {

        public TestBody() {
            super(null, 0);
        }

        @Override
        public float getAngle() {
            return 0f;
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
        when(bodyBuilderMock.construct()).thenReturn(new TestBody());

        return bodyBuilderMock;
    }
    
    @Test
    public void test1() {

        BodyBuilder bodyBuilderMock = getBodyBuilderMock();

        SlurgMonster monster = new SlurgMonster(0, 0, bodyBuilderMock);
        
        // TODO: tests
    }
}
