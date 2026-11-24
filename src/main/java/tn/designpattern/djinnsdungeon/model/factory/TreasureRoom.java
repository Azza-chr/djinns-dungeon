package tn.designpattern.djinnsdungeon.model.factory;

import tn.designpattern.djinnsdungeon.model.decorator.Weapon;
import tn.designpattern.djinnsdungeon.model.theme.ThemeFactory;

import java.util.List;
import java.util.function.UnaryOperator;

public class TreasureRoom extends Room {
    private final UnaryOperator<Weapon> reward;

    public TreasureRoom(ThemeFactory theme, UnaryOperator<Weapon> reward) {
        super("Salle au trésor - " + theme.getThemeName(),
                theme.createDecor(), null, List.of());
        this.reward = reward;
    }

    /** Enveloppe l'arme avec un djinn (patron Decorator). */
    public Weapon applyReward(Weapon weapon) {
        return reward.apply(weapon);
    }
}