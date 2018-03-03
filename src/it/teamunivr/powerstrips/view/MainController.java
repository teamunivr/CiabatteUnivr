package it.teamunivr.powerstrips.view;

import it.teamunivr.powerstrips.Config;
import it.teamunivr.powerstrips.model.Loan;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class MainController {
    @FXML
    private TableView<Loan> loansTable;

    @FXML
    private TableColumn<Loan, String> nameColumn;

    @FXML
    private TableColumn<Loan, String> lastNameColumn;
    @FXML
    private TableColumn<Loan, String> IDColumn;

    @FXML
    private ComboBox<String> comboBoxIDs;

    @FXML
    private ComboBox<String> comboBoxTypes;

    @FXML
    private TextField name;

    @FXML
    private TextField lastName;

    @FXML
    private Button preferences;

    @FXML
    private Pane mainPane;


    // Reference to the main application.
    private ObservableList<String> observableKeys;
    private ArrayList<ObservableList<String>> IDs;
    private ObservableList<Loan> loans;

    public MainController() {
        Map<String, ArrayList<String>> powerStripMap;
        observableKeys = FXCollections.observableArrayList();
        loans = FXCollections.observableArrayList(Config.getInstance().getLoanSave().getLoans());
        IDs = new ArrayList<>();

        try {
            powerStripMap = Config.getInstance().getPowerStrips();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        for (Map.Entry<String, ArrayList<String>> e : powerStripMap.entrySet()) {
            observableKeys.add(e.getKey());
            IDs.add(FXCollections.observableArrayList(e.getValue()));
            FXCollections.sort(IDs.get(observableKeys.indexOf(e.getKey())));
        }

        for (Loan l : loans) {
            String[] parts = l.getPowerStripID().split(": ");
            IDs.get(observableKeys.indexOf(parts[0])).remove(parts[1]);
        }
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded. N.B. this method is called before the main app methods
     */
    @FXML
    @SuppressWarnings("unchecked")
    private void initialize() {
        // Initialize the person table with the two columns.
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        IDColumn.setCellValueFactory(cellData -> cellData.getValue().powerStripIDProperty());


        //Insert Button
        TableColumn col_action = new TableColumn<>("");
        col_action.setStyle("-fx-alignment: CENTER-RIGHT");

        loansTable.getColumns().add(col_action);

        col_action.setCellValueFactory(
                (Callback<TableColumn.CellDataFeatures<Object, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));

        //Adding the Button to the cell
        col_action.setCellFactory(
                (Callback<TableColumn<Object, Boolean>, TableCell<Object, Boolean>>) p -> new ButtonCell());

        loansTable.setItems(loans);

        comboBoxTypes.setItems(observableKeys);

        comboBoxTypes.getSelectionModel().selectedItemProperty().addListener(
                (ChangeListener) (oL, oldValue, newValue) -> {
                    comboBoxIDs.setItems(IDs.get(observableKeys.indexOf(newValue)));
                    comboBoxIDs.getSelectionModel().selectFirst();
                }
        );

        comboBoxTypes.getSelectionModel().selectFirst();

        preferences.setGraphic(
                new ImageView(new Image(getClass().getResourceAsStream("/it/teamunivr/powerstrips/resources/preferences-icon.png")))
        );

    }

    @FXML
    public void onAddButton() {
        if (name.getText().isEmpty() || lastName.getText().isEmpty()) {
            name.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ;");
            lastName.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ;");
            return;
        } else {
            name.setStyle(null);
            lastName.setStyle(null);
        }

        if (comboBoxTypes.getValue() == null || comboBoxIDs.getValue() == null) {
            return;
        }

        Loan tmp = new Loan(name.getText(), lastName.getText(),
                String.format("%s: %s", comboBoxTypes.getValue(), comboBoxIDs.getValue()));

        loans.add(tmp);

        Config.getInstance().getLoanSave().addEntry(tmp);

        System.out.println(String.format("Type: %s, ID: %s", comboBoxTypes.getValue(), comboBoxIDs.getValue()));

        IDs.get(observableKeys.indexOf(comboBoxTypes.getValue())).remove(comboBoxIDs.getValue());

        comboBoxIDs.getSelectionModel().selectFirst();
    }

    //Define the button cell
    private class ButtonCell extends TableCell<Object, Boolean> {
        final Button cellButton = new Button("Rientra");

        ButtonCell() {

            //Action when the button is pressed
            cellButton.setOnAction(t -> {
                        Loan l = (Loan) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                        loans.remove(l);
                        String[] parts = l.getPowerStripID().split(": ");

                        Config.getInstance().getLoanSave().removeEntry(l);

                        IDs.get(observableKeys.indexOf(parts[0])).add(parts[1]);
                        FXCollections.sort(IDs.get(observableKeys.indexOf(parts[0])));

                comboBoxIDs.getSelectionModel().selectFirst();
                    }
            );
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(cellButton);
            } else {
                setGraphic(null);
            }
        }
    }

    @FXML
    private void onPreferences() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("PreferenceDialog.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 300, 200);
            Stage stage = new Stage();

            stage.initOwner(mainPane.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Preferences");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
