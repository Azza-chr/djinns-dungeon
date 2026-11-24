package tn.designpattern.djinnsdungeon.model.theme;

public class SimpleTrap implements Trap {
    private final String name;
    private final int damage;

    public SimpleTrap(String name, int damage) {
        this.name = name;
        this.damage = damage;
    }

    @Override public String getName() { return name; }
    @Override public int getDamage() { return damage; }
}