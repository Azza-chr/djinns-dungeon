package tn.designpattern.djinnsdungeon.ui;

import javax.sound.sampled.*;

/** Sons et musique générés par le code (aucun fichier audio nécessaire). */
public class Sfx {
    private static final float RATE = 22050f;
    private static final AudioFormat FMT = new AudioFormat(RATE, 16, 1, true, false);

    private boolean muted;
    private Clip music;

    public boolean isMuted() { return muted; }

    public void setMuted(boolean m) {
        muted = m;
        if (m) {
            if (music != null) music.stop();
        } else if (music != null) {
            music.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            startMusic();
        }
    }

    public void stop() {
        if (music != null) music.close();
    }

    /** Boucle orientale (gamme phrygienne dominante) avec un bourdon grave. */
    public void startMusic() {
        if (muted) return;
        double[] notes = {293.66, 311.13, 369.99, 392.0, 369.99, 311.13, 293.66, 220.0};
        double[][] parts = new double[notes.length][];
        for (int i = 0; i < notes.length; i++) parts[i] = note(notes[i], 0.4, 0.16);
        double[] mix = join(parts);
        for (int i = 0; i < mix.length; i++) {
            double t = i / (double) RATE;
            mix[i] += 0.06 * Math.sin(2 * Math.PI * 75.0 * t) + 0.03 * Math.sin(2 * Math.PI * 112.5 * t);
        }
        music = start(mix, true);
    }

    public void play(String name) {
        if (muted) return;
        double[] s = switch (name) {
            case "swing" -> tone(900, 250, 0.14, 0.35, 0.35);
            case "hit" -> tone(200, 70, 0.16, 0.8, 0.9);
            case "hurt" -> tone(260, 90, 0.28, 0.5, 0.8);
            case "door" -> tone(140, 55, 0.25, 0.4, 0.7);
            case "treasure" -> join(tone(523, 523, 0.1, 0, 0.4), tone(659, 659, 0.1, 0, 0.4),
                    tone(784, 784, 0.1, 0, 0.4), tone(1047, 1047, 0.35, 0, 0.4));
            case "win" -> join(tone(392, 392, 0.15, 0, 0.45), tone(523, 523, 0.15, 0, 0.45),
                    tone(659, 659, 0.15, 0, 0.45), tone(784, 784, 0.5, 0, 0.45));
            case "lose" -> join(tone(330, 330, 0.25, 0, 0.45), tone(277, 277, 0.25, 0, 0.45),
                    tone(220, 220, 0.25, 0, 0.45), tone(165, 165, 0.6, 0, 0.45));
            default -> null;
        };
        if (s != null) start(s, false);
    }

    // ---------- synthèse ----------

    private static double[] tone(double f0, double f1, double dur, double noise, double vol) {
        int n = (int) (RATE * dur);
        double[] s = new double[n];
        double phase = 0;
        for (int i = 0; i < n; i++) {
            double p = (double) i / n;
            phase += 2 * Math.PI * (f0 + (f1 - f0) * p) / RATE;
            double env = Math.pow(1 - p, 2) * Math.min(1, i / 200.0);
            double v = Math.sin(phase) * (1 - noise) + (Math.random() * 2 - 1) * noise;
            s[i] = v * env * vol;
        }
        return s;
    }

    private static double[] note(double f, double dur, double vol) {
        int n = (int) (RATE * dur);
        double[] s = new double[n];
        for (int i = 0; i < n; i++) {
            double t = i / (double) RATE, p = (double) i / n;
            double v = Math.sin(2 * Math.PI * f * t) + 0.4 * Math.sin(4 * Math.PI * f * t)
                    + 0.2 * Math.sin(6 * Math.PI * f * t);
            s[i] = v * vol * Math.min(1, i / 300.0) * Math.exp(-3 * p);
        }
        return s;
    }

    private static double[] join(double[]... parts) {
        int total = 0;
        for (double[] p : parts) total += p.length;
        double[] out = new double[total];
        int k = 0;
        for (double[] p : parts) {
            System.arraycopy(p, 0, out, k, p.length);
            k += p.length;
        }
        return out;
    }

    private Clip start(double[] s, boolean loop) {
        try {
            byte[] b = new byte[s.length * 2];
            for (int i = 0; i < s.length; i++) {
                short v = (short) (Math.max(-1, Math.min(1, s[i])) * 32767);
                b[2 * i] = (byte) v;
                b[2 * i + 1] = (byte) (v >> 8);
            }
            Clip c = AudioSystem.getClip();
            c.open(FMT, b, 0, b.length);
            if (loop) {
                c.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                c.addLineListener(e -> { if (e.getType() == LineEvent.Type.STOP) c.close(); });
                c.start();
            }
            return c;
        } catch (Exception e) {
            return null; // pas de carte son : le jeu continue sans audio
        }
    }
}