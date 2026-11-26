package tn.designpattern.djinnsdungeon.model.action;

import tn.designpattern.djinnsdungeon.model.composite.Enemy;

/** Un ennemi placé dans le niveau : ses PV (Enemy), sa position et son état d'IA. */
public class EnemyUnit {
    private final Enemy enemy;
    private final double homeX, chaseSpeed;
    private double x, cooldown = 0.6;
    private boolean facingRight = false, strike = false;
    private EnemyAIState ai = new PatrolState();

    public EnemyUnit(Enemy enemy, double x, double chaseSpeed) {
        this.enemy = enemy;
        this.x = x;
        this.homeX = x;
        this.chaseSpeed = chaseSpeed;
    }

    public void update(double dt, double heroX) {
        cooldown = Math.max(0, cooldown - dt);
        if (enemy.isAlive()) ai.update(this, heroX, dt);
    }

    public Enemy getEnemy() { return enemy; }
    public String getName() { return enemy.getName(); }
    public int getHp() { return enemy.getHp(); }
    public boolean isAlive() { return enemy.isAlive(); }
    public int getAttackPower() { return enemy.getAttackPower(); }

    public double getX() { return x; }
    public double getHomeX() { return homeX; }
    public void moveBy(double dx) { x += dx; }
    public double getPatrolSpeed() { return 55; }
    public double getChaseSpeed() { return chaseSpeed; }
    public boolean isFacingRight() { return facingRight; }
    public void setFacingRight(boolean f) { facingRight = f; }

    public void setAi(EnemyAIState state) { ai = state; }
    public String getAiName() { return ai.getName(); }
    public double getAttackProgress() {
        return ai instanceof AttackState a ? a.getProgress() : -1;
    }

    public boolean isReadyToAttack() { return cooldown <= 0; }
    public void setCooldown(double c) { cooldown = c; }
    public void requestStrike() { strike = true; }
    public boolean consumeStrike() {
        boolean s = strike;
        strike = false;
        return s;
    }
}