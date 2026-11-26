package tn.designpattern.djinnsdungeon.ui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import tn.designpattern.djinnsdungeon.model.action.EnemyUnit;
import tn.designpattern.djinnsdungeon.model.action.LevelWorld;
import tn.designpattern.djinnsdungeon.model.decorator.FireDjinn;
import tn.designpattern.djinnsdungeon.model.state.Hero;
import tn.designpattern.djinnsdungeon.model.theme.CatacombsFactory;
import tn.designpattern.djinnsdungeon.model.theme.DesertFactory;
import tn.designpattern.djinnsdungeon.model.theme.MedinaFactory;
import tn.designpattern.djinnsdungeon.model.theme.ThemeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameApp extends Application {
    private enum Screen { MAP, PLAY, END }

    private static final double W = Painter.W, H = Painter.H;
    private static final double GY = Painter.GROUND + 40;

    private final ThemeFactory[] themes = { new DesertFactory(), new MedinaFactory(), new CatacombsFactory() };
    private final String[] worldNames = { "Le Sahara", "La Médina", "Les Catacombes" };
    private final Color[] worldColors = { Color.web("#e8b865"), Color.web("#3d7fc4"), Color.web("#7a6a8a") };
    private final int[] enemyCount = { 2, 3, 3 };

    private final Progress progress = new Progress();
    private final Set<KeyCode> keys = new HashSet<>();
    private final List<Floating> floats = new ArrayList<>();
    private final Map<EnemyUnit, Integer> lastHp = new HashMap<>();
    private final Map<EnemyUnit, Double> hurtUntil = new HashMap<>();

    private SpriteSet heroSprites, zombieSprites, robotSprites;
    private Screen screen = Screen.MAP;
    private LevelWorld world;
    private int currentLevel, theme, lastHeroHp;
    private double t, heroHurtUntil, cam;
    private boolean recorded;
    private int earnedStars;

    private record Floating(double x, double y0, String text, Color color, double born) {}

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        heroSprites = new SpriteSet("Male adventurer", "maleAdventurer", 170);
        zombieSprites = new SpriteSet("Zombie", "zombie", 160);
        robotSprites = new SpriteSet("Robot", "robot", 160);

        Canvas canvas = new Canvas(W, H);
        GraphicsContext g = canvas.getGraphicsContext2D();
        Scene scene = new Scene(new StackPane(canvas), W, H);
        scene.setOnKeyPressed(e -> keys.add(e.getCode()));
        scene.setOnKeyReleased(e -> keys.remove(e.getCode()));
        scene.setOnMouseClicked(e -> click(e.getX(), e.getY()));

        stage.setTitle("Djinn's Dungeon");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        new AnimationTimer() {
            long last = 0;
            @Override public void handle(long now) {
                if (last == 0) last = now;
                double dt = Math.min(0.05, (now - last) / 1e9);
                last = now;
                t += dt;
                if (screen != Screen.MAP) step(dt);
                draw(g);
            }
        }.start();
    }

    // ---------------- logique ----------------
    private void startLevel(int level) {
        currentLevel = level;
        theme = level / 3;
        int l = level % 3;
        Hero hero = new Hero(100);
        for (int i = 0; i < progress.djinnCount(); i++) hero.setWeapon(new FireDjinn(hero.getWeapon()));
        world = LevelWorld.create(hero, themes[theme], enemyCount[theme] + (l == 2 ? 0 : l), l == 2,
                2800 + l * 600, 45 + l * 20);
        floats.clear();
        lastHp.clear();
        hurtUntil.clear();
        for (EnemyUnit u : world.getEnemies()) lastHp.put(u, u.getHp());
        lastHeroHp = hero.getHp();
        heroHurtUntil = 0;
        recorded = false;
        screen = Screen.PLAY;
    }

    private void step(double dt) {
        if (screen != Screen.PLAY) return;
        int dir = 0;
        if (keys.contains(KeyCode.LEFT) || keys.contains(KeyCode.Q) || keys.contains(KeyCode.A)) dir -= 1;
        if (keys.contains(KeyCode.RIGHT) || keys.contains(KeyCode.D)) dir += 1;
        world.update(dt, dir, keys.contains(KeyCode.SPACE));

        Hero hero = world.getHero();
        if (hero.getHp() < lastHeroHp) {
            heroHurtUntil = t + 0.25;
            floats.add(new Floating(world.getHeroX(), GY - 190, "-" + (lastHeroHp - hero.getHp()),
                    Color.CRIMSON, t));
        }
        lastHeroHp = hero.getHp();
        for (EnemyUnit u : world.getEnemies()) {
            int before = lastHp.getOrDefault(u, u.getHp());
            if (u.getHp() < before) {
                hurtUntil.put(u, t + 0.25);
                floats.add(new Floating(u.getX(), GY - 180, "-" + (before - u.getHp()), Color.GOLD, t));
            }
            lastHp.put(u, u.getHp());
        }
        floats.removeIf(f -> t - f.born() > 1.0);

        cam = Math.max(0, Math.min(world.getLength() - W, world.getHeroX() - W * 0.35));

        if (world.isOver() && !recorded) {
            recorded = true;
            earnedStars = world.getStars();
            progress.record(currentLevel, earnedStars);
            screen = Screen.END;
        }
    }

    private void click(double x, double y) {
        if (screen == Screen.MAP) {
            for (int i = 0; i < Progress.LEVELS; i++) {
                double[] p = nodePos(i);
                if (Math.hypot(x - p[0], y - p[1]) < 36 && progress.isUnlocked(i)) {
                    startLevel(i);
                    return;
                }
            }
        } else if (screen == Screen.END) {
            if (world.isVictory() && currentLevel + 1 < Progress.LEVELS && x > W / 2) {
                startLevel(currentLevel + 1);
            } else if (!world.isVictory() && x > W / 2) {
                startLevel(currentLevel);
            } else {
                screen = Screen.MAP;
            }
        }
    }

    // ---------------- dessin ----------------
    private double[] nodePos(int i) {
        double x = 110 + i * (W - 220) / (Progress.LEVELS - 1);
        double y = H / 2 + 20 + 110 * Math.sin(i * 1.1);
        return new double[]{x, y};
    }

    private void text(GraphicsContext g, String s, double x, double y, double size, Color c) {
        g.setFont(Font.font("Arial", FontWeight.BOLD, size));
        g.setTextAlign(TextAlignment.CENTER);
        g.setFill(Color.rgb(0, 0, 0, 0.55));
        g.fillText(s, x + 2, y + 2);
        g.setFill(c);
        g.fillText(s, x, y);
    }

    private void star(GraphicsContext g, double cx, double cy, double r, boolean on) {
        double[] xs = new double[10], ys = new double[10];
        for (int i = 0; i < 10; i++) {
            double a = -Math.PI / 2 + i * Math.PI / 5, rr = i % 2 == 0 ? r : r * 0.45;
            xs[i] = cx + Math.cos(a) * rr;
            ys[i] = cy + Math.sin(a) * rr;
        }
        g.setFill(on ? Color.GOLD : Color.rgb(60, 60, 70, 0.8));
        g.fillPolygon(xs, ys, 10);
        g.setStroke(on ? Color.web("#a67c00") : Color.rgb(20, 20, 25));
        g.setLineWidth(1.5);
        g.strokePolygon(xs, ys, 10);
    }

    private void draw(GraphicsContext g) {
        g.clearRect(0, 0, W, H);
        switch (screen) {
            case MAP -> drawMap(g);
            case PLAY -> drawLevel(g);
            case END -> { drawLevel(g); drawEnd(g); }
        }
    }

    private void drawMap(GraphicsContext g) {
        g.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#1d2b53")), new Stop(1, Color.web("#f29a62"))));
        g.fillRect(0, 0, W, H);
        for (int w = 0; w < 3; w++) {
            double x0 = w * W / 3;
            g.setFill(Color.color(worldColors[w].getRed(), worldColors[w].getGreen(), worldColors[w].getBlue(), 0.35));
            g.fillRect(x0, 90, W / 3, H - 90);
            text(g, worldNames[w], x0 + W / 6, 130, 26, Color.WHITE);
        }
        text(g, "Djinn's Dungeon", W / 2, 55, 40, Color.GOLD);

        g.setStroke(Color.rgb(255, 240, 200, 0.8));
        g.setLineWidth(5);
        g.setLineDashes(14, 10);
        for (int i = 0; i < Progress.LEVELS - 1; i++) {
            double[] a = nodePos(i), b = nodePos(i + 1);
            g.strokeLine(a[0], a[1], b[0], b[1]);
        }
        g.setLineDashes(null);

        for (int i = 0; i < Progress.LEVELS; i++) {
            double[] p = nodePos(i);
            boolean open = progress.isUnlocked(i);
            double pulse = open && progress.getStars(i) == 0 ? 3 * Math.sin(t * 5) : 0;
            g.setFill(open ? worldColors[i / 3].brighter() : Color.GRAY);
            g.fillOval(p[0] - 34 - pulse, p[1] - 34 - pulse, 68 + 2 * pulse, 68 + 2 * pulse);
            g.setStroke(Color.WHITE);
            g.setLineWidth(4);
            g.strokeOval(p[0] - 34 - pulse, p[1] - 34 - pulse, 68 + 2 * pulse, 68 + 2 * pulse);
            text(g, open ? String.valueOf(i + 1) : "🔒", p[0], p[1] + 12, 30, Color.WHITE);
            for (int s = 0; s < 3; s++) star(g, p[0] - 24 + s * 24, p[1] + 58, 11, progress.getStars(i) > s);
        }
        text(g, "Étoiles : " + progress.totalStars() + "/27   |   Djinns : " + progress.djinnCount(),
                W / 2, H - 25, 20, Color.WHITE);
    }

    private void drawLevel(GraphicsContext g) {
        WorldPainter.background(g, theme, cam, t);
        Hero hero = world.getHero();

        for (EnemyUnit u : world.getEnemies()) drawEnemy(g, u);
        drawHero(g, hero);

        for (Floating f : floats) {
            double age = t - f.born();
            g.setGlobalAlpha(Math.max(0, 1 - age));
            text(g, f.text(), f.x() - cam, f.y0() - age * 60, 28, f.color());
            g.setGlobalAlpha(1);
        }
        drawHud(g, hero);
    }

    private void drawHero(GraphicsContext g, Hero hero) {
        double x = world.getHeroX() - cam;
        g.setFill(Color.rgb(0, 0, 0, 0.25));
        g.fillOval(x - 40, GY - 8, 80, 16);
        String pose;
        if (world.isDefeat()) pose = "fallDown";
        else if (world.isVictory()) pose = "cheer" + ((int) (t * 4) % 2);
        else if (world.isSwinging()) pose = "attack" + Math.min(2, (int) (world.getSwingProgress() * 3));
        else if (t < heroHurtUntil) pose = "hurt";
        else if (world.isMoving()) pose = "walk" + ((int) (t * 12) % 8);
        else pose = "idle";
        if (heroSprites.isAvailable()) {
            heroSprites.draw(g, pose, x, GY, world.isFacingRight(), 1);
        } else {
            g.setFill(Color.ROYALBLUE);
            g.fillRoundRect(x - 25, GY - 150, 50, 150, 20, 20);
        }
        String st = hero.getState().getName();
        if (!st.equals("Normal")) {
            Color c = st.toLowerCase().contains("poison") || st.toLowerCase().contains("empoisonn") ? Color.LIMEGREEN : Color.CYAN;
            g.setStroke(Color.color(c.getRed(), c.getGreen(), c.getBlue(), 0.5 + 0.3 * Math.sin(t * 8)));
            g.setLineWidth(4);
            g.strokeOval(x - 55, GY - 190, 110, 200);
        }
    }

    private void drawEnemy(GraphicsContext g, EnemyUnit u) {
        double x = u.getX() - cam;
        if (x < -150 || x > W + 150) return;
        boolean alive = u.isAlive();
        boolean right = u.isFacingRight();
        if (u.getAiName().equals("Poursuite") || u.getAttackProgress() >= 0) {
            right = u.getX() < world.getHeroX();
        }
        if (alive) {
            g.setFill(Color.rgb(0, 0, 0, 0.25));
            g.fillOval(x - 40, GY - 8, 80, 16);
        }
        String name = u.getName();
        if (name.equals("Scorpion")) drawScorpion(g, x, right, alive, u);
        else if (name.equals("Djinn maudit")) drawDjinn(g, x, right, alive);
        else {
            SpriteSet set = name.equals("Garde") ? robotSprites : zombieSprites;
            String pose;
            if (!alive) pose = "fallDown";
            else if (u.getAttackProgress() >= 0) pose = "attack" + Math.min(2, (int) (u.getAttackProgress() * 3));
            else if (hurtUntil.getOrDefault(u, 0.0) > t) pose = "hurt";
            else if (u.getAiName().equals("Poursuite") || u.getAiName().equals("Patrouille")) pose = "walk" + ((int) (t * 8) % 8);
            else pose = "idle";
            if (set.isAvailable()) set.draw(g, pose, x, GY, right, alive ? 1 : 0.5);
            else { g.setFill(Color.FIREBRICK); g.fillRoundRect(x - 25, GY - 140, 50, 140, 20, 20); }
        }
        if (alive) {
            double max = Math.max(u.getHp(), lastMax(u));
            g.setFill(Color.rgb(0, 0, 0, 0.6));
            g.fillRect(x - 35, GY - 205, 70, 9);
            g.setFill(Color.CRIMSON);
            g.fillRect(x - 34, GY - 204, 68.0 * u.getHp() / max, 7);
            text(g, u.getName(), x, GY - 212, 13, Color.WHITE);
        }
    }

    private final Map<EnemyUnit, Integer> maxHp = new HashMap<>();

    private int lastMax(EnemyUnit u) {
        return maxHp.merge(u, u.getHp(), Math::max);
    }

    private void drawScorpion(GraphicsContext g, double x, boolean right, boolean alive, EnemyUnit u) {
        g.save();
        g.translate(x, GY);
        if (!right) g.scale(-1, 1);
        if (!alive) g.setGlobalAlpha(0.45);
        double bob = Math.sin(t * 10) * 2;
        Color body = hurtUntil.getOrDefault(u, 0.0) > t ? Color.WHITE : Color.web("#8b3a1a");
        g.setStroke(body);
        g.setLineWidth(10);
        g.strokeArc(-70, -110, 90, 110, -20, 200, javafx.scene.shape.ArcType.OPEN); // queue
        g.setFill(Color.web("#ffcc33"));
        g.fillOval(-40, -112, 16, 16); // dard
        g.setFill(body);
        g.fillOval(-40, -45 + bob, 90, 40);
        g.setFill(body.darker());
        g.fillOval(40, -42 + bob, 36, 28);
        g.setFill(Color.web("#c0531f"));
        g.fillOval(62, -62 + bob, 30, 24); // pinces
        g.fillOval(62, -22 + bob, 30, 22);
        g.setFill(Color.WHITE);
        g.fillOval(58, -38 + bob, 8, 8);
        g.setStroke(body.darker());
        g.setLineWidth(4);
        for (int i = 0; i < 4; i++) {
            double lx = -25 + i * 22, sw = Math.sin(t * 12 + i) * 5;
            g.strokeLine(lx, -8, lx - 8 + sw, 2);
        }
        g.restore();
    }

    private void drawDjinn(GraphicsContext g, double x, boolean right, boolean alive) {
        g.save();
        g.translate(x, GY - 40 + Math.sin(t * 3) * 10);
        if (!right) g.scale(-1, 1);
        if (!alive) g.setGlobalAlpha(0.4);
        g.setFill(Color.rgb(140, 40, 200, 0.35));
        g.fillOval(-90, -230, 180, 250);
        g.setFill(Color.web("#6a1fb5"));
        g.fillPolygon(new double[]{-45, 45, 20, -20}, new double[]{-20, -20, 40, 40}, 4); // queue de fumée
        g.fillOval(-55, -170, 110, 160);
        g.setFill(Color.web("#8e3fd6"));
        g.fillOval(-38, -230, 76, 76);
        g.setFill(Color.GOLD);
        g.fillPolygon(new double[]{-38, -30, -20, -8, 0, 8, 20, 30, 38}, new double[]{-205, -250, -215, -255, -218, -255, -215, -250, -205}, 9); // couronne
        g.setFill(Color.WHITE);
        g.fillOval(-22, -202, 14, 10);
        g.fillOval(6, -202, 14, 10);
        g.setFill(Color.RED);
        g.fillOval(-18, -200, 6, 7);
        g.fillOval(10, -200, 6, 7);
        g.setFill(Color.web("#4a1480"));
        g.fillPolygon(new double[]{-35, -55, -25}, new double[]{-205, -260, -225}, 3); // cornes
        g.fillPolygon(new double[]{35, 55, 25}, new double[]{-205, -260, -225}, 3);
        g.restore();
    }

    private void drawHud(GraphicsContext g, Hero hero) {
        g.setFill(Color.rgb(0, 0, 0, 0.55));
        g.fillRoundRect(15, 12, 360, 78, 16, 16);
        g.setFill(Color.rgb(60, 0, 0));
        g.fillRoundRect(28, 24, 250, 20, 10, 10);
        g.setFill(hero.getHp() > hero.getMaxHp() / 2 ? Color.LIMEGREEN : Color.ORANGERED);
        g.fillRoundRect(28, 24, 250.0 * Math.max(0, hero.getHp()) / hero.getMaxHp(), 20, 10, 10);
        g.setTextAlign(TextAlignment.LEFT);
        g.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        g.setFill(Color.WHITE);
        g.fillText(hero.getHp() + "/" + hero.getMaxHp() + " PV", 288, 40);
        g.fillText("État : " + hero.getState().getName(), 28, 64);
        g.fillText("Arme : " + hero.getWeapon().getDescription() + " (" + hero.getWeapon().getDamage() + ")", 28, 82);
        text(g, "Niveau " + (currentLevel + 1) + " - " + worldNames[theme], W - 190, 34, 18, Color.WHITE);
        text(g, String.format("Ennemis : %d   Temps : %.0fs", world.aliveCount(), world.getElapsed()),
                W - 190, 58, 15, Color.WHITE);
        if (world.getElapsed() < 6 && screen == Screen.PLAY) {
            text(g, "← → ou Q D : bouger   |   ESPACE : attaquer", W / 2, H - 25, 18, Color.WHITE);
        }
    }

    private void drawEnd(GraphicsContext g) {
        g.setFill(Color.rgb(0, 0, 0, 0.65));
        g.fillRect(0, 0, W, H);
        boolean win = world.isVictory();
        text(g, win ? "VICTOIRE !" : "DÉFAITE...", W / 2, 190, 60, win ? Color.GOLD : Color.CRIMSON);
        for (int s = 0; s < 3; s++) star(g, W / 2 - 80 + s * 80, 260, 34, earnedStars > s);
        if (win && progress.getStars(currentLevel) > 0) {
            text(g, "PV restants : " + world.getHero().getHp() + "   Temps : "
                    + String.format("%.0fs", world.getElapsed()), W / 2, 340, 20, Color.WHITE);
            text(g, "Chaque tranche de 3 niveaux gagne un djinn pour ton arme !", W / 2, 372, 16, Color.LIGHTGRAY);
        }
        text(g, "◀ Carte (clic gauche)", W / 4, 470, 24, Color.WHITE);
        boolean next = win && currentLevel + 1 < Progress.LEVELS;
        text(g, win ? (next ? "Niveau suivant ▶" : "Terminé !") : "Réessayer ▶", W * 3 / 4, 470, 24, Color.GOLD);
    }
}