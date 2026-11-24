package tn.designpattern.djinnsdungeon.model.theme;

import tn.designpattern.djinnsdungeon.model.composite.Combatant;
import tn.designpattern.djinnsdungeon.model.composite.Enemy;

public class MedinaFactory implements ThemeFactory {
    @Override public String getThemeName() { return "Médina"; }
    @Override public Combatant createEnemy() { return new Guard(); }
    @Override public Trap createTrap() { return new SimpleTrap("Piège de souk", 6); }
    @Override public Decor createDecor() { return new SimpleDecor("Ruelles du souk"); }
}