package tn.designpattern.djinnsdungeon.model.action;

/** Ce qui vient de se passer dans le niveau (pour les sons et les effets). */
public record WorldEvent(Type type, double x, int amount, EnemyUnit unit) {
    public enum Type { SWING, ENEMY_HURT, ENEMY_DIED, HERO_HURT, STATE_DAMAGE }
}