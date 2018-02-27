package it.teamunivr.ciabatte;

import it.teamunivr.ciabatte.model.Prestito;
import it.teamunivr.ciabatte.view.CiabatteController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class CiabatteUnivr extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;

    private ObservableList<Prestito> prestiti;
    private ObservableList<String> powerStripTypes;
    private ObservableList<Integer> powerStripNumbers;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Ciabatte Univr");
        this.primaryStage.setResizable(false);
        this.prestiti = FXCollections.observableArrayList();
        this.powerStripTypes = FXCollections.observableArrayList();
        this.powerStripNumbers = FXCollections.observableArrayList();

        powerStripTypes.addAll("Ciabatta", "Prolunga", "Altro");
        powerStripNumbers.addAll(0, 1, 2, 3, 4, 5, 6);
        
        // loads JSON data file
        LoanSave.init();

        initRootLayout();
    }

    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CiabatteUnivr.class.getResource("view/CiabatteView.fxml"));
            rootLayout = loader.load();

            CiabatteController controller = loader.getController();
            controller.setApp(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Prestito> getPrestiti() {
        return prestiti;
    }

    public ObservableList<String> getPowerStripTypes() {
        return powerStripTypes;
    }

    public ObservableList<Integer> getPowerStripNumbers() {
        return powerStripNumbers;
    }
}
