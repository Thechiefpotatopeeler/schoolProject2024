package com.ducksteam.needleseye.entity.pickups;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.collision.Collider;
import com.ducksteam.needleseye.entity.collision.ColliderBox;
import com.ducksteam.needleseye.entity.Entity;
import com.ducksteam.needleseye.player.Upgrade;

/**
 * Entity for an upgrade pickup
 * @author SkySourced
 */
public class UpgradeEntity extends Entity {
    Upgrade upgrade;

    public UpgradeEntity(Vector3 position, Upgrade upgrade) {
        super(position, new Vector2(0,0));
        this.upgrade= upgrade;
        this.collider = new ColliderBox(position, new Vector3(-0.5f, -0.5f,-0.5f), new Vector3(-0.5f, -0.5f,-0.5f));
        //this.setModelAddress(upgrade.getModelAddress());
    }

    public void update(float delta) {
        if (Collider.collidesWith(this.collider, Main.player.collider)) {
            //TODO: Destroy entity
        }
    }

    @Override
    public String getModelAddress() {
        return upgrade.getModelAddress();
    }
}