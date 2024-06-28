package fr.inria.corese.view;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class QueryView {
    public VBox getView() {
        VBox view = new VBox(new Label("Query View"));
        return view;
    }
}
