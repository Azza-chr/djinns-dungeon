package tn.designpattern.djinnsdungeon.model.state;

import tn.designpattern.djinnsdungeon.model.decorator.BasicSword;
import tn.designpattern.djinnsdungeon.model.decorator.Weapon;

public class Hero {
    private final int maxHp;
    private int hp;
    private HeroState state = new NormalState();
    private Weapon weapon = new BasicSword();

    public Hero(int maxHp) {
        if (maxHp <= 0) throw new IllegalArgumentException("maxHp doit être > 0");
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public boolean isAlive() { return hp > 0; }
    public HeroState getState() { return state; }
    public Weapon getWeapon() { return weapon; }

    public void setState(HeroState state) { this.state = state; }
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }

    /** Perte de PV directe (poison), sans passer par la défense. */
    public void loseHp(int amount) {
        hp = Math.max(0, hp - amount);
    }

    /** Dégâts subis en combat : l'état peut les modifier. */
    public void takeDamage(int rawDamage) {
        loseHp(state.modifyIncomingDamage(rawDamage));
    }

    /** À appeler au début de chaque tour du héros. */
    public void startTurn() {
        state.onTurnStart(this);
    }

    /** Dégâts que le héros inflige ce tour, 0 s'il ne peut pas agir. */
    public int attackDamage() {
        if (!state.canAct()) return 0;
        return state.modifyOutgoingDamage(weapon.getDamage());
    }
}