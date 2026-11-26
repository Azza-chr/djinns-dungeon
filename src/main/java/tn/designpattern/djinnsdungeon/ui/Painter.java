package tn.designpattern.djinnsdungeon.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.Random;

/** Dessine décors, héros et ennemis avec des formes JavaFX (aucune image externe). */
public final class Painter {
    private Painter() {}

    public static final double W = 960, H = 540, GROUND = 430;

    // ================= utilitaires =================

    private static LinearGradient vgrad(Color... cs) {
        Stop[] st = new Stop[cs.length];
        for (int i = 0; i < cs.length; i++) st[i] = new Stop(i / (double) (cs.length - 1), cs[i]);
        return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, st);
    }

    private static RadialGradient glow(double cx, double cy, double r, Color c) {
        return new RadialGradient(0, 0, cx, cy, r, false, CycleMethod.NO_CYCLE,
                new Stop(0, c), new Stop(1, Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0)));
    }

    public static void text(GraphicsContext g, String s, double x, double y, double size,
                            Color c, TextAlignment a) {
        g.setFont(Font.font("Arial", FontWeight.BOLD, size));
        g.setTextAlign(a);
        g.setLineWidth(Math.max(2, size / 6));
        g.setStroke(Color.rgb(0, 0, 0, 0.85));
        g.strokeText(s, x, y);
        g.setFill(c);
        g.fillText(s, x, y);
    }

    public static void bar(GraphicsContext g, double x, double y, double w, double h, double r, Color c) {
        r = Math.max(0, Math.min(1, r));
        g.setFill(Color.rgb(20, 15, 25, 0.85));
        g.fillRoundRect(x - 2, y - 2, w + 4, h + 4, h, h);
        g.setFill(c);
        g.fillRoundRect(x, y, w * r, h, h, h);
        g.setFill(Color.rgb(255, 255, 255, 0.25));
        g.fillRoundRect(x, y, w * r, h / 2, h, h);
    }

    // ================= décors =================

    public static void background(GraphicsContext g, int theme, double t) {
        switch (theme) {
            case 0 -> desert(g, t);
            case 1 -> medina(g, t);
            default -> catacombs(g, t);
        }
    }

    private static void dune(GraphicsContext g, Color c, double base, double amp, double freq, double ph) {
        g.setFill(c);
        g.beginPath();
        g.moveTo(0, H);
        for (double x = 0; x <= W; x += 8) {
            g.lineTo(x, base + amp * Math.sin(x * freq + ph) + amp * 0.4 * Math.sin(x * freq * 2.3 + ph * 2));
        }
        g.lineTo(W, H);
        g.closePath();
        g.fill();
    }

    private static void desert(GraphicsContext g, double t) {
        g.setFill(vgrad(Color.web("#2f80c8"), Color.web("#f7d58b"), Color.web("#f5a35a")));
        g.fillRect(0, 0, W, H);
        g.setFill(glow(760, 110, 160, Color.rgb(255, 245, 190, 0.95)));
        g.fillOval(600, -50, 320, 320);
        g.setFill(Color.web("#fff6c8"));
        g.fillOval(730, 80, 60, 60);
        // pyramides
        g.setFill(Color.web("#c98b4a"));
        g.fillPolygon(new double[]{150, 260, 370}, new double[]{340, 200, 340}, 3);
        g.setFill(Color.web("#a8703a"));
        g.fillPolygon(new double[]{260, 370, 260}, new double[]{200, 340, 340}, 3);
        g.setFill(Color.web("#c98b4a"));
        g.fillPolygon(new double[]{340, 410, 480}, new double[]{340, 275, 340}, 3);
        dune(g, Color.web("#e8b865"), 345, 26, 0.006, 0.9 + t * 0.02);
        dune(g, Color.web("#dca550"), 385, 20, 0.009, 1.7 + t * 0.04);
        g.setFill(vgrad(Color.web("#d9a24a"), Color.web("#b9812f")));
        g.fillRect(0, GROUND - 12, W, H - GROUND + 12);
        g.setStroke(Color.rgb(120, 70, 20, 0.25));
        g.setLineWidth(2);
        for (int i = 0; i < 6; i++) g.strokeLine(0, GROUND + 15 + i * 20, W, GROUND + 15 + i * 20 + (i % 2) * 6);
        // vent de sable
        g.setFill(Color.rgb(255, 235, 180, 0.55));
        for (int i = 0; i < 30; i++) {
            double x = (i * 131 + t * (80 + i % 5 * 25)) % W;
            g.fillRect(x, 300 + (i * 53) % 220, 3, 1.5);
        }
    }

    private static void medina(GraphicsContext g, double t) {
        g.setFill(vgrad(Color.web("#1a1d4a"), Color.web("#7a4a9a"), Color.web("#f29a62")));
        g.fillRect(0, 0, W, H);
        Random r = new Random(7);
        for (int i = 0; i < 60; i++) {
            double x = r.nextDouble() * W, y = r.nextDouble() * 220;
            g.setFill(Color.rgb(255, 255, 230, 0.4 + 0.6 * Math.abs(Math.sin(t * 1.5 + i))));
            g.fillOval(x, y, 2, 2);
        }
        g.setFill(glow(150, 80, 90, Color.rgb(255, 246, 213, 0.6)));
        g.fillOval(60, -10, 180, 180);
        g.setFill(Color.web("#fff6d5"));
        g.fillOval(120, 50, 60, 60);
        // minaret
        g.setFill(Color.web("#f3ead8"));
        g.fillRect(800, 130, 36, 320);
        g.setFill(Color.web("#d8ccb4"));
        g.fillRect(792, 175, 52, 9);
        g.setFill(Color.web("#2d6fb4"));
        g.fillPolygon(new double[]{796, 818, 840}, new double[]{130, 78, 130}, 3);
        g.setFill(Color.web("#d9a826"));
        g.fillOval(814, 68, 8, 8);
        // maisons blanches aux portes bleues
        for (int i = -1; i < 9; i++) {
            double bx = i * 125 + 10, bh = 110 + (((i * 53) % 4) + 4) % 4 * 30, by = GROUND - bh;
            g.setFill(Color.web(i % 2 == 0 ? "#efe3d0" : "#e2d2ba"));
            g.fillRect(bx, by, 118, bh + 10);
            g.setFill(Color.rgb(0, 0, 0, 0.15));
            g.fillRect(bx + 90, by, 28, bh + 10);
            if (i % 3 == 0) {
                g.setFill(Color.web("#2d6fb4"));
                g.fillArc(bx + 29, by - 40, 60, 80, 0, 180, ArcType.ROUND);
            }
            g.setFill(Color.web("#2b6cb0"));
            g.fillRect(bx + 44, GROUND - 62, 30, 62);
            g.fillArc(bx + 44, GROUND - 77, 30, 30, 0, 180, ArcType.ROUND);
            g.setFill(Color.rgb(255, 200, 90, 0.7 + 0.3 * Math.sin(t * 3 + i * 2)));
            g.fillRoundRect(bx + 14, by + 24, 18, 26, 9, 9);
            g.fillRoundRect(bx + 94, by + 24, 16, 26, 8, 8);
        }
        g.setFill(vgrad(Color.web("#b78b60"), Color.web("#8a6340")));
        g.fillRect(0, GROUND - 10, W, H - GROUND + 10);
        g.setStroke(Color.rgb(60, 35, 20, 0.35));
        g.setLineWidth(2);
        for (int i = 0; i < 4; i++) g.strokeLine(0, GROUND + 10 + i * 28, W, GROUND + 10 + i * 28);
        for (double x = -400; x < W + 400; x += 80) g.strokeLine(x, GROUND - 10, (x - W / 2) * 1.8 + W / 2, H);
    }

    private static void skull(GraphicsContext g, double x, double y, double s) {
        g.setFill(Color.web("#d8d4c0"));
        g.fillOval(x, y, s, s * 0.9);
        g.fillRect(x + s * 0.25, y + s * 0.7, s * 0.5, s * 0.3);
        g.setFill(Color.BLACK);
        g.fillOval(x + s * 0.18, y + s * 0.3, s * 0.25, s * 0.28);
        g.fillOval(x + s * 0.57, y + s * 0.3, s * 0.25, s * 0.28);
    }

    private static void torch(GraphicsContext g, double x, double y, double t) {
        g.setFill(glow(x, y - 10, 130, Color.rgb(255, 150, 40, 0.45)));
        g.fillOval(x - 130, y - 140, 260, 260);
        g.setFill(Color.web("#5a3a1a"));
        g.fillRect(x - 4, y, 8, 40);
        double f = 1 + 0.15 * Math.sin(t * 14 + x);
        g.setFill(Color.web("#ff7a00"));
        g.fillOval(x - 11, y - 34 * f + 6, 22, 40 * f);
        g.setFill(Color.web("#ffe066"));
        g.fillOval(x - 6, y - 20 * f + 6, 12, 26 * f);
    }

    private static void catacombs(GraphicsContext g, double t) {
        g.setFill(vgrad(Color.web("#0f0d16"), Color.web("#2a2535"), Color.web("#3a3346")));
        g.fillRect(0, 0, W, H);
        g.setStroke(Color.rgb(0, 0, 0, 0.35));
        g.setLineWidth(2);
        for (int row = 0; row * 40 < GROUND; row++) {
            double y = row * 40;
            g.strokeLine(0, y, W, y);
            for (double x = (row % 2) * 60; x < W; x += 120) g.strokeLine(x, y, x, y + 40);
        }
        for (int i = 0; i < 3; i++) {
            double ax = 90 + i * 300;
            g.setFill(Color.rgb(5, 4, 10));
            g.fillRect(ax, 170, 140, GROUND - 170);
            g.fillArc(ax, 110, 140, 120, 0, 180, ArcType.ROUND);
            g.setFill(Color.web("#4a4357"));
            g.fillRect(ax - 18, 150, 18, GROUND - 150);
            g.fillRect(ax + 140, 150, 18, GROUND - 150);
            for (int j = 0; j < 3; j++) skull(g, ax + 25 + j * 38, GROUND - 26, 22);
        }
        torch(g, 310, 230, t);
        torch(g, 610, 230, t);
        torch(g, 910, 230, t);
        g.setFill(vgrad(Color.web("#2d2836"), Color.web("#1b1822")));
        g.fillRect(0, GROUND - 10, W, H - GROUND + 10);
        g.setStroke(Color.rgb(0, 0, 0, 0.4));
        for (int i = 0; i < 4; i++) g.strokeLine(0, GROUND + 10 + i * 28, W, GROUND + 10 + i * 28);
        g.setFill(Color.rgb(200, 200, 255, 0.05));
        for (int i = 0; i < 5; i++) g.fillOval(((i * 220 + t * 20) % (W + 300)) - 150, GROUND - 30, 300, 60);
    }

    // ================= héros =================

    private static Color bladeColor(String w) {
        if (w.contains("Feu")) return Color.web("#ff7a1a");
        if (w.contains("Poison")) return Color.web("#5cff5c");
        if (w.contains("Glace")) return Color.web("#8fe3ff");
        if (w.contains("Vol")) return Color.web("#ff4d6d");
        return Color.web("#dfe6ee");
    }

    private static void aura(GraphicsContext g, String state, double t) {
        if (state.equals("Normal")) return;
        Color c = switch (state) {
            case "Empoisonné" -> Color.rgb(80, 220, 80, 0.28);
            case "Gelé" -> Color.rgb(130, 210, 255, 0.38);
            default -> Color.rgb(255, 60, 40, 0.33);
        };
        double p = 1 + 0.06 * Math.sin(t * 8);
        g.setFill(c);
        g.fillOval(-55 * p, -80 - 100 * p, 110 * p, 200 * p);
        if (state.equals("Empoisonné")) {
            g.setFill(Color.rgb(120, 255, 120, 0.8));
            for (int i = 0; i < 4; i++) g.fillOval(-30 + i * 18, -40 - ((t * 45 + i * 37) % 110), 8, 8);
        } else if (state.equals("Gelé")) {
            g.setFill(Color.rgb(190, 240, 255, 0.85));
            for (int i = 0; i < 4; i++) {
                g.fillPolygon(new double[]{-44 + i * 26, -34 + i * 26, -24 + i * 26},
                        new double[]{0, -50 - (i % 2) * 25, 0}, 3);
            }
        }
    }

    public static void hero(GraphicsContext g, double x, double y, double t, double lunge,
                            String weapon, String state, double hurt) {
        double bob = Math.sin(t * 3) * 2.5;
        Color blade = bladeColor(weapon);
        g.setFill(Color.rgb(0, 0, 0, 0.3));
        g.fillOval(x - 42 + lunge * 70, y - 9, 84, 18);
        g.save();
        g.translate(x + lunge * 70, y + bob);
        aura(g, state, t);
        double sway = Math.sin(t * 4) * 4;
        g.setFill(Color.web("#8e1b1b")); // cape
        g.fillPolygon(new double[]{-18, -34 - sway - lunge * 20, -30 - sway, -14},
                new double[]{-108, -70, -30, -40}, 4);
        g.setFill(Color.web("#3a2a1c")); // jambes
        g.fillRoundRect(-17, -48, 15, 48, 6, 6);
        g.fillRoundRect(3, -48, 15, 48, 6, 6);
        g.setFill(Color.web("#1d140c")); // bottes
        g.fillRoundRect(-20, -12, 20, 12, 5, 5);
        g.fillRoundRect(2, -12, 22, 12, 5, 5);
        g.setFill(Color.web("#2f6fb8")); // tunique
        g.fillRoundRect(-25, -112, 50, 70, 18, 18);
        g.setFill(Color.web("#d9a826"));
        g.fillRect(-25, -74, 50, 8);
        g.fillOval(-5, -76, 10, 12);
        g.setFill(Color.web("#f2efe6"));
        g.fillPolygon(new double[]{-12, 0, 12}, new double[]{-112, -92, -112}, 3);
        g.setFill(Color.web("#e0ac7b")); // tête
        g.fillOval(-15, -140, 30, 32);
        g.setFill(Color.web("#2a1a10"));
        g.fillOval(4, -128, 5, 5);
        g.setFill(Color.web("#f4f0e4")); // turban
        g.fillOval(-19, -156, 38, 24);
        g.setFill(Color.web("#c0392b"));
        g.fillRect(-17, -139, 34, 5);
        g.setFill(Color.web("#38b6ff"));
        g.fillOval(-3, -148, 8, 8);
        // bras + épée
        g.save();
        g.translate(18, -92);
        g.rotate(-50 + lunge * 110);
        g.setFill(Color.web("#2f6fb8"));
        g.fillRoundRect(-4, -6, 34, 12, 6, 6);
        g.setFill(Color.web("#e0ac7b"));
        g.fillOval(26, -6, 12, 12);
        g.setFill(Color.web("#d9a826"));
        g.fillRect(34, -12, 6, 24);
        g.setEffect(new DropShadow(18, blade));
        g.setFill(blade);
        g.fillPolygon(new double[]{40, 110, 118, 110, 40}, new double[]{-5, -4, 0, 4, 5}, 5);
        g.setEffect(null);
        g.restore();
        if (hurt > 0) {
            g.setFill(Color.rgb(255, 0, 0, 0.35 * hurt));
            g.fillRoundRect(-26, -156, 52, 156, 20, 20);
        }
        g.restore();
    }

    // ================= ennemis =================

    public static void enemy(GraphicsContext g, String name, double x, double y, double t,
                             double hit, double dead, int idx) {
        double bob = Math.sin(t * 3 + idx * 1.7) * 2.5;
        double shake = Math.sin(t * 60) * 8 * hit;
        g.save();
        g.setGlobalAlpha(1 - dead);
        g.setFill(Color.rgb(0, 0, 0, 0.3));
        g.fillOval(x - 50, y - 9, 100, 18);
        g.translate(x + shake, y + bob);
        g.rotate(dead * 80);
        if (hit > 0) g.setEffect(new ColorAdjust(0, 0, 0.5 * hit, 0));
        switch (name) {
            case "Scorpion" -> scorpion(g, t, idx);
            case "Garde" -> guard(g, t);
            case "Squelette" -> skeleton(g, t);
            case "Djinn maudit" -> boss(g, t);
            default -> {
                g.setFill(Color.PURPLE);
                g.fillOval(-30, -80, 60, 80);
            }
        }
        g.setEffect(null);
        g.restore();
    }

    /** Hauteur approximative d'un ennemi, pour placer sa barre de vie. */
    public static double enemyHeight(String name) {
        return switch (name) {
            case "Scorpion" -> 100;
            case "Garde" -> 215;
            case "Squelette" -> 160;
            case "Djinn maudit" -> 280;
            default -> 100;
        };
    }

    private static void scorpion(GraphicsContext g, double t, int idx) {
        g.scale(1.3, 1.3);
        Color body = Color.web("#8a3d14"), dark = Color.web("#5e2a0d");
        g.setStroke(dark);
        g.setLineWidth(4);
        for (int i = 0; i < 4; i++) {
            double lx = -22 + i * 14, w = Math.sin(t * 10 + i + idx) * 5;
            g.strokeLine(lx, -22, lx - 10 + w, -2);
        }
        g.setFill(body);
        g.fillOval(-42, -48, 80, 40);
        g.fillOval(-60, -44, 30, 28);
        g.setStroke(dark);
        g.setLineWidth(2);
        for (int i = 0; i < 4; i++) g.strokeLine(-20 + i * 14, -47, -20 + i * 14, -10);
        g.setStroke(body);
        g.setLineWidth(7);
        g.strokeLine(-55, -35, -80, -44);
        double snap = Math.sin(t * 6) * 5;
        g.setFill(body);
        g.fillOval(-102, -64 - snap, 28, 14);
        g.fillOval(-102, -48 + snap, 28, 14);
        g.setFill(Color.RED);
        g.fillOval(-52, -42, 5, 5);
        g.fillOval(-44, -44, 5, 5);
        double px = 0, py = 0;
        for (int i = 0; i < 6; i++) {
            px = 34 + 16 * Math.sin(i * 0.6) + Math.sin(t * 4) * 2;
            py = -34 - 12 * i + 1.4 * i * i;
            double r = 9 - i * 0.7;
            g.setFill(body);
            g.fillOval(px - r, py - r, 2 * r, 2 * r);
        }
        g.setFill(Color.web("#2b0f05"));
        g.fillPolygon(new double[]{px - 4, px + 4, px - 7}, new double[]{py - 4, py - 4, py + 12}, 3);
    }

    private static void guard(GraphicsContext g, double t) {
        double sway = Math.sin(t * 3) * 2;
        g.setStroke(Color.web("#6b4a2a")); // lance
        g.setLineWidth(5);
        g.strokeLine(42, 0, 42 + sway, -185);
        g.setFill(Color.web("#c7ced6"));
        g.fillPolygon(new double[]{36 + sway, 48 + sway, 42 + sway}, new double[]{-185, -185, -220}, 3);
        g.setFill(Color.web("#3a2a1c"));
        g.fillRoundRect(-18, -48, 15, 48, 6, 6);
        g.fillRoundRect(3, -48, 15, 48, 6, 6);
        g.setFill(Color.web("#a63232"));
        g.fillRoundRect(-26, -114, 52, 70, 16, 16);
        g.setFill(Color.web("#8e98a3"));
        g.fillRoundRect(-26, -114, 52, 26, 12, 12);
        g.setFill(Color.web("#4b3b2a"));
        g.fillRect(-26, -76, 52, 7);
        g.setFill(Color.web("#d19a6a"));
        g.fillOval(-15, -142, 30, 32);
        g.setFill(Color.web("#8e98a3")); // casque
        g.fillArc(-19, -152, 38, 34, 0, 180, ArcType.ROUND);
        g.fillPolygon(new double[]{-4, 4, 0}, new double[]{-152, -152, -174}, 3);
        g.setFill(Color.web("#2a1a10"));
        g.fillOval(-9, -129, 5, 5);
        g.fillRect(-14, -119, 16, 4);
        g.setFill(Color.web("#c9a227")); // bouclier
        g.fillOval(-48, -108, 34, 56);
        g.setFill(Color.web("#8a6b12"));
        g.fillOval(-39, -88, 16, 16);
    }

    private static void skeleton(GraphicsContext g, double t) {
        Color bone = Color.web("#ecebdc");
        double sway = Math.sin(t * 3) * 3;
        g.setStroke(bone);
        g.setLineWidth(6);
        g.strokeLine(-8, -48, -10, 0);
        g.strokeLine(8, -48, 10, 0);
        g.setFill(bone);
        g.fillOval(-18, -6, 16, 8);
        g.fillOval(4, -6, 16, 8);
        g.setLineWidth(8);
        g.strokeLine(0, -48, 0, -112);
        g.setLineWidth(4);
        for (int i = 0; i < 4; i++) g.strokeArc(-20, -108 + i * 13, 40, 18, 180, 180, ArcType.OPEN);
        g.fillOval(-16, -58, 32, 14);
        g.setLineWidth(5);
        g.strokeLine(-18, -104, -44, -84 + sway);
        g.strokeLine(18, -104, 26, -70);
        g.setStroke(Color.web("#9a9a96")); // épée rouillée
        g.setLineWidth(6);
        g.strokeLine(-44, -84 + sway, -72, -150 + sway);
        g.setFill(bone);
        g.fillOval(-17, -146, 34, 32);
        g.fillRoundRect(-11, -118, 22, 10, 4, 4);
        g.setFill(Color.BLACK);
        g.fillOval(-12, -136, 10, 12);
        g.fillOval(2, -136, 10, 12);
        g.setFill(Color.RED);
        g.fillOval(-9, -132, 4, 4);
        g.fillOval(5, -132, 4, 4);
        g.setStroke(Color.web("#555"));
        g.setLineWidth(1);
        for (int i = 0; i < 4; i++) g.strokeLine(-7 + i * 5, -118, -7 + i * 5, -109);
    }

    private static void boss(GraphicsContext g, double t) {
        g.scale(0.9, 0.9);
        g.translate(0, -30 + Math.sin(t * 2) * 8);
        g.setFill(glow(0, -120, 130, Color.rgb(160, 60, 255, 0.45)));
        g.fillOval(-130, -250, 260, 260);
        for (int i = 6; i >= 0; i--) { // queue de fumée
            double r = 40 - i * 5, ox = Math.sin(t * 3 + i * 0.8) * 10;
            g.setFill(Color.rgb(90, 50, 170, Math.max(0.3, 0.95 - i * 0.08)));
            g.fillOval(ox - r, -60 + i * 14 - r, 2 * r, 2 * r);
        }
        g.setFill(Color.web("#4b3fbf")); // torse
        g.fillOval(-50, -190, 100, 120);
        g.setFill(Color.web("#d9a826"));
        g.fillRoundRect(-36, -168, 72, 10, 5, 5);
        g.setFill(Color.web("#5a4fd0")); // bras croisés
        g.fillRoundRect(-60, -150, 120, 26, 13, 13);
        g.setFill(Color.web("#d9a826"));
        g.fillRect(-60, -150, 10, 26);
        g.fillRect(50, -150, 10, 26);
        g.setFill(Color.web("#5a4fd0")); // tête
        g.fillOval(-30, -240, 60, 58);
        g.setFill(Color.web("#e8dcc0")); // cornes
        g.fillPolygon(new double[]{-26, -42, -14}, new double[]{-225, -275, -238}, 3);
        g.fillPolygon(new double[]{26, 42, 14}, new double[]{-225, -275, -238}, 3);
        g.setFill(Color.web("#ffe347")); // yeux
        g.fillOval(-18, -220, 12, 8);
        g.fillOval(6, -220, 12, 8);
        g.setFill(Color.BLACK);
        g.fillArc(-14, -208, 28, 16, 180, 180, ArcType.ROUND);
        g.setFill(Color.web("#d9a826")); // couronne
        g.fillPolygon(new double[]{-20, -20, -8, 0, 8, 20, 20},
                new double[]{-238, -258, -246, -264, -246, -258, -238}, 7);
    }

    // ================= coffre =================

    public static void chest(GraphicsContext g, double x, double y, double t) {
        g.save();
        g.translate(x, y);
        g.setFill(Color.rgb(0, 0, 0, 0.3));
        g.fillOval(-60, -9, 120, 18);
        g.setFill(glow(0, -80, 120 + 10 * Math.sin(t * 4), Color.rgb(255, 215, 80, 0.7)));
        g.fillOval(-130, -210, 260, 260);
        g.setFill(Color.web("#7a4a1e"));
        g.fillRoundRect(-50, -60, 100, 60, 8, 8);
        g.setFill(Color.web("#d9a826"));
        g.fillRect(-50, -40, 100, 8);
        g.fillRect(-8, -60, 16, 60);
        g.save();
        g.translate(-50, -60);
        g.rotate(-35);
        g.setFill(Color.web("#9a5f28"));
        g.fillArc(0, -35, 100, 70, 0, 180, ArcType.ROUND);
        g.restore();
        g.setFill(Color.web("#fff3a0"));
        for (int i = 0; i < 5; i++) {
            double s = 3 + 3 * Math.abs(Math.sin(t * 3 + i * 1.3));
            g.fillOval(-40 + i * 20, -90 - ((t * 30 + i * 23) % 60), s, s);
        }
        g.restore();
    }
}