package tn.designpattern.djinnsdungeon.model.state;

public class PoisonedState extends TimedState {
    public PoisonedState() { super(2); }

    @Override public String getName() { return "Empoisonné"; }

    @Override
    protected void applyEffect(Hero hero) {
        hero.loseHp(5);
    }
}