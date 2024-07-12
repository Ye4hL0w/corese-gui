package fr.inria.corese.view.data;

import fr.inria.corese.controller.DataController;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * The {@link StatsContentView} class provides a view for displaying various statistical data
 * related to semantic elements, triplets, graphs, and rules.
 */
public class StatsContentView {

    DataController dataController = new DataController();

    /**
     * Creates the content view for displaying statistical data.
     *
     * @return the {@link VBox} containing the statistics content
     */
    public VBox createStatsContent() {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        int sementicElements = dataController.getNumberOfSemanticElements();
        int triplet = dataController.getNumberOfTriplets();
        int graph = dataController.getNumberOfGraphs();
        int rules = dataController.getNumberOfRules();

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
