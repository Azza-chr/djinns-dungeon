package tn.designpattern.djinnsdungeon.model.theme;

import tn.designpattern.djinnsdungeon.model.composite.Combatant;
import tn.designpattern.djinnsdungeon.model.composite.Enemy;

public class DesertFactory implements ThemeFactory {
    @Override public String getThemeName() { return "Désert"; }
    @Override public Combatant createEnemy() { return new Scorpion(); }
    @Override public Trap createTrap() { return new SimpleTrap("Sables mouvants", 4); }
    @Override public Decor createDecor() { return new SimpleDecor("Dunes"); }
}