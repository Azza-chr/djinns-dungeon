package tn.designpattern.djinnsdungeon.model.decorator;

public class LifeStealDjinn extends DjinnDecorator {
    public LifeStealDjinn(Weapon wrapped) { super(wrapped); }

    @Override
    public int getDamage() { return wrapped.getDamage() + 2; }

    @Override
    public String getDescription() { return wrapped.getDescription() + " + Vol de vie"; }
}