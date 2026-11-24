package tn.designpattern.djinnsdungeon.model.state;

public class NormalState implements HeroState {
    @Override public String getName() { return "Normal"; }
    @Override public void onTurnStart(Hero hero) { }
    @Override public int modifyOutgoingDamage(int baseDamage) { return baseDamage; }
    @Override public int modifyIncomingDamage(int rawDamage) { return rawDamage; }
    @Override public boolean canAct() { return true; }
}