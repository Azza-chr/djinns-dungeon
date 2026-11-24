module tn.designpattern.djinnsdungeon {
    requires javafx.controls;
    requires javafx.fxml;

    opens tn.designpattern.djinnsdungeon to javafx.fxml;
    exports tn.designpattern.djinnsdungeon;

    exports tn.designpattern.djinnsdungeon.model.composite;
    opens tn.designpattern.djinnsdungeon.model.composite to org.junit.platform.commons;
}