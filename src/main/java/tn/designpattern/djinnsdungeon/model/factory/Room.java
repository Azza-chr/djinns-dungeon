package tn.designpattern.djinnsdungeon.model.factory;

import tn.designpattern.djinnsdungeon.model.composite.Combatant;
import tn.designpattern.djinnsdungeon.model.theme.Decor;
import tn.designpattern.djinnsdungeon.model.theme.Trap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Room {
    private final String name;
    private final Decor decor;
    private final Trap trap;              // peut être null
    private final List<Combatant> enemies;

    protected Room(String name, Decor decor, Trap trap, List<Combatant> enemies) {
        this.name = name;
        this.decor = decor;
        this.trap = trap;
        this.enemies = new ArrayList<>(enemies);
    }

    public String getName() { return name; }
    public Decor getDecor() { return decor; }
    public Trap getTrap() { return trap; }
    public List<Combatant> getEnemies() { return Collections.unmodifiableList(enemies); }

    public boolean isCleared() {
        return enemies.stream().noneMatch(Combatant::isAlive);
    }
}