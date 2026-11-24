module tn.designpattern.djinnsdungeon {
    requires javafx.controls;
    requires javafx.fxml;

    opens tn.designpattern.djinnsdungeon to javafx.fxml;
    exports tn.designpattern.djinnsdungeon;

    exports tn.designpattern.djinnsdungeon.model.composite;
    opens tn.designpattern.djinnsdungeon.model.composite to org.junit.platform.commons;

    exports tn.designpattern.djinnsdungeon.model.decorator;
    opens tn.designpattern.djinnsdungeon.model.decorator to org.junit.platform.commons;

    exports tn.designpattern.djinnsdungeon.model.state;
    opens tn.designpattern.djinnsdungeon.model.state to org.junit.platform.commons;

    exports tn.designpattern.djinnsdungeon.model.theme;
    opens tn.designpattern.djinnsdungeon.model.theme to org.junit.platform.commons;


}