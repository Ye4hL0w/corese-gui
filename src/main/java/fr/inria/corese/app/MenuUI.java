package fr.inria.corese.app;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.materialdesign2.MaterialDesignM;

public class MenuUI {
    private boolean isMenuExpanded = true;
    private Button activeButton;
    private VBox vbLeft;
    private VBox menuBox;

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
        FontIcon dataIcon = new FontIcon(MaterialDesignD.DATABASE);
        dataIcon.setIconSize(34);
        dataButton.setGraphic(dataIcon);
        dataButton.setOnAction(e -> {
            app.showDataView();
            setActiveButton(dataButton);
        });
        setActiveButton(dataButton);

        Button rdfEditorButton = new Button("RDF Editor");
        FontIcon rdfEditorIcon = new FontIcon(MaterialDesignC.CODE_NOT_EQUAL_VARIANT);
        rdfEditorIcon.setIconSize(34);
        rdfEditorButton.setGraphic(rdfEditorIcon);
        rdfEditorButton.setOnAction(e -> {
            app.showRdfEditorView();
            setActiveButton(rdfEditorButton);
        });

        Button validationButton = new Button("Validation");
        FontIcon validationIcon = new FontIcon(MaterialDesignC.CHECK_DECAGRAM);
        validationIcon.setIconSize(34);
        validationButton.setGraphic(validationIcon);
        validationButton.setOnAction(e -> {
            app.showValidationView();
            setActiveButton(validationButton);
        });

        Button queryButton = new Button("Query");
        FontIcon queryIcon = new FontIcon(MaterialDesignM.MAGNIFY);
        queryIcon.setIconSize(34);
        queryButton.setGraphic(queryIcon);
        queryButton.setOnAction(e -> {
            app.showQueryView();
            setActiveButton(queryButton);
        });

        Button settingsButton = new Button("Settings");
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

    public VBox getMenu() {
        return vbLeft;
    }

    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active-button");
        }
        activeButton = button;
        activeButton.getStyleClass().add("active-button");
    }
}
