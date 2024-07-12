package fr.inria.corese.view.data;

import fr.inria.corese.view.DataView;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The {@link RdfContentView} class provides a view for displaying RDF content with hyperlinks
 * to various RDF and OWL resources. It allows users to interact with these resources
 * through a graphical interface.
 */
public class RdfContentView {

    /**
     * Creates the content view for displaying RDF content.
     *
     * @param dataView the DataView instance used in the application
     * @return the {@link VBox} containing the RDF content UI
     */
    public VBox createRdfContent(DataView dataView) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        VBox.setVgrow(vbox, Priority.ALWAYS);
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add("custom-scrollPane");

        CheckBox rdfsSubset = createHyperlinkCheckBox("RDFS Subset", "https://www.w3.org/TR/rdf-mt/");
        CheckBox rdfsRl = createHyperlinkCheckBox("RDFS RL", "https://owl-rl.readthedocs.io/en/latest/");
        CheckBox owlRl = createHyperlinkCheckBox("OWL RL", "https://owl-rl.readthedocs.io/en/latest/");
        CheckBox owlRlExtended = createHyperlinkCheckBox("OWL RL Extended", "https://owl-rl.readthedocs.io/en/latest/stubs/owlrl.html");
        CheckBox owlClean = createHyperlinkCheckBox("OWL Clean", "https://ebooks.iospress.nl/publication/3236");

        VBox rdfsContent = new VBox(rdfsSubset, rdfsRl);
        TitledPane rdfsPane = new TitledPane("RDFS", rdfsContent);
        rdfsPane.getStyleClass().add("titled-pane");

        VBox owlContent = new VBox(owlRl, owlRlExtended, owlClean);
        TitledPane owlPane = new TitledPane("OWL", owlContent);
        owlPane.getStyleClass().add("titled-pane");

        VBox titledPanesContainer = new VBox(rdfsPane, owlPane);

        scrollPane.setContent(titledPanesContainer);

        vbox.getChildren().add(scrollPane);

        return vbox;
    }

    /* Create HyperText for rules */

    /**
     * Creates a {@link CheckBox} with a hyperlink that opens in the default browser on right-click.
     *
     * @param text the text to display on the {@link CheckBox}
     * @param url  the URL to open on right-click
     *
     * @return the {@link CheckBox} with the hyperlink functionality
     *
     * @see CheckBox
     */
    public CheckBox createHyperlinkCheckBox(String text, String url) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.getStyleClass().add("check-box");

        Tooltip tooltip = new Tooltip("Right click");
        tooltip.setStyle("-fx-font-size: 14;");
        Tooltip.install(checkBox, tooltip);

        checkBox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        return checkBox;
    }
}
