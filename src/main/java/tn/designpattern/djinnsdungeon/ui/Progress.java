package tn.designpattern.djinnsdungeon.ui;

/** Progression du joueur : étoiles par niveau, niveaux débloqués, djinns gagnés. */
public class Progress {
    public static final int LEVELS = 9;
    private final int[] stars = new int[LEVELS];

    public int getStars(int level) { return stars[level]; }

    public void record(int level, int s) { stars[level] = Math.max(stars[level], s); }

    public boolean isUnlocked(int level) { return level == 0 || stars[level - 1] >= 1; }

    public int totalStars() {
        int t = 0;
        for (int s : stars) t += s;
        return t;
    }

    /** Un djinn de plus (maximum 3) pour chaque tranche de 3 niveaux terminés. */
    public int djinnCount() {
        int done = 0;
        for (int s : stars) if (s >= 1) done++;
        return Math.min(3, done / 3 + (done > 0 ? 1 : 0));
    }
}