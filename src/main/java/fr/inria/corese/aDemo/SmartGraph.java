package fr.inria.corese.aDemo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SmartGraph extends Application {
    
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);

        Group root = new Group();
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

        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/smartgraph.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                System.out.println("Properties file not found, using default values.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Test Framework SmartGraph :
        Digraph<String, String> graph = new DigraphEdgeList<>();

        Vertex<String> vertexA = graph.insertVertex("ex:Alice");
        Vertex<String> vertexB = graph.insertVertex("ex:Person");
        Vertex<String> vertexC = graph.insertVertex("ex:KitKat");
        Vertex<String> D = graph.insertVertex("D");
        Vertex<String> E = graph.insertVertex("E");
        Vertex<String> F = graph.insertVertex("F");
        Vertex<String> G = graph.insertVertex("G");
        Vertex<String> H = graph.insertVertex("H");
        Vertex<String> I = graph.insertVertex("I");
        Vertex<String> J = graph.insertVertex("J");
        Vertex<String> K = graph.insertVertex("K");

        graph.insertEdge(vertexA, vertexB, "rdf:type");
        graph.insertEdge(vertexA, vertexC, "ex:worksFor");
        graph.insertEdge(vertexB, D, "D");
        graph.insertEdge(vertexB, E, "E");
        graph.insertEdge(vertexA, F, "F");
        graph.insertEdge(vertexA, G, "G");
        graph.insertEdge(vertexC, H, "H");
        graph.insertEdge(vertexB, I, "I");
        graph.insertEdge(vertexB, J, "J");
        graph.insertEdge(vertexC, K, "K");

        // test with many vertex
        // for (int i = 0; i < 10; i++) {
        //     Vertex<String> v = graph.insertVertex("Vertex_" + i);
        //     graph.insertEdge(vertexB, v, "none" + i);
        // }

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(graph, strategy);
        graphView.setPrefSize(1900, 1060);

        String css = getClass().getResource("/smartgraph/smartgraph.css").toExternalForm();
        scene.getStylesheets().add(css);

        graphView.getStylableVertex("ex:Alice").setStyleClass("myVertex");
        root.getChildren().add(graphView);

        primaryStage.setScene(scene);
        primaryStage.show();

        graphView.init();
        graphView.setAutomaticLayout(true);
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
