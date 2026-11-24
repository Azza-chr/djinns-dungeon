package tn.designpattern.djinnsdungeon.model.theme;

import org.junit.jupiter.api.Test;
import tn.designpattern.djinnsdungeon.model.composite.Combatant;
import static org.junit.jupiter.api.Assertions.*;

class ThemeFactoryTest {

    @Test
    void desertFamilyIsCoherent() {
        ThemeFactory f = new DesertFactory();
        assertEquals("Désert", f.getThemeName());
        assertEquals("Scorpion", f.createEnemy().getName());
        assertEquals("Sables mouvants", f.createTrap().getName());
        assertEquals("Dunes", f.createDecor().getName());
    }

    @Test
    void medinaFamilyIsCoherent() {
        ThemeFactory f = new MedinaFactory();
        assertEquals("Médina", f.getThemeName());
        assertEquals("Garde", f.createEnemy().getName());
        assertEquals("Piège de souk", f.createTrap().getName());
        assertEquals("Ruelles du souk", f.createDecor().getName());
    }

    @Test
    void catacombsFamilyIsCoherent() {
        ThemeFactory f = new CatacombsFactory();
        assertEquals("Catacombes", f.getThemeName());
        assertEquals("Squelette", f.createEnemy().getName());
        assertEquals("Malédiction", f.createTrap().getName());
        assertEquals("Cryptes", f.createDecor().getName());
    }

    @Test
    void themesHaveIncreasingTrapDamage() {
        assertTrue(new DesertFactory().createTrap().getDamage()
                < new MedinaFactory().createTrap().getDamage());
        assertTrue(new MedinaFactory().createTrap().getDamage()
                < new CatacombsFactory().createTrap().getDamage());
    }

    @Test
    void eachCallCreatesANewEnemy() {
        ThemeFactory f = new MedinaFactory();
        Combatant a = f.createEnemy();
        Combatant b = f.createEnemy();
        assertNotSame(a, b);
        a.takeDamage(10);
        assertEquals(30, b.getHp());
    }

    @Test
    void clientWorksWithoutKnowingTheTheme() {
        ThemeFactory[] themes = { new DesertFactory(), new MedinaFactory(), new CatacombsFactory() };
        for (ThemeFactory f : themes) {
            assertTrue(f.createEnemy().isAlive());
            assertTrue(f.createTrap().getDamage() > 0);
            assertNotNull(f.createDecor().getName());
        }
    }
}