package tn.designpattern.djinnsdungeon.model.game;

import tn.designpattern.djinnsdungeon.model.composite.Combatant;
import tn.designpattern.djinnsdungeon.model.composite.Enemy;
import tn.designpattern.djinnsdungeon.model.factory.Room;
import tn.designpattern.djinnsdungeon.model.factory.TreasureRoom;
import tn.designpattern.djinnsdungeon.model.state.Hero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private static final int MAX_TURNS = 1000; // sécurité anti-boucle infinie

    private final Hero hero;
    private final Dungeon dungeon;
    private final List<String> log = new ArrayList<>();
    private Room currentRoom;

    public Game(Hero hero, Dungeon dungeon) {
        this.hero = hero;
        this.dungeon = dungeon;
    }

    public Hero getHero() { return hero; }
    public Room getCurrentRoom() { return currentRoom; }
    public boolean hasNextRoom() { return dungeon.hasNext(); }
    public List<String> getLog() { return Collections.unmodifiableList(log); }

    public boolean isDefeat() { return !hero.isAlive(); }

    public boolean isVictory() {
        return hero.isAlive() && !dungeon.hasNext()
                && currentRoom != null && currentRoom.isCleared();
    }

    /** Entre dans la salle suivante (piège et trésor appliqués). */
    public Room enterNextRoom() {
        currentRoom = dungeon.nextRoom();
        log.add("Entrée : " + currentRoom.getName() + " (" + currentRoom.getDecor().getName() + ")");

        if (currentRoom.getTrap() != null) {
            hero.takeDamage(currentRoom.getTrap().getDamage());
            log.add("Piège : " + currentRoom.getTrap().getName()
                    + " (-" + currentRoom.getTrap().getDamage() + " PV)");
        }

        if (currentRoom instanceof TreasureRoom treasure) {
            hero.setWeapon(treasure.applyReward(hero.getWeapon()));
            log.add("Nouvelle arme : " + hero.getWeapon().getDescription());
        }
        return currentRoom;
    }

    /** Joue un seul tour de combat dans la salle courante. */
    public void playTurn() {
        if (currentRoom == null || !hero.isAlive() || currentRoom.isCleared()) return;

        hero.startTurn();
        if (!hero.isAlive()) return;

        Combatant target = firstAlive(currentRoom.getEnemies());
        int damage = hero.attackDamage();
        if (target != null && damage > 0) {
            target.takeDamage(damage);
            log.add("Le héros frappe " + target.getName() + " (-" + damage + ")");
        }

        for (Combatant c : currentRoom.getEnemies()) {
            if (c.isAlive() && c instanceof Enemy enemy) {
                hero.takeDamage(enemy.getAttackPower());
                log.add(enemy.getName() + " frappe le héros (-" + enemy.getAttackPower() + ")");
            }
        }
    }

    /** Joue toutes les salles. Retourne true si le héros survit. */
    public boolean playAll() {
        while (hero.isAlive() && dungeon.hasNext()) {
            playNextRoom();
        }
        return hero.isAlive();
    }

    /** Joue une salle entière. Retourne true si le héros est encore vivant. */
    public boolean playNextRoom() {
        enterNextRoom();
        int turns = 0;
        while (hero.isAlive() && !currentRoom.isCleared() && turns++ < MAX_TURNS) {
            playTurn();
        }
        return hero.isAlive();
    }

    private Combatant firstAlive(List<Combatant> list) {
        for (Combatant c : list) {
            if (c.isAlive()) return c;
        }
        return null;
    }
}