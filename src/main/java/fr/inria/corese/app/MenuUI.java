package fr.inria.corese.app;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;

/**
 * The {@link MenuUI} class creates and manages the user interface for the application menu.
 * <p>
 * It provides a vertical menu with buttons for various application views such as Data, RDF Editor, Validation, Query, and Settings.
 * Each button is associated with an icon and a specific action that triggers the display of the corresponding view in the application.
 * </p>
 */
public class MenuUI {
    private boolean isMenuExpanded = true;
    private Button activeButton;
    private VBox vbLeft;
    private VBox menuBox;

    /**
     * Constructs a new {@link MenuUI} instance with the specified {@link App} instance.
     * <p>
     * This constructor initializes the menu with buttons for different views in the application and sets up their icons and actions.
     * </p>
     *
     * @param app the {@link App} instance used to display different views of the application.
     */
    public MenuUI(App app) {
        vbLeft = new VBox();
        vbLeft.setAlignment(Pos.TOP_LEFT);
        vbLeft.setPadding(new Insets(20,0,0,0));
        vbLeft.setSpacing(10);

//        Button menuButton = new Button();
//        FontIcon menuIcon = new FontIcon(MaterialDesignM.MENU);
//        menuIcon.setIconSize(24);
//        menuButton.setGraphic(menuIcon);

        Button dataButton = new Button("Data");
        dataButton.setPrefWidth(175);
        dataButton.setMaxWidth(175);
        dataButton.setAlignment(Pos.CENTER_LEFT);
        FontIcon dataIcon = new FontIcon(MaterialDesignD.DATABASE);
        dataIcon.setIconSize(34);
        dataButton.setGraphic(dataIcon);
        dataButton.setOnAction(e -> {
            app.showDataView();
            setActiveButton(dataButton);
        });
        setActiveButton(dataButton);

        Button rdfEditorButton = new Button("RDF Editor");
        rdfEditorButton.setPrefWidth(175);
        rdfEditorButton.setMaxWidth(175);
        rdfEditorButton.setAlignment(Pos.CENTER_LEFT);
        FontIcon rdfEditorIcon = new FontIcon(MaterialDesignC.CODE_NOT_EQUAL_VARIANT);
        rdfEditorIcon.setIconSize(34);
        rdfEditorButton.setGraphic(rdfEditorIcon);
        rdfEditorButton.setOnAction(e -> {
            app.showRdfEditorView();
            setActiveButton(rdfEditorButton);
        });

        Button validationButton = new Button("Validation");
        validationButton.setPrefWidth(175);
        validationButton.setMaxWidth(175);
        validationButton.setAlignment(Pos.CENTER_LEFT);
        FontIcon validationIcon = new FontIcon(MaterialDesignC.CHECK_DECAGRAM);
        validationIcon.setIconSize(34);
        validationButton.setGraphic(validationIcon);
        validationButton.setOnAction(e -> {
            app.showValidationView();
            setActiveButton(validationButton);
        });

        Button queryButton = new Button("Query");
        queryButton.setPrefWidth(175);
        queryButton.setMaxWidth(175);
        queryButton.setAlignment(Pos.CENTER_LEFT);
        FontIcon queryIcon = new FontIcon(MaterialDesignM.MAGNIFY);
        queryIcon.setIconSize(34);
        queryButton.setGraphic(queryIcon);
        queryButton.setOnAction(e -> {
            app.showQueryView();
            setActiveButton(queryButton);
        });

        Button settingsButton = new Button("Settings");
        settingsButton.setPrefWidth(175);
        settingsButton.setMaxWidth(175);
        settingsButton.setAlignment(Pos.CENTER_LEFT);
        FontIcon settingsIcon = new FontIcon(MaterialDesignC.COG);
        settingsIcon.setIconSize(34);
        settingsButton.setGraphic(settingsIcon);
        settingsButton.setOnAction(e -> {
            app.showSettingsView();
            setActiveButton(settingsButton);
        });

        menuBox = new VBox(dataButton, rdfEditorButton, validationButton, queryButton, settingsButton);
        menuBox.setAlignment(Pos.TOP_LEFT);
        menuBox.setSpacing(10);

        vbLeft.getChildren().addAll(/*menuButton*/menuBox);

//        menuButton.setOnAction(e -> {
//            isMenuExpanded = !isMenuExpanded;
//            menuBox.setVisible(isMenuExpanded);
//        });

        String css = getClass().getResource("/style/custom-button.css").toExternalForm();
        menuBox.getStylesheets().add(css);

        dataButton.getStyleClass().add("custom-button");
        rdfEditorButton.getStyleClass().add("custom-button");
        validationButton.getStyleClass().add("custom-button");
        queryButton.getStyleClass().add("custom-button");
        settingsButton.getStyleClass().add("custom-button");
    }

    /**
     * Returns the {@link VBox} containing the menu buttons.
     *
     * @return the {@link VBox} instance that represents the {@link MenuUI}.
     */
    public VBox getMenu() {
        return vbLeft;
    }

    /**
     * Sets the specified button as the active button by updating its style.
     * <p>
     * The active button is highlighted with a specific style class, and any previously active button has its style updated to indicate it is no longer active.
     * </p>
     *
     * @param button the {@link Button} to be set as the active button.
     */
    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active-button");
        }
        activeButton = button;
        activeButton.getStyleClass().add("active-button");
    }
}
