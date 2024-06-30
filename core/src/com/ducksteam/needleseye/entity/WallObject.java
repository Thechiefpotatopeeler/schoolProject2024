package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class WallObject extends WorldObject {

    public WallObject(Vector3 position, Vector2 rotation, Vector3 scale) {
        super(position, rotation, scale);
    }

    @Override
    public String getModelAddress() {
        return "models/rooms/door.g3db";
    }
}
