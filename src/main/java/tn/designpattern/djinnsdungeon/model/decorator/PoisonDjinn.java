package tn.designpattern.djinnsdungeon.model.decorator;

public class PoisonDjinn extends DjinnDecorator {
    public PoisonDjinn(Weapon wrapped) { super(wrapped); }

    @Override
    public int getDamage() { return wrapped.getDamage() + 4; }

    @Override
    public String getDescription() { return wrapped.getDescription() + " + Poison"; }
}