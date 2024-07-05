package fr.inria.corese.view.data;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class StatsContentView {
    public VBox getView() {
        return createStatsContent();
    }

    public VBox createStatsContent() {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        int sementicElements = 0;
        int triplet = 0;
        int graph = 0;
        int rules = 0;

        Label label1 = new Label("Semantic elements loaded: " + sementicElements);
        label1.setFont(new Font("Arial", 26));

        Label label2 = new Label("Number of triplet: " + triplet);
        label2.setFont(new Font("Arial", 26));

        Label label3 = new Label("Number of graph: " + graph);
        label3.setFont(new Font("Arial", 26));

        Label label4 = new Label("Number of rules loaded: " + rules);
        label4.setFont(new Font("Arial", 26));

        vbox.getChildren().addAll(label1, label2, label3, label4);

        return vbox;
    }
}
