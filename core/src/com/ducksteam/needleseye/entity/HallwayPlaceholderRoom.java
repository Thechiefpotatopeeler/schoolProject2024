package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.map.RoomTemplate;

import static com.ducksteam.needleseye.Main.mapMan;

public class HallwayPlaceholderRoom extends RoomInstance {
    Vector2 associatedRoom;

    public static final Vector2[] PLACEHOLDER_OFFSET = new Vector2[]{
            new Vector2(0, 1),
            new Vector2(1, 0),
            new Vector2(0, -1),
            new Vector2(-1, 0)
    };

    public static final RoomTemplate HALLWAY_PLACEHOLDER_TEMPLATE = new RoomTemplate(RoomTemplate.RoomType.HALLWAY_PLACEHOLDER, 0, 0, false, null, null, new Vector3(0, 0, 0));

    public HallwayPlaceholderRoom(Vector2 position, Vector2 associatedRoom) {
        super(HALLWAY_PLACEHOLDER_TEMPLATE, position);
        this.associatedRoom = associatedRoom;
    }

    public RoomInstance getAssociatedRoom() {
        return mapMan.getCurrentLevel().getRoom(associatedRoom);
    }
}
