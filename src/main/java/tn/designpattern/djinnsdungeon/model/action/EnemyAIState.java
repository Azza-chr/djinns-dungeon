package tn.designpattern.djinnsdungeon.model.action;

/** Patron STATE : le comportement d'un ennemi dépend de son état d'IA. */
public interface EnemyAIState {
    String getName();

    void update(EnemyUnit unit, double heroX, double dt);
}