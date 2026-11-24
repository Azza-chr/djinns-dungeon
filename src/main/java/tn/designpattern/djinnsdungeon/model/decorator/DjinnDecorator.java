package tn.designpattern.djinnsdungeon.model.decorator;

public abstract class DjinnDecorator implements Weapon {
    protected final Weapon wrapped;

    protected DjinnDecorator(Weapon wrapped) {
        if (wrapped == null) {
            throw new IllegalArgumentException("L'arme à décorer ne peut pas être null");
        }
        this.wrapped = wrapped;
    }

    @Override
    public int getDamage() {
        return wrapped.getDamage();
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription();
    }
}