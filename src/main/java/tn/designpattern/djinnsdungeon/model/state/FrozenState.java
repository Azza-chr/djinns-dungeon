package tn.designpattern.djinnsdungeon.model.state;

public class FrozenState extends TimedState {
    public FrozenState() { super(2); }

    @Override public String getName() { return "Gelé"; }

    @Override
    protected void applyEffect(Hero hero) { }

    @Override public boolean canAct() { return false; }
}