package tn.designpattern.djinnsdungeon.model.factory;

import tn.designpattern.djinnsdungeon.model.composite.Enemy;

public class Boss extends Enemy {
    public Boss() { super("Djinn maudit", 60, 10); }
}