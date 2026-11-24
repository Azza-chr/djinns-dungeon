package tn.designpattern.djinnsdungeon.model.factory;

import tn.designpattern.djinnsdungeon.model.theme.ThemeFactory;

public class BossRoomGenerator extends RoomGenerator {
    @Override
    protected Room createRoom(ThemeFactory theme) { return new BossRoom(theme); }
}