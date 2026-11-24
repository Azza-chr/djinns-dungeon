package tn.designpattern.djinnsdungeon.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import tn.designpattern.djinnsdungeon.model.composite.Combatant;
import tn.designpattern.djinnsdungeon.model.factory.*;
import tn.designpattern.djinnsdungeon.model.game.Dungeon;
import tn.designpattern.djinnsdungeon.model.game.Game;
import tn.designpattern.djinnsdungeon.model.state.Hero;
import tn.designpattern.djinnsdungeon.model.theme.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameApp extends Application {

    private final ThemeFactory[] themes = {
            new DesertFactory(), new MedinaFactory(), new CatacombsFactory()
    };
    private final String[] backgrounds = { "#E8C97A", "#C9A27E", "#5A5A66" };

    private Game game;
    private final Map<Combatant, Integer> maxHp = new HashMap<>();

    private ComboBox<String> themeBox;
    private Label roomLabel, heroLabel, statusLabel;
    private ProgressBar heroBar;
    private HBox enemiesBox;
    private VBox centerPane;
    private ListView<String> logView;
    private Button attackBtn, nextBtn;
    private int themeIndex;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        themeBox = new ComboBox<>();
        themeBox.getItems().addAll("Désert", "Médina", "Catacombes");
        themeBox.getSelectionModel().select(0);

        Button newBtn = new Button("Nouvelle partie");
        newBtn.setOnAction(e -> newGame());

        HBox top = new HBox(10, new Label("Thème :"), themeBox, newBtn);
        top.setPadding(new Insets(10));
        top.setAlignment(Pos.CENTER_LEFT);

        roomLabel = new Label();
        roomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        enemiesBox = new HBox(20);
        enemiesBox.setAlignment(Pos.CENTER);
        heroLabel = new Label();
        heroBar = new ProgressBar(1.0);
        heroBar.setPrefWidth(300);
        statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Rectangle heroRect = new Rectangle(70, 70, Color.ROYALBLUE);

        centerPane = new VBox(15, roomLabel, enemiesBox, heroRect, heroLabel, heroBar, statusLabel);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setPadding(new Insets(15));

        attackBtn = new Button("Attaquer");
        attackBtn.setOnAction(e -> { game.playTurn(); refresh(); });
        nextBtn = new Button("Salle suivante");
        nextBtn.setOnAction(e -> { enterRoom(); refresh(); });

        HBox buttons = new HBox(10, attackBtn, nextBtn);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));

        logView = new ListView<>();
        logView.setPrefHeight(180);

        VBox bottom = new VBox(buttons, logView);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(centerPane);
        root.setBottom(bottom);

        stage.setTitle("Djinn's Dungeon");
        stage.setScene(new Scene(root, 700, 650));
        stage.show();

        newGame();
    }

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
        enterRoom();
        refresh();
    }

    private void enterRoom() {
        game.enterNextRoom();
        for (Combatant c : game.getCurrentRoom().getEnemies()) {
            maxHp.put(c, c.getHp());
        }
    }

    private void refresh() {
        Hero hero = game.getHero();
        var room = game.getCurrentRoom();

        centerPane.setStyle("-fx-background-color: " + backgrounds[themeIndex] + ";");
        roomLabel.setText(room.getName() + " - " + room.getDecor().getName());

        enemiesBox.getChildren().clear();
        for (Combatant c : room.getEnemies()) {
            int max = maxHp.get(c);
            Rectangle rect = new Rectangle(70, 70, c.isAlive() ? Color.FIREBRICK : Color.GRAY);
            Label name = new Label(c.getName() + " " + c.getHp() + "/" + max);
            ProgressBar bar = new ProgressBar((double) c.getHp() / max);
            VBox card = new VBox(5, rect, name, bar);
            card.setAlignment(Pos.CENTER);
            enemiesBox.getChildren().add(card);
        }

        heroLabel.setText("Héros : " + hero.getHp() + "/" + hero.getMaxHp() + " PV"
                + " | État : " + hero.getState().getName()
                + " | Arme : " + hero.getWeapon().getDescription()
                + " (" + hero.getWeapon().getDamage() + ")");
        heroBar.setProgress((double) hero.getHp() / hero.getMaxHp());

        if (game.isDefeat()) {
            statusLabel.setText("Défaite...");
        } else if (game.isVictory()) {
            statusLabel.setText("Victoire !");
        } else {
            statusLabel.setText("");
        }

        attackBtn.setDisable(game.isDefeat() || room.isCleared());
        nextBtn.setDisable(game.isDefeat() || !room.isCleared() || !game.hasNextRoom());

        logView.getItems().setAll(game.getLog());
        logView.scrollTo(logView.getItems().size() - 1);
    }
}