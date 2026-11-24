package tn.designpattern.djinnsdungeon.model.game;

import tn.designpattern.djinnsdungeon.model.factory.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Dungeon {
    private final List<Room> rooms;
    private int index = 0;

    public Dungeon(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException("Un donjon doit avoir au moins une salle");
        }
        this.rooms = new ArrayList<>(rooms);
    }

    public boolean hasNext() { return index < rooms.size(); }

    public Room nextRoom() {
        if (!hasNext()) throw new NoSuchElementException("Plus de salles");
        return rooms.get(index++);
    }

    public int size() { return rooms.size(); }
}