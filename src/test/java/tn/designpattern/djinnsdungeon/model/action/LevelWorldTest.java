package tn.designpattern.djinnsdungeon.model.action;

import org.junit.jupiter.api.Test;
import tn.designpattern.djinnsdungeon.model.state.FrozenState;
import tn.designpattern.djinnsdungeon.model.state.Hero;
import tn.designpattern.djinnsdungeon.model.state.PoisonedState;
import tn.designpattern.djinnsdungeon.model.theme.DesertFactory;

import static org.junit.jupiter.api.Assertions.*;

class LevelWorldTest {

    private LevelWorld world(Hero hero, int count) {
        return LevelWorld.create(hero, new DesertFactory(), count, false, 3000, 60);
    }

    /** Un joueur automatique : marche vers l'ennemi vivant le plus proche et frappe. */
    private void autoPlay(LevelWorld w, int maxSteps) {
        for (int i = 0; i < maxSteps && !w.isOver(); i++) {
            EnemyUnit target = w.getEnemies().stream().filter(EnemyUnit::isAlive).findFirst().orElse(null);
            int dir = 0;
            if (target != null) {
                double dx = target.getX() - w.getHeroX();
                dir = Math.abs(dx) > 90 ? (dx > 0 ? 1 : -1) : 0;
            }
            w.update(0.016, dir, true);
        }
    }

    @Test
    void enemiesAreSpreadAlongTheLevel() {
        LevelWorld w = world(new Hero(100), 3);
        assertEquals(3, w.getEnemies().size());
        assertTrue(w.getEnemies().get(0).getX() >= 650);
        assertTrue(w.getEnemies().get(0).getX() < w.getEnemies().get(1).getX());
        assertTrue(w.getEnemies().get(1).getX() < w.getEnemies().get(2).getX());
    }

    @Test
    void enemyStartsPatrollingThenChasesWhenHeroIsClose() {
        LevelWorld w = world(new Hero(100), 1);
        assertEquals("Patrouille", w.getEnemies().get(0).getAiName());

        // Le héros avance jusqu'à ce que l'ennemi le repère (maximum 30 secondes simulées).
        for (int i = 0; i < 2000 && !w.isOver()
                && w.getEnemies().get(0).getAiName().equals("Patrouille"); i++) {
            w.update(0.016, 1, false);
        }
        assertNotEquals("Patrouille", w.getEnemies().get(0).getAiName());
    }

    @Test
    void heroWinsAgainstOneScorpion() {
        Hero hero = new Hero(100);
        LevelWorld w = world(hero, 1);
        autoPlay(w, 6000);
        assertTrue(w.isVictory());
        assertTrue(hero.isAlive());
        assertTrue(w.getStars() >= 1);
    }

    @Test
    void poisonCanKillTheHero() {
        Hero hero = new Hero(3);
        hero.setState(new PoisonedState());
        LevelWorld w = world(hero, 1);
        for (int i = 0; i < 100; i++) w.update(0.016, 0, false);
        assertTrue(w.isDefeat());
        assertEquals(0, w.getStars());
    }

    @Test
    void frozenHeroCannotMove() {
        Hero hero = new Hero(100);
        hero.setState(new FrozenState());
        LevelWorld w = world(hero, 1);
        w.update(0.1, 1, false);
        assertEquals(150, w.getHeroX(), 0.001);
    }
}