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

    public Game(Hero hero, Dungeon dungeon) {
        this.hero = hero;
        this.dungeon = dungeon;
    }

    public List<String> getLog() { return Collections.unmodifiableList(log); }

    /** Joue toutes les salles. Retourne true si le héros survit. */
    public boolean playAll() {
        while (hero.isAlive() && dungeon.hasNext()) {
            playNextRoom();
        }
        return hero.isAlive();
    }

    /** Joue une salle. Retourne true si le héros est encore vivant. */
    public boolean playNextRoom() {
        Room room = dungeon.nextRoom();
        log.add("Entrée : " + room.getName() + " (" + room.getDecor().getName() + ")");

        if (room.getTrap() != null) {
            hero.takeDamage(room.getTrap().getDamage());
            log.add("Piège : " + room.getTrap().getName() + " (-" + room.getTrap().getDamage() + " PV)");
        }

        if (room instanceof TreasureRoom treasure) {
            hero.setWeapon(treasure.applyReward(hero.getWeapon()));
            log.add("Nouvelle arme : " + hero.getWeapon().getDescription());
        }

        int turns = 0;
        while (hero.isAlive() && !room.isCleared() && turns++ < MAX_TURNS) {
            hero.startTurn();
            if (!hero.isAlive()) break;

            Combatant target = firstAlive(room.getEnemies());
            int damage = hero.attackDamage();
            if (target != null && damage > 0) {
                target.takeDamage(damage);
                log.add("Le héros frappe " + target.getName() + " (-" + damage + ")");
            }

            for (Combatant c : room.getEnemies()) {
                if (c.isAlive() && c instanceof Enemy enemy) {
                    hero.takeDamage(enemy.getAttackPower());
                    log.add(enemy.getName() + " frappe le héros (-" + enemy.getAttackPower() + ")");
                }
            }
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