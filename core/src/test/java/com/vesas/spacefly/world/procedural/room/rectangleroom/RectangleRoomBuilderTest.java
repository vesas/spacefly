package com.vesas.spacefly.world.procedural.room.rectangleroom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.vesas.spacefly.box2d.BodyBuilder;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;

public class RectangleRoomBuilderTest {
    
    public class TestRectangleRoom extends RectangleRoom {
        @Override
        public void init() {
            // No LibGDX calls
        }
    }

    @Test
    public void testBuildFrom() {

        Visibility visib = new Visibility();
        BodyBuilder bodyBuilderMock = mock(BodyBuilder.class);

        // Chain method returns
        when(bodyBuilderMock.setPosition(anyFloat(), anyFloat())).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.polygon(any(float[].class))).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.construct()).thenReturn(null);  // or mock a Body if needed

        Visibility visibilityMock = mock(Visibility.class);

        doNothing().when(visibilityMock).startConvexArea();
        doNothing().when(visibilityMock).addSegment(anyFloat(), anyFloat(), anyFloat(), anyFloat());
        doNothing().when(visibilityMock).finishConvexArea();

        RectangleRoomBuilder builder = new RectangleRoomBuilder(visibilityMock, bodyBuilderMock) {
            @Override
            protected RectangleRoom createRoom() {
                return new TestRectangleRoom();
            }
        };

        MetaRectangleRoom metaRoom = new MetaRectangleRoom();
        metaRoom.setSize(0, 0, 15, 10);
        builder.setPos(0, 0);
        RectangleRoom room = builder.buildFrom(metaRoom);

        assertEquals(15, room.getWidth());
        assertEquals(10, room.getHeight());
    }

}
