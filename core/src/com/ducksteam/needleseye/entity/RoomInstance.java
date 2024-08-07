package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;
import com.ducksteam.needleseye.entity.enemies.EnemyEntity;
import com.ducksteam.needleseye.map.MapManager;
import com.ducksteam.needleseye.map.RoomTemplate;

import java.util.HashMap;

/**
 * Represents an instance of a room in the world
 * @author SkySourced
 */

public class RoomInstance extends Entity {
    RoomTemplate room;
    Vector2 roomSpacePos;
    int rot;//IN DEGREES

    HashMap<Integer, EnemyEntity> enemies = new HashMap<>();

    public RoomInstance(RoomTemplate room, Vector3 drawPos, Vector2 roomSpacePos, int rot){
        super(drawPos, new Quaternion().setEulerAngles(0, rot, 0), new ModelInstance(room.getModel()));
        this.room = room;
        this.roomSpacePos = roomSpacePos;
        this.rot = rot;
    }

    public RoomInstance(RoomTemplate room, Vector2 roomSpacePos, int rot) {
        super(MapManager.getRoomPos(roomSpacePos).sub(new Vector3(5,0,5)).cpy().add(room.getCentreOffset()), new Quaternion(), (room.getModel() == null) ? null : new ModelInstance(room.getModel()));

        this.room = room;
        this.roomSpacePos = roomSpacePos;
        this.rot = rot;

//        if (room.getType() == RoomTemplate.RoomType.HALLWAY) this.setPosition(getPosition().add(0, 20, 10));

//        if (room.getCollider() == null) return;
//        this.collider = room.getCollider().copy();

//        // move collider to correct position
//        assert collider != null;

//        Vector3 offset = MapManager.getRoomPos(roomSpacePos).sub(new Vector3(10,0,10));
//        Gdx.app.log("Offset for " + roomSpacePos, "Offset: " + offset);
//        collider.move(offset, true);

//        assert collider != null;
//        Vector3 colliderCentreOffset = MapManager.getRoomPos(roomSpacePos.cpy().sub(new Vector2(1,1))).add(room.getCentreOffset().cpy());
//        Gdx.app.debug("RoomInstance", "Moving " + room.getName() + "@" + getRoomSpacePos() + " by " + colliderCentreOffset.cpy());
//        for (IHasCollision c : collider.colliders) {
//            c.setCentre(c.getCentre().add(colliderCentreOffset), true);
//            if (!room.getName().equals("brokenceiling")) continue;
//            Gdx.app.debug(c.getClass().getSimpleName() + " moving " + room.getName() + "@" + getRoomSpacePos(), "From: " + c.getCentre().cpy() + " To: " + c.getCentre().cpy().add(colliderCentreOffset.cpy()));
//        }
    }

    public RoomInstance(RoomTemplate room, Vector2 pos) {
        this(room, pos, 0);
    }

    public RoomTemplate getRoom() {
        return room;
    }

    public Vector2 getRoomSpacePos() {
        return roomSpacePos;
    }

    public boolean isAdjacent(RoomInstance other) {
        return roomSpacePos.dst(other.roomSpacePos) == 1;
    }

    /*public boolean hasCommonDoor(RoomInstance other) {
        return (isAdjacent(other) &&
    }*/

    public int getRot() {
        return rot;
    }

    /**
     * Lock the room (for when the player is inside)
     */
    public void lock(){

    }

    /**
     * Unlock the room (for when the player defeats all enemies)
     */
    public void unlock(){

    }


    public void addEnemy(EnemyEntity enemy) {
        Gdx.app.debug("Population", "Added " + enemy.getClass().getSimpleName() + " to room " + room.getName() + " at " + roomSpacePos);
        enemies.put(enemy.id, enemy);
    }

    public void addEnemy(int id){
        try {
            enemies.put(id, (EnemyEntity) Main.entities.get(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeEnemy(Entity enemy) {
        enemies.remove(enemy.id);
        if (enemies.isEmpty()) unlock();
    }

    public void removeEnemy(int id) {
        enemies.remove(id);
        if (enemies.isEmpty()) unlock();
    }

    public HashMap<Integer, EnemyEntity> getEnemies() {
        return enemies;
    }

    @Override
    public String toString() {
        return "RoomInstance{" +
                "isrenderable=" + isRenderable +
                ",room={type=" + room.getType() +
                ", name=" + room.getName() +
                "}, pos=" + roomSpacePos +
                '}';
    }

    @Override
    public String getModelAddress() {
        return room.getModelPath();
    }
}
