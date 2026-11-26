package tn.designpattern.djinnsdungeon.model.action;

/** Score d'un niveau : 0 étoile si perdu, 1 si gagné, 2 avec au moins 50 % de PV, 3 avec en plus un bon temps. */
public final class StarRating {
    private StarRating() {}

    public static int stars(boolean won, int hp, int maxHp, double seconds, double parSeconds) {
        if (!won) return 0;
        boolean healthy = hp * 2 >= maxHp;
        if (healthy && seconds <= parSeconds) return 3;
        return healthy ? 2 : 1;
    }
}