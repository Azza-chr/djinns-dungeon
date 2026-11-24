package tn.designpattern.djinnsdungeon.model.state;

public abstract class TimedState implements HeroState {
    private int turnsLeft;

    protected TimedState(int duration) {
        this.turnsLeft = duration;
    }

    @Override
    public void onTurnStart(Hero hero) {
        applyEffect(hero);
        turnsLeft--;
        if (turnsLeft <= 0) {
            hero.setState(new NormalState());
        }
    }

    /** Effet propre à l'état, appliqué au début du tour. */
    protected abstract void applyEffect(Hero hero);

    @Override public int modifyOutgoingDamage(int baseDamage) { return baseDamage; }
    @Override public int modifyIncomingDamage(int rawDamage) { return rawDamage; }
    @Override public boolean canAct() { return true; }
}