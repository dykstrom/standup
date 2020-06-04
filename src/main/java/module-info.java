module se.dykstrom.standup {
    requires com.google.gson;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.fxml;

    opens se.dykstrom.standup.gui to javafx.fxml;
    opens se.dykstrom.standup.model to com.google.gson;
    exports se.dykstrom.standup;
}