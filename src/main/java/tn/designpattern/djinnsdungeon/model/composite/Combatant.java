package tn.designpattern.djinnsdungeon.model.composite;

/**
 * Pattern COMPOSITE - interface commune (le "Component").
 * Un ennemi seul et un groupe d'ennemis l'implementent tous les deux :
 * le reste du jeu les traite de la meme facon.
 */
public interface Combatant {

    /** Nom affiche dans le journal de combat. */
    String getName();

    /** Points de vie restants (pour un groupe : la somme de ses membres). */
    int getHp();

    /** Vrai tant qu'il reste de la vie (pour un groupe : au moins un membre vivant). */
    boolean isAlive();

    /** Retire des points de vie. Un montant negatif est refuse. */
    void takeDamage(int amount);
}