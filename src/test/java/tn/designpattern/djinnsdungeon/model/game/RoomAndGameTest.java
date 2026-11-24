package tn.designpattern.djinnsdungeon.model.game;

import org.junit.jupiter.api.Test;
import tn.designpattern.djinnsdungeon.model.decorator.BasicSword;
import tn.designpattern.djinnsdungeon.model.factory.*;
import tn.designpattern.djinnsdungeon.model.state.Hero;
import tn.designpattern.djinnsdungeon.model.theme.DesertFactory;
import tn.designpattern.djinnsdungeon.model.theme.MedinaFactory;
import tn.designpattern.djinnsdungeon.model.theme.ThemeFactory;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class RoomAndGameTest {

    private final ThemeFactory desert = new DesertFactory();

    @Test
    void combatRoomUsesThemeFamily() {
        Room room = new CombatRoomGenerator().prepareRoom(desert);
        assertEquals(2, room.getEnemies().size());
        assertEquals("Scorpion", room.getEnemies().get(0).getName());
        assertEquals("Sables mouvants", room.getTrap().getName());
        assertEquals("Dunes", room.getDecor().getName());
    }

    @Test
    void treasureRoomHasNoEnemiesAndIsCleared() {
        Room room = new TreasureRoomGenerator().prepareRoom(desert);
        assertTrue(room.getEnemies().isEmpty());
        assertTrue(room.isCleared());
    }

    @Test
    void bossRoomHasOneBoss() {
        Room room = new BossRoomGenerator().prepareRoom(new MedinaFactory());
        assertEquals(1, room.getEnemies().size());
        assertEquals("Djinn maudit", room.getEnemies().get(0).getName());
        assertFalse(room.isCleared());
    }

    @Test
    void treasureRewardAddsDjinn() {
        TreasureRoom room = (TreasureRoom) new TreasureRoomGenerator().prepareRoom(desert);
        assertEquals(15, room.applyReward(new BasicSword()).getDamage());
    }

    @Test
    void nullThemeIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new CombatRoomGenerator().prepareRoom(null));
    }

    @Test
    void dungeonGivesRoomsInOrder() {
        Dungeon d = new Dungeon(List.of(
                new CombatRoomGenerator().prepareRoom(desert),
                new BossRoomGenerator().prepareRoom(desert)));
        assertTrue(d.nextRoom().getName().startsWith("Salle de combat"));
        assertTrue(d.nextRoom().getName().startsWith("Salle du boss"));
        assertFalse(d.hasNext());
        assertThrows(NoSuchElementException.class, d::nextRoom);
    }

    @Test
    void fullRunIsWon() {
        Hero hero = new Hero(100);
        Dungeon d = new Dungeon(List.of(
                new CombatRoomGenerator().prepareRoom(desert),
                new TreasureRoomGenerator().prepareRoom(desert),
                new BossRoomGenerator().prepareRoom(desert)));
        Game game = new Game(hero, d);

        assertTrue(game.playAll());
        assertEquals(46, hero.getHp());
        assertEquals("Épée + Feu", hero.getWeapon().getDescription());
        assertFalse(game.getLog().isEmpty());
    }

    @Test
    void weakHeroDiesAgainstBoss() {
        Hero hero = new Hero(10);
        Dungeon d = new Dungeon(List.of(new BossRoomGenerator().prepareRoom(desert)));
        Game game = new Game(hero, d);

        assertFalse(game.playAll());
        assertFalse(hero.isAlive());
    }

    @Test
    void turnByTurnPlay() {
        Hero hero = new Hero(100);
        Game game = new Game(hero,
                new Dungeon(List.of(new CombatRoomGenerator().prepareRoom(desert))));

        game.enterNextRoom();
        assertEquals(96, hero.getHp());          // piège -4
        game.playTurn();
        assertEquals(88, hero.getHp());          // 2 scorpions : -8
        assertFalse(game.getCurrentRoom().isCleared());

        game.playTurn();
        game.playTurn();
        game.playTurn();
        assertTrue(game.getCurrentRoom().isCleared());
        assertEquals(80, hero.getHp());
        assertTrue(game.isVictory());
    }
}