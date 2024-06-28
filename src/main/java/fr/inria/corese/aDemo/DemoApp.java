
package fr.inria.corese.aDemo;

import io.github.palexdev.mfxcore.controls.Label;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DemoApp extends Application {

    private boolean isDarkMode = false;

    public static void lancement(String[] args) {
        DemoApp.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Corese");
        primaryStage.setMinWidth(1920);
        primaryStage.setMinHeight(1080);

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());

        Text welcomeText = new Text("Welcome to CORESE");
        welcomeText.setFont(new Font(40));

        root.setCenter(welcomeText);

        VBox vbCenter = new VBox();
        vbCenter.setAlignment(Pos.CENTER);
        vbCenter.setSpacing(10);

        VBox vbTitre = new VBox();
        vbTitre.setAlignment(Pos.CENTER);
        vbTitre.setPadding(new Insets(0,0,80,0));

        Label title = new Label("Design Element in JavaFx for Corese");
        title.setFont(new Font(40));

        vbTitre.getChildren().add(title);

        Button bSmartGraph = new Button("SMARTGRAPH");
        bSmartGraph.setPrefWidth(300);
        bSmartGraph.setPrefHeight(50);
        bSmartGraph.setOnMouseClicked(e -> {
            SmartGraph smartGraph = new SmartGraph();
            try {
                smartGraph.start(primaryStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        bSmartGraph.setFocusTraversable(false);

        Button bMaterialDesignIcons = new Button("MATERIAL DESIGN ICONS");
        bMaterialDesignIcons.setPrefWidth(300);
        bMaterialDesignIcons.setPrefHeight(50);
        bMaterialDesignIcons.setOnMouseClicked(e -> {
            MaterialDesignIcons materialDesignIcons = new MaterialDesignIcons();
            try {
                materialDesignIcons.start(primaryStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        bMaterialDesignIcons.setFocusTraversable(false);

        Button bMaterialFx = new Button("MATERIALFX");
        bMaterialFx.setPrefWidth(300);
        bMaterialFx.setPrefHeight(50);
        bMaterialFx.setOnMouseClicked(e -> {
            MaterialFx materialFx = new MaterialFx();
            try {
                materialFx.start(primaryStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        bMaterialFx.setFocusTraversable(false);

        Button themeSwitchButton = new Button("DARK/LIGHT");
        themeSwitchButton.setPrefWidth(300);
        themeSwitchButton.setPrefHeight(50);
        themeSwitchButton.setOnAction(e -> {
            isDarkMode = !isDarkMode;
            applyTheme(scene);
        });
        themeSwitchButton.setFocusTraversable(false);


        vbCenter.getChildren().addAll(vbTitre, bSmartGraph, bMaterialDesignIcons, bMaterialFx, themeSwitchButton);

        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), welcomeText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            root.getChildren().remove(welcomeText);
            root.setCenter(vbCenter);
        });

        fadeOut.play();
    }

    private void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(DemoApp.class.getResource("/themes/dark-theme.css").toExternalForm());
        } else {
            scene.getStylesheets().add(DemoApp.class.getResource("/themes/light-theme.css").toExternalForm());
        }
    }
    

}
