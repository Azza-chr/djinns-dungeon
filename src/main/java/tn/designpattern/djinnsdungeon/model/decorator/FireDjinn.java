package tn.designpattern.djinnsdungeon.model.decorator;

public class FireDjinn extends DjinnDecorator {
    public FireDjinn(Weapon wrapped) { super(wrapped); }

    @Override
    public int getDamage() { return wrapped.getDamage() + 5; }

    @Override
    public String getDescription() { return wrapped.getDescription() + " + Feu"; }
}