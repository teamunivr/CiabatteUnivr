package it.teamunivr.ciabatte;

import it.teamunivr.ciabatte.model.Loan;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class UnivrPowerStripManager extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;

    private ObservableList<Loan> loans;
    private ObservableList<String> powerStripTypes;
    private ObservableList<String> powerStripNumbers;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Ciabatte Univr");
        this.primaryStage.setResizable(false);
        this.loans = FXCollections.observableArrayList();
        this.powerStripTypes = FXCollections.observableArrayList();
        this.powerStripNumbers = FXCollections.observableArrayList();

        //aggiungi spiegazione
        
        // loads JSON data file
        LoanSave.init();

        initRootLayout();
    }

    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(UnivrPowerStripManager.class.getResource("view/MainView.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Loan> getLoans() {
        return loans;
    }

    public ObservableList<String> getPowerStripTypes() {
        return powerStripTypes;
    }

    public ObservableList<Integer> getPowerStripNumbers() {
        //return powerStripNumbers;
        return null;
    }
}
