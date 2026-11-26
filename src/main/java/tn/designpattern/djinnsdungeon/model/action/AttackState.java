package tn.designpattern.djinnsdungeon.model.action;

/** L'ennemi arme son coup, frappe, puis reprend la poursuite. */
public class AttackState implements EnemyAIState {
    static final double WINDUP = 0.45, TOTAL = 0.8;
    private double t = 0;
    private boolean struck = false;

    @Override
    public String getName() { return "Attaque"; }

    public double getProgress() { return Math.min(1, t / TOTAL); }

    @Override
    public void update(EnemyUnit u, double heroX, double dt) {
        t += dt;
        if (!struck && t >= WINDUP) {
            struck = true;
            u.requestStrike();
            u.setCooldown(1.0);
        }
        if (t >= TOTAL) u.setAi(new ChaseState());
    }
}