package com.ducksteam.needleseye.entity.enemies;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.RoomInstance;
import com.ducksteam.needleseye.entity.enemies.ai.MeleeAI;

public class WormEnemy extends EnemyEntity{
    static final float moveSpeed = 80f;

    public static String modelAddress = "models/enemies/untitled.gltf";

    public WormEnemy(Vector3 position, Quaternion rotation, RoomInstance room) {
        super(position, rotation, 8, new ModelInstance((Model) Main.assMan.get(modelAddress)), 5, room.getRoomSpacePos());
        setAi(new MeleeAI(this, moveSpeed));
    }

    @Override
    public int getContactDamage() {
        return 2;
    }

    @Override
    public String getModelAddress() {
        return "models/enemies/worm.gltf";
    }
}
