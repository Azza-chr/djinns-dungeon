package tn.designpattern.djinnsdungeon.model.composite;

/**
 * Pattern COMPOSITE - la "feuille" : un ennemi seul.
 * Les ennemis concrets (Scorpion, Guard, Skeleton...) heritent de cette classe
 * a l'etape 4 (Abstract Factory).
 */
public abstract class Enemy implements Combatant {

    protected final String name;
    protected final int attackPower;
    protected int hp;

    protected Enemy(String name, int hp, int attackPower) {
        if (hp <= 0) {
            throw new IllegalArgumentException("Les points de vie doivent etre positifs");
        }
        this.name = name;
        this.hp = hp;
        this.attackPower = attackPower;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getHp() {
        return hp;
    }

    public int getAttackPower() {
        return attackPower;
    }

    @Override
    public boolean isAlive() {
        return hp > 0;
    }

    @Override
    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Les degats ne peuvent pas etre negatifs");
        }
        hp = Math.max(0, hp - amount);
    }
}