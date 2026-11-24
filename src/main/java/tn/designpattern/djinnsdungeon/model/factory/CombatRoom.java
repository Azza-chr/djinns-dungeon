package tn.designpattern.djinnsdungeon.model.factory;

import tn.designpattern.djinnsdungeon.model.theme.ThemeFactory;
import java.util.List;

public class CombatRoom extends Room {
    public CombatRoom(ThemeFactory theme) {
        super("Salle de combat - " + theme.getThemeName(),
                theme.createDecor(),
                theme.createTrap(),
                List.of(theme.createEnemy(), theme.createEnemy()));
    }
}