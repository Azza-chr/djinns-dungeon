package tn.designpattern.djinnsdungeon.model.action;

import tn.designpattern.djinnsdungeon.model.composite.Combatant;
import tn.designpattern.djinnsdungeon.model.composite.Enemy;
import tn.designpattern.djinnsdungeon.model.factory.Boss;
import tn.designpattern.djinnsdungeon.model.state.FrozenState;
import tn.designpattern.djinnsdungeon.model.state.Hero;
import tn.designpattern.djinnsdungeon.model.state.HeroState;
import tn.designpattern.djinnsdungeon.model.state.NormalState;
import tn.designpattern.djinnsdungeon.model.state.PoisonedState;
import tn.designpattern.djinnsdungeon.model.theme.ThemeFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Un niveau en temps réel : le héros et les ennemis bougent sur une ligne de longueur "length". */
public class LevelWorld {
    public static final double HERO_SPEED = 260, REACH_FRONT = 140, REACH_BACK = 30,
            ENEMY_HIT_RANGE = 95, SWING_TIME = 0.35, HIT_AT = 0.15, ATTACK_COOLDOWN = 0.5;

    private static final Map<String, Supplier<HeroState>> EFFECTS = Map.of(
            "Scorpion", PoisonedState::new,
            "Squelette", FrozenState::new,
            "Djinn maudit", FrozenState::new);

    private final Hero hero;
    private final List<EnemyUnit> enemies = new ArrayList<>();
    private final List<WorldEvent> events = new ArrayList<>();
    private final double length, parTime;
    private double heroX = 150, elapsed, tick, swingTime = -1, cooldown;
    private boolean facingRight = true, moving, swingHit;

    public LevelWorld(Hero hero, List<Enemy> list, double length, double parTime) {
        this.hero = hero;
        this.length = length;
        this.parTime = parTime;
        int n = list.size();
        double span = length - 1000;
        for (int i = 0; i < n; i++) {
            Enemy e = list.get(i);
            double x = n == 1 ? 650 + span / 2 : 650 + i * span / (n - 1);
            double speed = e.getName().equals("Djinn maudit") ? 85 : 120;
            enemies.add(new EnemyUnit(e, x, speed));
        }
    }

    /** Les ennemis viennent de la fabrique du thème (Abstract Factory). */
    public static LevelWorld create(Hero hero, ThemeFactory theme, int count, boolean boss,
                                    double length, double parTime) {
        List<Enemy> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Combatant c = theme.createEnemy();
            if (c instanceof Enemy e) list.add(e);
        }
        if (boss) list.add(new Boss());
        return new LevelWorld(hero, list, length, parTime);
    }

    /** dir : -1 gauche, 0 immobile, 1 droite. */
    public void update(double dt, int dir, boolean attack) {
        if (isOver()) {
            moving = false;
            return;
        }
        elapsed += dt;
        tick += dt;
        if (tick >= 1.0) { // un "tour" par seconde pour les effets d'état (poison...)
            tick -= 1.0;
            int before = hero.getHp();
            hero.startTurn();
            int lost = before - hero.getHp();
            if (lost > 0) events.add(new WorldEvent(WorldEvent.Type.STATE_DAMAGE, heroX, lost, null));
        }
        boolean canAct = hero.isAlive() && hero.getState().canAct();
        moving = canAct && dir != 0;
        if (moving) {
            heroX = Math.max(40, Math.min(length - 40, heroX + dir * HERO_SPEED * dt));
            facingRight = dir > 0;
        }
        cooldown = Math.max(0, cooldown - dt);
        if (swingTime >= 0) {
            swingTime += dt;
            if (!swingHit && swingTime >= HIT_AT) {
                swingHit = true;
                applySwing();
            }
            if (swingTime >= SWING_TIME) swingTime = -1;
        } else if (attack && canAct && cooldown <= 0) {
            swingTime = 0;
            swingHit = false;
            cooldown = ATTACK_COOLDOWN;
            events.add(new WorldEvent(WorldEvent.Type.SWING, heroX, 0, null));
        }
        for (EnemyUnit u : enemies) {
            if (!u.isAlive()) continue;
            u.update(dt, heroX);
            if (u.consumeStrike()) strike(u);
        }
    }

    private void applySwing() {
        int damage = hero.attackDamage(); // arme (Decorator) + état (State)
        if (damage <= 0) return;
        for (EnemyUnit u : enemies) {
            if (!u.isAlive()) continue;
            double dx = u.getX() - heroX;
            boolean inFront = facingRight
                    ? (dx > -REACH_BACK && dx < REACH_FRONT)
                    : (dx < REACH_BACK && dx > -REACH_FRONT);
            if (!inFront) continue;
            u.getEnemy().takeDamage(damage);
            u.moveBy(facingRight ? 25 : -25);
            events.add(new WorldEvent(WorldEvent.Type.ENEMY_HURT, u.getX(), damage, u));
            if (!u.isAlive()) events.add(new WorldEvent(WorldEvent.Type.ENEMY_DIED, u.getX(), 0, u));
        }
    }

    private void strike(EnemyUnit u) {
        if (!hero.isAlive() || Math.abs(u.getX() - heroX) > ENEMY_HIT_RANGE) return;
        int before = hero.getHp();
        hero.takeDamage(u.getAttackPower());
        events.add(new WorldEvent(WorldEvent.Type.HERO_HURT, heroX, before - hero.getHp(), u));
        Supplier<HeroState> effect = EFFECTS.get(u.getName());
        if (effect != null && hero.getState() instanceof NormalState && Math.random() < 0.5) {
            hero.setState(effect.get());
        }
    }

    public List<WorldEvent> drainEvents() {
        List<WorldEvent> out = new ArrayList<>(events);
        events.clear();
        return out;
    }

    public Hero getHero() { return hero; }
    public double getHeroX() { return heroX; }
    public double getLength() { return length; }
    public double getElapsed() { return elapsed; }
    public double getParTime() { return parTime; }
    public boolean isFacingRight() { return facingRight; }
    public boolean isMoving() { return moving; }
    public boolean isSwinging() { return swingTime >= 0; }
    public double getSwingProgress() { return swingTime < 0 ? 0 : Math.min(1, swingTime / SWING_TIME); }
    public List<EnemyUnit> getEnemies() { return Collections.unmodifiableList(enemies); }
    public int aliveCount() { return (int) enemies.stream().filter(EnemyUnit::isAlive).count(); }

    public boolean isDefeat() { return !hero.isAlive(); }
    public boolean isVictory() { return hero.isAlive() && aliveCount() == 0; }
    public boolean isOver() { return isDefeat() || isVictory(); }

    public int getStars() {
        return StarRating.stars(isVictory(), hero.getHp(), hero.getMaxHp(), elapsed, parTime);
    }
}