package tn.designpattern.djinnsdungeon.model.factory;

import tn.designpattern.djinnsdungeon.model.theme.ThemeFactory;

public abstract class RoomGenerator {

    /** La Factory Method : chaque sous-classe choisit la salle à créer. */
    protected abstract Room createRoom(ThemeFactory theme);

    public Room prepareRoom(ThemeFactory theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Le thème ne peut pas être null");
        }
        return createRoom(theme);
    }
}