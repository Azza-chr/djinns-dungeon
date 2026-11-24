package tn.designpattern.djinnsdungeon.model.decorator;

public class IceDjinn extends DjinnDecorator {
    public IceDjinn(Weapon wrapped) { super(wrapped); }

    @Override
    public int getDamage() { return wrapped.getDamage() + 3; }

    @Override
    public String getDescription() { return wrapped.getDescription() + " + Glace"; }
}