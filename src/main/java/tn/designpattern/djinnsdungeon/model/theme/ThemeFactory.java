package tn.designpattern.djinnsdungeon.model.theme;

import tn.designpattern.djinnsdungeon.model.composite.Combatant;

public interface ThemeFactory {
    String getThemeName();
    Combatant createEnemy();
    Trap createTrap();
    Decor createDecor();
}