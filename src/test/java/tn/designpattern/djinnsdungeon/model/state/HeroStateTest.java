package tn.designpattern.djinnsdungeon.model.state;

import org.junit.jupiter.api.Test;
import tn.designpattern.djinnsdungeon.model.decorator.FireDjinn;
import static org.junit.jupiter.api.Assertions.*;

class HeroStateTest {

    @Test
    void heroStartsNormal() {
        Hero h = new Hero(100);
        assertEquals("Normal", h.getState().getName());
        assertEquals(10, h.attackDamage());
    }

    @Test
    void poisonedHeroLosesHpEachTurn() {
        Hero h = new Hero(100);
        h.setState(new PoisonedState());
        h.startTurn();
        assertEquals(95, h.getHp());
        h.startTurn();
        assertEquals(90, h.getHp());
    }

    @Test
    void poisonWearsOffAfterTwoTurns() {
        Hero h = new Hero(100);
        h.setState(new PoisonedState());
        h.startTurn();
        h.startTurn();
        assertEquals("Normal", h.getState().getName());
        h.startTurn();
        assertEquals(90, h.getHp());
    }

    @Test
    void frozenHeroCannotAttack() {
        Hero h = new Hero(100);
        h.setState(new FrozenState());
        assertEquals(0, h.attackDamage());
    }

    @Test
    void frozenHeroRecoversAfterTwoTurns() {
        Hero h = new Hero(100);
        h.setState(new FrozenState());
        h.startTurn();
        h.startTurn();
        assertEquals("Normal", h.getState().getName());
        assertEquals(10, h.attackDamage());
    }

    @Test
    void enragedHeroHitsHarderAndTakesMore() {
        Hero h = new Hero(100);
        h.setState(new EnragedState());
        assertEquals(15, h.attackDamage());
        h.takeDamage(10);
        assertEquals(85, h.getHp());
    }

    @Test
    void hpNeverGoesBelowZero() {
        Hero h = new Hero(3);
        h.setState(new PoisonedState());
        h.startTurn();
        assertEquals(0, h.getHp());
        assertFalse(h.isAlive());
    }

    @Test
    void stateAndDecoratorWorkTogether() {
        Hero h = new Hero(100);
        h.setWeapon(new FireDjinn(new tn.designpattern.djinnsdungeon.model.decorator.BasicSword()));
        h.setState(new EnragedState());
        assertEquals(22, h.attackDamage());
    }

    @Test
    void invalidMaxHpIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Hero(0));
    }
}