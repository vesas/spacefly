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
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.MetaRectangleRoom;

class RectangleRoomBuilderTest {
    
    public class TestRectangleRoom extends RectangleRoom {
        @Override
        public void init() {
            // No LibGDX calls
        }
    }

    private BodyBuilder getBodyBuilderMock() {
        BodyBuilder bodyBuilderMock = mock(BodyBuilder.class);

        when(bodyBuilderMock.setPosition(anyFloat(), anyFloat())).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.polygon(any(float[].class))).thenReturn(bodyBuilderMock);
        when(bodyBuilderMock.construct()).thenReturn(null);

        return bodyBuilderMock;
    }

    private Visibility getVisibilityMock() {
        Visibility visibilityMock = mock(Visibility.class);

        doNothing().when(visibilityMock).startConvexArea();
        doNothing().when(visibilityMock).addSegment(anyFloat(), anyFloat(), anyFloat(), anyFloat());
        doNothing().when(visibilityMock).finishConvexArea();

        return visibilityMock;
    }

    @Test
    public void testBuildNoPortals() {

        BodyBuilder bodyBuilderMock = getBodyBuilderMock();
        Visibility visibilityMock = getVisibilityMock();

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

    @Test
    public void testBuildAllPortals() {

        BodyBuilder bodyBuilderMock = getBodyBuilderMock();
        Visibility visibilityMock = getVisibilityMock();

        RectangleRoomBuilder builder = new RectangleRoomBuilder(visibilityMock, bodyBuilderMock) {
            @Override
            protected RectangleRoom createRoom() {
                return new TestRectangleRoom();
            }
        };

        MetaRectangleRoom metaRoom = new MetaRectangleRoom();
        metaRoom.setSize(0, 0, 5, 15);
        metaRoom.addPortal(ExitDir.N, new MetaPortal(1));
        metaRoom.addPortal(ExitDir.E, new MetaPortal(1));
        metaRoom.addPortal(ExitDir.S, new MetaPortal(1));
        metaRoom.addPortal(ExitDir.W, new MetaPortal(1));
        builder.setPos(0, 0);
        RectangleRoom room = builder.buildFrom(metaRoom);

        assertEquals(5, room.getWidth());
        assertEquals(15, room.getHeight());
    }

}
