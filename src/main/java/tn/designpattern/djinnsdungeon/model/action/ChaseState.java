package tn.designpattern.djinnsdungeon.model.action;

/** L'ennemi a repéré le héros et court vers lui. */
public class ChaseState implements EnemyAIState {
    static final double REACH = 85, LOSE = 750;

    @Override
    public String getName() { return "Poursuite"; }

    @Override
    public void update(EnemyUnit u, double heroX, double dt) {
        double d = heroX - u.getX();
        u.setFacingRight(d > 0);
        if (Math.abs(d) > LOSE) {
            u.setAi(new PatrolState());
            return;
        }
        if (Math.abs(d) <= REACH) {
            if (u.isReadyToAttack()) u.setAi(new AttackState());
            return;
        }
        u.moveBy(Math.signum(d) * u.getChaseSpeed() * dt);
    }
}