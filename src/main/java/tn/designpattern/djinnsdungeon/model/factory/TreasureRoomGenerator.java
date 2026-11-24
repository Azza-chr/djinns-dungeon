package tn.designpattern.djinnsdungeon.model.factory;

import tn.designpattern.djinnsdungeon.model.decorator.FireDjinn;
import tn.designpattern.djinnsdungeon.model.decorator.Weapon;
import tn.designpattern.djinnsdungeon.model.theme.ThemeFactory;

import java.util.function.UnaryOperator;

public class TreasureRoomGenerator extends RoomGenerator {
    private final UnaryOperator<Weapon> reward;

    public TreasureRoomGenerator() { this(FireDjinn::new); }

    public TreasureRoomGenerator(UnaryOperator<Weapon> reward) { this.reward = reward; }

    @Override
    protected Room createRoom(ThemeFactory theme) { return new TreasureRoom(theme, reward); }
}