package fr.inria.corese.view;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SettingsView {
    public VBox getView() {
        VBox view = new VBox(new Label("Settings View"));
        return view;
    }
}
