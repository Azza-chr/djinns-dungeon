package tn.designpattern.djinnsdungeon.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;

import java.util.Random;

/** Décors qui défilent avec plusieurs plans à des vitesses différentes (parallaxe). */
public final class WorldPainter {
    private WorldPainter() {}

    private static final double W = Painter.W, H = Painter.H, G = Painter.GROUND;

    public static void background(GraphicsContext g, int theme, double cam, double t) {
        switch (theme) {
            case 0 -> desert(g, cam, t);
            case 1 -> medina(g, cam, t);
            default -> catacombs(g, cam, t);
        }
    }

    // ---------- utilitaires ----------
    private static LinearGradient vgrad(Color... cs) {
        Stop[] st = new Stop[cs.length];
        for (int i = 0; i < cs.length; i++) st[i] = new Stop(i / (double) (cs.length - 1), cs[i]);
        return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, st);
    }

    private static RadialGradient glow(double cx, double cy, double r, Color c) {
        return new RadialGradient(0, 0, cx, cy, r, false, CycleMethod.NO_CYCLE,
                new Stop(0, c), new Stop(1, Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0)));
    }

    private static double mod(double a, double b) { return ((a % b) + b) % b; }
    private static int hash(int i, int m) { return (((i * 53) % m) + m) % m; }
    private static int first(double off, double period) { return (int) Math.floor(off / period) - 1; }
    private static int last(double off, double period) { return (int) Math.floor((off + W) / period) + 1; }

    private static void dune(GraphicsContext g, Color c, double base, double amp, double freq, double ph, double off) {
        g.setFill(c);
        g.beginPath();
        g.moveTo(0, H);
        for (double x = 0; x <= W; x += 8) {
            double wx = x + off;
            g.lineTo(x, base + amp * Math.sin(wx * freq + ph) + amp * 0.4 * Math.sin(wx * freq * 2.3 + ph * 2));
        }
        g.lineTo(W, H);
        g.closePath();
        g.fill();
    }

    // ---------- Désert ----------
    private static void palm(GraphicsContext g, double x, double y, double t, double s) {
        double tx = x + 14 * s, ty = y - 150 * s, sway = Math.sin(t * 1.5 + x * 0.01) * 6;
        g.setStroke(Color.web("#6b4a2a"));
        g.setLineWidth(9 * s);
        g.strokeLine(x, y, tx, ty);
        g.setStroke(Color.web("#2f8f3a"));
        g.setLineWidth(7 * s);
        for (int k = 0; k <= 6; k++) {
            double a = Math.PI * k / 6.0;
            double mx = tx + Math.cos(a) * 45 * s, my = ty - Math.sin(a) * 35 * s;
            g.strokeLine(tx, ty, mx, my);
            g.strokeLine(mx, my, tx + Math.cos(a) * 85 * s + sway, my + 30 * s);
        }
    }

    private static void desert(GraphicsContext g, double cam, double t) {
        g.setFill(vgrad(Color.web("#2f80c8"), Color.web("#f7d58b"), Color.web("#f5a35a")));
        g.fillRect(0, 0, W, H);
        g.setFill(glow(760, 110, 160, Color.rgb(255, 245, 190, 0.95)));
        g.fillOval(600, -50, 320, 320);
        g.setFill(Color.web("#fff6c8"));
        g.fillOval(730, 80, 60, 60);

        double off = cam * 0.12;
        for (int i = first(off, 900); i <= last(off, 900); i++) {
            double x = i * 900 - off + 150, s = 200 + hash(i, 3) * 50;
            g.setFill(Color.web("#c98b4a"));
            g.fillPolygon(new double[]{x, x + s / 2, x + s}, new double[]{348, 348 - s * 0.65, 348}, 3);
            g.setFill(Color.web("#a8703a"));
            g.fillPolygon(new double[]{x + s / 2, x + s, x + s / 2}, new double[]{348 - s * 0.65, 348, 348}, 3);
        }
        dune(g, Color.web("#e8b865"), 345, 26, 0.006, 0.9, cam * 0.3);
        dune(g, Color.web("#dca550"), 385, 20, 0.009, 1.7, cam * 0.55);

        off = cam * 0.8;
        for (int i = first(off, 560); i <= last(off, 560); i++) {
            palm(g, i * 560 - off + 90 + hash(i, 5) * 40, G - 4, t, 0.8 + hash(i, 3) * 0.15);
        }

        g.setFill(vgrad(Color.web("#d9a24a"), Color.web("#b9812f")));
        g.fillRect(0, G - 12, W, H - G + 12);
        g.setStroke(Color.rgb(120, 70, 20, 0.25));
        g.setLineWidth(2);
        for (int row = 0; row < 5; row++) {
            double y = G + 14 + row * 22, step = 150 + row * 30;
            for (double x = -mod(cam * (1 + row * 0.1), step); x < W; x += step) g.strokeLine(x, y, x + 60, y);
        }
        g.setFill(Color.rgb(255, 235, 180, 0.55));
        for (int i = 0; i < 30; i++) {
            double x = mod(i * 131 + t * (80 + i % 5 * 25) - cam * 0.5, W);
            g.fillRect(x, 300 + (i * 53) % 220, 3, 1.5);
        }
    }

    // ---------- Médina ----------
    private static void medina(GraphicsContext g, double cam, double t) {
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

        double off = cam * 0.35;
        for (int i = first(off, 125); i <= last(off, 125); i++) {
            double bx = i * 125 - off, bh = 110 + hash(i, 4) * 30, by = G - bh;
            if (hash(i, 7) == 3) { // minaret
                g.setFill(Color.web("#f3ead8"));
                g.fillRect(bx + 40, by - 130, 36, 140);
                g.setFill(Color.web("#d8ccb4"));
                g.fillRect(bx + 32, by - 90, 52, 9);
                g.setFill(Color.web("#2d6fb4"));
                g.fillPolygon(new double[]{bx + 36, bx + 58, bx + 80}, new double[]{by - 130, by - 182, by - 130}, 3);
            }
            g.setFill(Color.web(i % 2 == 0 ? "#efe3d0" : "#e2d2ba"));
            g.fillRect(bx, by, 118, bh + 10);
            g.setFill(Color.rgb(0, 0, 0, 0.15));
            g.fillRect(bx + 90, by, 28, bh + 10);
            if (hash(i, 3) == 0) {
                g.setFill(Color.web("#2d6fb4"));
                g.fillArc(bx + 29, by - 40, 60, 80, 0, 180, ArcType.ROUND);
            }
            g.setFill(Color.web("#2b6cb0"));
            g.fillRect(bx + 44, G - 62, 30, 62);
            g.fillArc(bx + 44, G - 77, 30, 30, 0, 180, ArcType.ROUND);
            g.setFill(Color.rgb(255, 200, 90, 0.7 + 0.3 * Math.sin(t * 3 + i * 2)));
            g.fillRoundRect(bx + 14, by + 24, 18, 26, 9, 9);
            g.fillRoundRect(bx + 94, by + 24, 16, 26, 8, 8);
        }
        g.setFill(vgrad(Color.web("#b78b60"), Color.web("#8a6340")));
        g.fillRect(0, G - 10, W, H - G + 10);
        g.setStroke(Color.rgb(60, 35, 20, 0.35));
        g.setLineWidth(2);
        for (int i = 0; i < 4; i++) g.strokeLine(0, G + 10 + i * 28, W, G + 10 + i * 28);
        for (double x = -mod(cam, 80) - 400; x < W + 400; x += 80) g.strokeLine(x, G - 10, (x - W / 2) * 1.8 + W / 2, H);
    }

    // ---------- Catacombes ----------
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

    private static void catacombs(GraphicsContext g, double cam, double t) {
        g.setFill(vgrad(Color.web("#0f0d16"), Color.web("#2a2535"), Color.web("#3a3346")));
        g.fillRect(0, 0, W, H);
        g.setStroke(Color.rgb(0, 0, 0, 0.35));
        g.setLineWidth(2);
        double bo = mod(cam * 0.3, 120);
        for (int row = 0; row * 40 < G; row++) {
            double y = row * 40;
            g.strokeLine(0, y, W, y);
            for (double x = -bo + (row % 2) * 60 - 120; x < W; x += 120) g.strokeLine(x, y, x, y + 40);
        }
        double off = cam * 0.5;
        for (int i = first(off, 420); i <= last(off, 420); i++) {
            double ax = i * 420 - off + 60;
            g.setFill(Color.rgb(5, 4, 10));
            g.fillRect(ax, 170, 140, G - 170);
            g.fillArc(ax, 110, 140, 120, 0, 180, ArcType.ROUND);
            g.setFill(Color.web("#4a4357"));
            g.fillRect(ax - 18, 150, 18, G - 150);
            g.fillRect(ax + 140, 150, 18, G - 150);
            for (int j = 0; j < 3; j++) skull(g, ax + 25 + j * 38, G - 26, 22);
            torch(g, ax + 250, 230, t);
        }
        g.setFill(vgrad(Color.web("#2d2836"), Color.web("#1b1822")));
        g.fillRect(0, G - 10, W, H - G + 10);
        g.setStroke(Color.rgb(0, 0, 0, 0.4));
        for (int i = 0; i < 4; i++) g.strokeLine(0, G + 10 + i * 28, W, G + 10 + i * 28);
        for (double x = -mod(cam, 140); x < W; x += 140) g.strokeLine(x, G - 10, x - 30, H);
        g.setFill(Color.rgb(200, 200, 255, 0.05));
        for (int i = 0; i < 5; i++) g.fillOval(mod(i * 220 + t * 20 - cam * 0.6, W + 300) - 150, G - 30, 300, 60);
    }
}