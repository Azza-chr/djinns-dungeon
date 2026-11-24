package tn.designpattern.djinnsdungeon.model.theme;

public class SimpleDecor implements Decor {
    private final String name;

    public SimpleDecor(String name) {
        this.name = name;
    }

    @Override public String getName() { return name; }
}