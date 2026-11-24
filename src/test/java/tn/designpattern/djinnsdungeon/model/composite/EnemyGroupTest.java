package tn.designpattern.djinnsdungeon.model.composite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EnemyGroupTest {

    /** Ennemi minimal, uniquement pour les tests (les vrais arrivent a l'etape 4). */
    private static class DummyEnemy extends Enemy {
        DummyEnemy(String name, int hp) {
            super(name, hp, 1);
        }
    }

    @Test
    void unCoupSurLeGroupeTouchePlusieursMembres() {
        DummyEnemy a = new DummyEnemy("A", 30);
        DummyEnemy b = new DummyEnemy("B", 30);
        DummyEnemy c = new DummyEnemy("C", 30);
        EnemyGroup horde = new EnemyGroup("Horde");
        horde.add(a);
        horde.add(b);
        horde.add(c);

        horde.takeDamage(10);   // UN seul appel

        assertEquals(20, a.getHp());
        assertEquals(20, b.getHp());
        assertEquals(20, c.getHp());
    }

    @Test
    void laVieDuGroupeEstLaSommeDesMembres() {
        EnemyGroup horde = new EnemyGroup("Horde");
        horde.add(new DummyEnemy("A", 30));
        horde.add(new DummyEnemy("B", 20));
        assertEquals(50, horde.getHp());
    }

    @Test
    void leGroupeResteVivantTantQuUnMembreVit() {
        DummyEnemy faible = new DummyEnemy("Faible", 5);
        DummyEnemy fort = new DummyEnemy("Fort", 50);
        EnemyGroup horde = new EnemyGroup("Horde");
        horde.add(faible);
        horde.add(fort);

        horde.takeDamage(10);

        assertFalse(faible.isAlive());
        assertTrue(fort.isAlive());
        assertTrue(horde.isAlive());
    }

    @Test
    void leGroupeEstMortQuandTousLesMembresSontMorts() {
        EnemyGroup horde = new EnemyGroup("Horde");
        horde.add(new DummyEnemy("A", 10));
        horde.add(new DummyEnemy("B", 10));

        horde.takeDamage(10);

        assertFalse(horde.isAlive());
        assertEquals(0, horde.getHp());
    }

    @Test
    void unGroupePeutContenirUnGroupe() {
        DummyEnemy a = new DummyEnemy("A", 40);
        DummyEnemy b = new DummyEnemy("B", 40);
        EnemyGroup petit = new EnemyGroup("Petit groupe");
        petit.add(b);
        EnemyGroup grand = new EnemyGroup("Grand groupe");
        grand.add(a);
        grand.add(petit);   // un groupe DANS un groupe

        grand.takeDamage(15);

        assertEquals(25, a.getHp());
        assertEquals(25, b.getHp());
    }

    @Test
    void retirerUnMembreLeSortDuGroupe() {
        DummyEnemy a = new DummyEnemy("A", 30);
        EnemyGroup horde = new EnemyGroup("Horde");
        horde.add(a);
        horde.remove(a);

        horde.takeDamage(10);

        assertEquals(30, a.getHp());   // a n'a plus ete touche
    }

    @Test
    void lesDegatsNegatifsSontRefuses() {
        EnemyGroup horde = new EnemyGroup("Horde");
        assertThrows(IllegalArgumentException.class, () -> horde.takeDamage(-5));
    }

    @Test
    void unGroupeNePeutPasSeContenirLuiMeme() {
        EnemyGroup horde = new EnemyGroup("Horde");
        assertThrows(IllegalArgumentException.class, () -> horde.add(horde));
    }
}