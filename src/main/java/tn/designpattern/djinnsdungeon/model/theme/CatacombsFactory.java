package tn.designpattern.djinnsdungeon.model.theme;

import tn.designpattern.djinnsdungeon.model.composite.Combatant;
import tn.designpattern.djinnsdungeon.model.composite.Enemy;

public class CatacombsFactory implements ThemeFactory {
    @Override public String getThemeName() { return "Catacombes"; }
    @Override public Combatant createEnemy() { return new Skeleton(); }
    @Override public Trap createTrap() { return new SimpleTrap("Malédiction", 8); }
    @Override public Decor createDecor() { return new SimpleDecor("Cryptes"); }
}