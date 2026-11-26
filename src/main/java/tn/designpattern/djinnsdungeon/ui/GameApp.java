package tn.designpattern.djinnsdungeon.ui;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.designpattern.djinnsdungeon.model.composite.Combatant;
import tn.designpattern.djinnsdungeon.model.factory.BossRoomGenerator;
import tn.designpattern.djinnsdungeon.model.factory.CombatRoomGenerator;
import tn.designpattern.djinnsdungeon.model.factory.Room;
import tn.designpattern.djinnsdungeon.model.factory.TreasureRoom;
import tn.designpattern.djinnsdungeon.model.factory.TreasureRoomGenerator;
import tn.designpattern.djinnsdungeon.model.game.Dungeon;
import tn.designpattern.djinnsdungeon.model.game.Game;
import tn.designpattern.djinnsdungeon.model.state.Hero;
import tn.designpattern.djinnsdungeon.model.theme.CatacombsFactory;
import tn.designpattern.djinnsdungeon.model.theme.DesertFactory;
import tn.designpattern.djinnsdungeon.model.theme.MedinaFactory;
import tn.designpattern.djinnsdungeon.model.theme.ThemeFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameApp extends Application {
    private static final double W = Painter.W, H = Painter.H, GROUND = Painter.GROUND;
    private static final double HERO_X = W * 0.2;

    private record Pop(String text, double x, double y, double born, Color color) {}

    private static class Spark {
        double x, y, vx, vy, life;
        Color color;
    }

    private final ThemeFactory[] themes = {new DesertFactory(), new MedinaFactory(), new CatacombsFactory()};
    private final Sfx sfx = new Sfx();

    private Game game;
    private int themeIndex;
    private final Map<Combatant, Integer> maxHp = new HashMap<>();
    private final Map<Combatant, Double> hitAt = new HashMap<>();
    private final Map<Combatant, Double> deadAt = new HashMap<>();
    private final List<Pop> pops = new ArrayList<>();
    private final List<Spark> sparks = new ArrayList<>();

    private double clock, lunge, heroHurtAt = -10, roomAt = -10;
    private boolean busy, endPlayed;

    private GraphicsContext g;
    private ComboBox<String> themeBox;
    private Button attackBtn, nextBtn, muteBtn;
    private ListView<String> logView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(W, H);
        g = canvas.getGraphicsContext2D();

        Label themeLabel = new Label("Thème :");
        themeLabel.setStyle("-fx-text-fill: #e8d9b5; -fx-font-weight: bold;");
        themeBox = new ComboBox<>();
        themeBox.getItems().addAll("Désert", "Médina", "Catacombes");
        themeBox.getSelectionModel().select(0);

        Button newBtn = styled("Nouvelle partie");
        newBtn.setOnAction(e -> newGame());
        attackBtn = styled("⚔ Attaquer");
        attackBtn.setOnAction(e -> attack());
        nextBtn = styled("Salle suivante ➜");
        nextBtn.setOnAction(e -> nextRoom());
        muteBtn = styled("🔊");
        muteBtn.setOnAction(e -> {
            sfx.setMuted(!sfx.isMuted());
            muteBtn.setText(sfx.isMuted() ? "🔇" : "🔊");
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(10, themeLabel, themeBox, newBtn, spacer, attackBtn, nextBtn, muteBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10));
        bar.setStyle("-fx-background-color: #1b1522;");

        logView = new ListView<>();
        logView.setPrefHeight(110);
        logView.setStyle("-fx-control-inner-background: #14101a;");
        logView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(Color.web("#e8d9b5"));
                setStyle("-fx-background-color: transparent;");
            }
        });

        stage.setTitle("Djinn's Dungeon");
        stage.setScene(new Scene(new VBox(canvas, bar, logView)));
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> sfx.stop());
        stage.show();

        newGame();
        sfx.startMusic();

        new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (last == 0) last = now;
                double dt = (now - last) / 1e9;
                last = now;
                clock += dt;
                update(dt);
                render();
            }
        }.start();
    }

    private Button styled(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: #c9892b; -fx-text-fill: white; -fx-font-weight: bold;"
                + "-fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 18;");
        return b;
    }

    // ================= logique de jeu (appelle le modèle) =================

    private void newGame() {
        themeIndex = themeBox.getSelectionModel().getSelectedIndex();
        ThemeFactory theme = themes[themeIndex];
        Dungeon dungeon = new Dungeon(List.of(
                new CombatRoomGenerator().prepareRoom(theme),
                new TreasureRoomGenerator().prepareRoom(theme),
                new CombatRoomGenerator().prepareRoom(theme),
                new BossRoomGenerator().prepareRoom(theme)));
        game = new Game(new Hero(100), dungeon);
        maxHp.clear();
        hitAt.clear();
        deadAt.clear();
        pops.clear();
        sparks.clear();
        lunge = 0;
        busy = false;
        endPlayed = false;
        enterRoom();
    }

    private void enterRoom() {
        int heroBefore = game.getHero().getHp();
        game.enterNextRoom();
        Room room = game.getCurrentRoom();
        for (Combatant c : room.getEnemies()) maxHp.put(c, c.getHp());
        roomAt = clock;
        sfx.play(room instanceof TreasureRoom ? "treasure" : "door");
        int lost = heroBefore - game.getHero().getHp();
        if (lost > 0) heroHit(lost);
        updateButtons();
        updateLog();
    }

    private void nextRoom() {
        Room r = game.getCurrentRoom();
        if (busy || !r.isCleared() || !game.hasNextRoom()) return;
        enterRoom();
    }

    private void attack() {
        if (busy || game.isDefeat() || game.getCurrentRoom().isCleared()) return;
        busy = true;
        updateButtons();
        sfx.play("swing");
        DoubleProperty p = new SimpleDoubleProperty(0);
        p.addListener((o, a, b) -> lunge = b.doubleValue());
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(p, 0)),
                new KeyFrame(Duration.millis(170), new KeyValue(p, 1, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(190), e -> doTurn()),
                new KeyFrame(Duration.millis(450), new KeyValue(p, 0, Interpolator.EASE_BOTH)));
        tl.setOnFinished(e -> {
            busy = false;
            updateButtons();
        });
        tl.play();
    }

    private void doTurn() {
        Hero hero = game.getHero();
        List<Combatant> es = game.getCurrentRoom().getEnemies();
        int heroBefore = hero.getHp();
        int[] before = es.stream().mapToInt(Combatant::getHp).toArray();
        game.playTurn();
        for (int i = 0; i < es.size(); i++) {
            int d = before[i] - es.get(i).getHp();
            if (d > 0) {
                double ex = enemyX(i, es.size());
                hitAt.put(es.get(i), clock);
                pops.add(new Pop("-" + d, ex, GROUND - 190, clock, Color.web("#ffd24a")));
                burst(ex, GROUND - 90, Color.web("#ffb347"));
                sfx.play("hit");
                if (!es.get(i).isAlive()) deadAt.put(es.get(i), clock);
            }
        }
        int lost = heroBefore - hero.getHp();
        if (lost > 0) heroHit(lost);
        updateLog();
    }

    private void heroHit(int amount) {
        heroHurtAt = clock;
        pops.add(new Pop("-" + amount, HERO_X, GROUND - 190, clock, Color.web("#ff5a5a")));
        burst(HERO_X, GROUND - 90, Color.web("#ff5a5a"));
        sfx.play("hurt");
    }

    private void burst(double x, double y, Color c) {
        for (int i = 0; i < 14; i++) {
            Spark s = new Spark();
            s.x = x;
            s.y = y;
            double a = Math.random() * Math.PI * 2, v = 80 + Math.random() * 200;
            s.vx = Math.cos(a) * v;
            s.vy = Math.sin(a) * v - 120;
            s.life = 0.5 + Math.random() * 0.4;
            s.color = c;
            sparks.add(s);
        }
    }

    private void updateButtons() {
        Room r = game.getCurrentRoom();
        boolean over = game.isDefeat();
        attackBtn.setDisable(busy || over || r.isCleared());
        nextBtn.setDisable(busy || over || !r.isCleared() || !game.hasNextRoom());
    }

    private void updateLog() {
        logView.getItems().setAll(game.getLog());
        logView.scrollTo(logView.getItems().size() - 1);
    }

    // ================= animation et dessin =================

    private double enemyX(int i, int n) {
        return n == 1 ? W * 0.72 : W * 0.56 + i * (W * 0.32 / (n - 1));
    }

    private void update(double dt) {
        for (Spark s : sparks) {
            s.x += s.vx * dt;
            s.y += s.vy * dt;
            s.vy += 500 * dt;
            s.life -= dt;
        }
        sparks.removeIf(s -> s.life <= 0);
        pops.removeIf(p -> clock - p.born() > 1.1);
        if (!endPlayed && !busy) {
            if (game.isDefeat()) {
                sfx.play("lose");
                endPlayed = true;
            } else if (game.isVictory()) {
                sfx.play("win");
                endPlayed = true;
            }
        }
    }

    private void render() {
        Room room = game.getCurrentRoom();
        Hero hero = game.getHero();
        Painter.background(g, themeIndex, clock);

        List<Combatant> es = room.getEnemies();
        for (int i = 0; i < es.size(); i++) {
            Combatant c = es.get(i);
            double x = enemyX(i, es.size());
            double hit = Math.max(0, 1 - (clock - hitAt.getOrDefault(c, -10.0)) / 0.35);
            double dead = c.isAlive() ? 0 : Math.min(1, (clock - deadAt.getOrDefault(c, clock - 10)) / 0.7);
            Painter.enemy(g, c.getName(), x, GROUND, clock, hit, dead, i);
            if (c.isAlive()) {
                double by = GROUND - Painter.enemyHeight(c.getName()) - 22;
                Painter.text(g, c.getName() + "  " + c.getHp() + "/" + maxHp.get(c),
                        x, by - 6, 15, Color.WHITE, TextAlignment.CENTER);
                Painter.bar(g, x - 45, by, 90, 10, (double) c.getHp() / maxHp.get(c), Color.web("#e04848"));
            }
        }
        if (room instanceof TreasureRoom) Painter.chest(g, W * 0.72, GROUND, clock);

        double hurt = Math.max(0, 1 - (clock - heroHurtAt) / 0.4);
        Painter.hero(g, HERO_X, GROUND, clock, lunge, hero.getWeapon().getDescription(),
                hero.getState().getName(), hurt);

        for (Spark s : sparks) {
            g.setGlobalAlpha(Math.min(1, s.life * 2));
            g.setFill(s.color);
            g.fillOval(s.x, s.y, 5, 5);
        }
        for (Pop p : pops) {
            double age = clock - p.born();
            g.setGlobalAlpha(Math.max(0, 1 - age / 1.1));
            Painter.text(g, p.text(), p.x(), p.y() - age * 55, 34, p.color(), TextAlignment.CENTER);
        }
        g.setGlobalAlpha(1);

        drawHud(hero);
        drawBanner(room);
        drawEnd();
    }

    private void drawHud(Hero hero) {
        g.setFill(Color.rgb(10, 8, 16, 0.65));
        g.fillRoundRect(15, 15, 330, 112, 16, 16);
        Painter.text(g, "HÉROS   " + hero.getHp() + "/" + hero.getMaxHp(), 28, 40, 16,
                Color.WHITE, TextAlignment.LEFT);
        Painter.bar(g, 28, 48, 304, 14, (double) hero.getHp() / hero.getMaxHp(), Color.web("#52c45a"));
        String st = hero.getState().getName();
        Color sc = switch (st) {
            case "Empoisonné" -> Color.web("#7dff7d");
            case "Gelé" -> Color.web("#9fe0ff");
            case "Enragé" -> Color.web("#ff6a55");
            default -> Color.web("#e8d9b5");
        };
        Painter.text(g, "État : " + st, 28, 86, 15, sc, TextAlignment.LEFT);
        Painter.text(g, "Arme : " + hero.getWeapon().getDescription() + " (" + hero.getWeapon().getDamage() + ")",
                28, 108, 14, Color.web("#ffd24a"), TextAlignment.LEFT);
    }

    private void drawBanner(Room room) {
        double age = clock - roomAt;
        double a = age < 0.3 ? age / 0.3 : age < 2.5 ? 1 : Math.max(0, 1 - (age - 2.5) / 0.7);
        if (a <= 0) return;
        g.setGlobalAlpha(a);
        Painter.text(g, room.getName(), W / 2 + 120, 70, 32, Color.web("#fff0c0"), TextAlignment.CENTER);
        g.setGlobalAlpha(1);
    }

    private void drawEnd() {
        boolean lost = game.isDefeat(), won = game.isVictory();
        if (!lost && !won) return;
        g.setFill(Color.rgb(0, 0, 0, 0.55));
        g.fillRect(0, 0, W, H);
        Painter.text(g, won ? "VICTOIRE !" : "DÉFAITE...", W / 2, H / 2, 72,
                won ? Color.web("#ffd24a") : Color.web("#ff5a5a"), TextAlignment.CENTER);
        Painter.text(g, "Clique sur « Nouvelle partie » pour rejouer", W / 2, H / 2 + 50, 20,
                Color.WHITE, TextAlignment.CENTER);
    }
}