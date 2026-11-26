package tn.designpattern.djinnsdungeon.model.action;

/** L'ennemi marche de long en large autour de son point de départ. */
public class PatrolState implements EnemyAIState {
    private static final double RANGE = 120, SIGHT = 480;
    private int dir = 1;

    @Override
    public String getName() { return "Patrouille"; }

    @Override
    public void update(EnemyUnit u, double heroX, double dt) {
        u.moveBy(dir * u.getPatrolSpeed() * dt);
        if (u.getX() > u.getHomeX() + RANGE) dir = -1;
        if (u.getX() < u.getHomeX() - RANGE) dir = 1;
        u.setFacingRight(dir > 0);
        if (Math.abs(heroX - u.getX()) < SIGHT) u.setAi(new ChaseState());
    }
}