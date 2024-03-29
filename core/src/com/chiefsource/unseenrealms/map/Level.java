package com.chiefsource.unseenrealms.map;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

/**
 * One floor of the dungeon
 */
public class Level {
    private ArrayList<RoomInstance> rooms;
    private final int levelNo;

    public Level(int levelNo) {
        this.levelNo = levelNo;
        rooms = new ArrayList<>();
    }

    /**
     * Add a room to the level
     * @param room the room to add
     */
    public void addRoom(RoomInstance room) {
        rooms.add(room);
        Gdx.app.debug("MapManager", "Added room "  + room.getRoom().getName() + " ("+ room.getRoom().getType() + ") at " + room.getPos());
        if (room.getType() == RoomTemplate.RoomType.HALLWAY) {
            rooms.add(new RoomInstance(MapManager.HALLWAY_PLACEHOLDER, room.getPos().add(0, 1).rotateDeg(room.getRot())));
            Gdx.app.debug("MapManager", "Added hallway placeholder at " + room.getPos().add(0, 1).rotateDeg(room.getRot()));
        }
    }

    /**
     * Get the rooms in the level
     * @return the rooms in the level
     */
    public ArrayList<RoomInstance> getRooms() {
        return rooms;
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
