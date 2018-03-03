package it.teamunivr.powerstrips;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class UnivrPowerStripManager extends Application {

    private Stage primaryStage;
    private GridPane rootLayout;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Ciabatte Univr");
        this.primaryStage.setMaxWidth(900);
        this.primaryStage.setMaxHeight(600);
        this.primaryStage.setMinHeight(400);
        this.primaryStage.setMinWidth(600);

        primaryStage.getIcons().add(new Image(UnivrPowerStripManager.class.getResourceAsStream("resources/icon.png")));


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
}
