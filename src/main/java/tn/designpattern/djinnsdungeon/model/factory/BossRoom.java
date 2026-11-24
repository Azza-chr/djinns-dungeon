package tn.designpattern.djinnsdungeon.model.factory;

import tn.designpattern.djinnsdungeon.model.theme.ThemeFactory;
import java.util.List;

public class BossRoom extends Room {
    public BossRoom(ThemeFactory theme) {
        super("Salle du boss - " + theme.getThemeName(),
                theme.createDecor(), theme.createTrap(), List.of(new Boss()));
    }
}