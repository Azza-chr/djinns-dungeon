package tn.designpattern.djinnsdungeon.model.state;

public class EnragedState extends TimedState {
    public EnragedState() { super(2); }

    @Override public String getName() { return "Enragé"; }

    @Override
    protected void applyEffect(Hero hero) { }

    @Override
    public int modifyOutgoingDamage(int baseDamage) { return baseDamage * 3 / 2; }

    @Override
    public int modifyIncomingDamage(int rawDamage) { return rawDamage * 3 / 2; }
}