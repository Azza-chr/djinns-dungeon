package tn.designpattern.djinnsdungeon.model.decorator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WeaponTest {

    @Test
    void basicSwordHasBaseDamage() {
        Weapon w = new BasicSword();
        assertEquals(10, w.getDamage());
        assertEquals("Épée", w.getDescription());
    }

    @Test
    void fireAddsDamageAndName() {
        Weapon w = new FireDjinn(new BasicSword());
        assertEquals(15, w.getDamage());
        assertEquals("Épée + Feu", w.getDescription());
    }

    @Test
    void djinnsStackAtRuntime() {
        Weapon w = new FireDjinn(new IceDjinn(new BasicSword()));
        assertEquals(18, w.getDamage());
        assertEquals("Épée + Glace + Feu", w.getDescription());
    }

    @Test
    void allFourDjinnsStack() {
        Weapon w = new LifeStealDjinn(new PoisonDjinn(new IceDjinn(new FireDjinn(new BasicSword()))));
        assertEquals(24, w.getDamage());
        assertEquals("Épée + Feu + Glace + Poison + Vol de vie", w.getDescription());
    }

    @Test
    void sameDjinnCanBeStackedTwice() {
        Weapon w = new FireDjinn(new FireDjinn(new BasicSword()));
        assertEquals(20, w.getDamage());
    }

    @Test
    void orderChangesDescriptionButNotDamage() {
        Weapon a = new FireDjinn(new IceDjinn(new BasicSword()));
        Weapon b = new IceDjinn(new FireDjinn(new BasicSword()));
        assertEquals(a.getDamage(), b.getDamage());
        assertNotEquals(a.getDescription(), b.getDescription());
    }

    @Test
    void nullWeaponIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FireDjinn(null));
    }
}