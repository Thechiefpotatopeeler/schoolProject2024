package com.ducksteam.needleseye.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ducksteam.needleseye.UpgradeRegistry;
import com.ducksteam.needleseye.entity.RoomInstance;
import com.ducksteam.needleseye.entity.WallObject;
import com.ducksteam.needleseye.entity.pickups.UpgradeEntity;
import com.ducksteam.needleseye.player.Upgrade;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a floor of the dungeon
 * @author SkySourced
 */
public class Level {
    private final ArrayList<RoomInstance> rooms;
    public HashMap<Vector2,WallObject> walls;
    private final int levelNo;


    public Level(int levelNo) {
        this.levelNo = levelNo;
        rooms = new ArrayList<>();
    }

    /**
     * Add a room to the level, & an additional placeholder room if it's a hallway
     * @param room the room to add
     */
    public void addRoom(RoomInstance room) {
        rooms.add(room);
        Gdx.app.debug("MapManager", "Added room "  + room.getRoom().getName() + " ("+ room.getRoom().getType() + ") at " + room.getRoomSpacePos());
        if (room.getRoom().getType() == RoomTemplate.RoomType.HALLWAY) {
            rooms.add(new RoomInstance(MapManager.HALLWAY_PLACEHOLDER, room.getRoomSpacePos().cpy().add(0, 1)));
//            Gdx.app.debug("MapManager", "Added hallway placeholder at " + room.getRoomSpacePos().add(0, 1).rotateDeg(room.getRot()));
        }

        if (room.getRoom().getType() == RoomTemplate.RoomType.TREASURE) {
            Upgrade upgrade = UpgradeRegistry.getRandomUpgrade();
            new UpgradeEntity(room.getPosition().add(0,1.5f,0), upgrade);
            Gdx.app.debug("MapManager", "Added upgrade " + upgrade.getName() + " at " + room.getRoomSpacePos());
        }
    }

    /**
     * Get the rooms in the level
     * @return the rooms in the level
     */
    public ArrayList<RoomInstance> getRooms() {
        return rooms;
    }

    public RoomInstance getRoom(Vector2 pos) {
        for (RoomInstance room : rooms) {
            if (room.getRoomSpacePos().equals(pos)) return room;
        }
        return null;
    }

    /**
     * Get the level number
     * @return the level number
     */
    public int getLevelNo() {
        return levelNo;
    }

    @Override
    public String toString() {
        return "Level{" +
                "rooms=" + rooms +
                ", levelNo=" + levelNo +
                '}';
    }
}
