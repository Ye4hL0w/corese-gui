package fr.inria.corese.aDemo;

import java.util.Comparator;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTooltip;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MaterialFx extends Application {
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

        // Spinner
        MFXProgressSpinner mfxProgressSpinner = new MFXProgressSpinner();

        // Table
        MFXTableView<Person> mfxTableView = new MFXTableView<>();
        MFXTableColumn<Person> firstNameColumn = new MFXTableColumn<>("First Name", true,
                Comparator.comparing(Person::getFirstName));
        firstNameColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getFirstName));

        MFXTableColumn<Person> lastNameColumn = new MFXTableColumn<>("Last Name", true,
                Comparator.comparing(Person::getLastName));
        lastNameColumn.setRowCellFactory(person -> new MFXTableRowCell<>(Person::getLastName));

        MFXTableColumn<Person> ageColumn = new MFXTableColumn<>("Age", true, Comparator.comparingInt(Person::getAge));
        ageColumn.setRowCellFactory(person -> new MFXTableRowCell<>(p -> String.valueOf(p.getAge())));

        mfxTableView.getTableColumns().addAll(firstNameColumn, lastNameColumn, ageColumn);

        ObservableList<Person> data = FXCollections.observableArrayList(
                new Person("Jérémy", "Moncada", 30),
                new Person("Marty", "McFly", 25),
                new Person("Dark", "Vador", 40));
        mfxTableView.setItems(data);

        MFXTooltip mfxTooltip = new MFXTooltip(mfxProgressSpinner);

        vbox.getChildren().addAll(mfxProgressSpinner, mfxTableView);

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

    public static class Person {
        private final String firstName;
        private final String lastName;
        private final int age;

        public Person(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public int getAge() {
            return age;
        }
    }
}
