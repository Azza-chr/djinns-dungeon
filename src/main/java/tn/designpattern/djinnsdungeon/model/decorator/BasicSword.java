package tn.designpattern.djinnsdungeon.model.decorator;

public class BasicSword implements Weapon {
    @Override
    public int getDamage() {
        return 10;
    }

    @Override
    public String getDescription() {
        return "Épée";
    }
}