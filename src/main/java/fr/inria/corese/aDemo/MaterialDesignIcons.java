package fr.inria.corese.aDemo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.materialdesign2.*;


public class MaterialDesignIcons extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE:
                    afficherInterfaceRetourAccueil();
                    break;
                default:
                    break;
            }
        });


        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);

        Button button1 = new Button("Open project");
        FontIcon icon1 = new FontIcon(MaterialDesignF.FOLDER_OPEN);
        icon1.setIconSize(34);
        button1.setGraphic(icon1);
        Tooltip tooltip1 = new Tooltip("Open an existing project");
        button1.setTooltip(tooltip1);

        Button button2 = new Button("Save as");
        FontIcon icon2 = new FontIcon(MaterialDesignC.CONTENT_SAVE_ALL_OUTLINE);
        icon2.setIconSize(34);
        button2.setGraphic(icon2);
        Tooltip tooltip2 = new Tooltip("Save the project as a new file");
        button2.setTooltip(tooltip2);

        Button button3 = new Button("Run");
        FontIcon icon3 = new FontIcon(MaterialDesignP.PLAY);
        icon3.setIconSize(34);
        button3.setGraphic(icon3);
        Tooltip tooltip3 = new Tooltip("Run the project");
        button3.setTooltip(tooltip3);

        Button button8 = new Button("Show logs");
        FontIcon icon8 = new FontIcon(MaterialDesignH.HISTORY);
        icon8.setIconSize(34);
        button8.setGraphic(icon8);
        Tooltip tooltip8 = new Tooltip("Show the log history");
        button8.setTooltip(tooltip8);

        Button button4 = new Button("Load files");
        FontIcon icon4 = new FontIcon(MaterialDesign.MDI_DOWNLOAD);
        icon4.setIconSize(34);
        button4.setGraphic(icon4);
        Tooltip tooltip4 = new Tooltip("Load files into the project");
        button4.setTooltip(tooltip4);

        Button button5 = new Button("Reload files");
        FontIcon icon5 = new FontIcon(MaterialDesignC.CACHED);
        icon5.setIconSize(34);
        button5.setGraphic(icon5);
        Tooltip tooltip5 = new Tooltip("Reload the files");
        button5.setTooltip(tooltip5);

        Button button6 = new Button("Clear graph");
        FontIcon icon6 = new FontIcon(MaterialDesign.MDI_BROOM);
        icon6.setIconSize(34);
        button6.setGraphic(icon6);
        Tooltip tooltip6 = new Tooltip("Clear the current graph");
        button6.setTooltip(tooltip6);

        Button button7 = new Button("Load rule file");
        FontIcon icon7 = new FontIcon(MaterialDesign.MDI_DOWNLOAD);
        icon7.setIconSize(34);
        button7.setGraphic(icon7);
        Tooltip tooltip7 = new Tooltip("Load a rule file");
        button7.setTooltip(tooltip7);
        
        vbox.getChildren().addAll(button1,button2,button3,button8,button4,button5,button6,button7);

        root.setCenter(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void afficherInterfaceRetourAccueil() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("En Pause");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        Button retourAccueilButton = new Button("Retour à l'accueil");
        retourAccueilButton.setOnAction(e -> {
            stage.close();
            DemoApp vueAccueil = new DemoApp();
            try {
                vueAccueil.start(primaryStage);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        vbox.getChildren().add(retourAccueilButton);

        Scene scene = new Scene(vbox, 300, 200);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
