package tn.designpattern.djinnsdungeon.model.state;

public interface HeroState {
    String getName();
    /** Appelé au début de chaque tour du héros. */
    void onTurnStart(Hero hero);
    /** Dégâts infligés, à partir des dégâts de base de l'arme. */
    int modifyOutgoingDamage(int baseDamage);
    /** Dégâts reçus, à partir des dégâts bruts. */
    int modifyIncomingDamage(int rawDamage);
    /** Le héros peut-il agir ce tour ? */
    boolean canAct();
}