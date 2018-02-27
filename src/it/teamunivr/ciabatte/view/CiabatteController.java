package it.teamunivr.ciabatte.view;

import it.teamunivr.ciabatte.CiabatteUnivr;
import it.teamunivr.ciabatte.LoanSave;
import it.teamunivr.ciabatte.model.Prestito;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.sun.prism.impl.Disposer.Record;
import javafx.util.Callback;

public class CiabatteController {
    @FXML
    private TableView<Prestito> tabellaPrestiti;

    @FXML
    private TableColumn<Prestito, String> colonnaNome;

    @FXML
    private TableColumn<Prestito, String> colonnaCognome;
    @FXML
    private TableColumn<Prestito, String> colonnaNumero;

    @FXML
    private ComboBox<Integer> comboBoxNumeriCiabatte;

    @FXML
    private ComboBox<String> comboBoxTipologiaCiabatte;

    @FXML
    private TextField nome;

    @FXML
    private TextField cognome;

    @FXML
    private Button aggiungi;


    // Reference to the main application.
    private CiabatteUnivr app;

    public CiabatteController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded. N.B. this method is called before the main app methods
     */
    @FXML
    @SuppressWarnings("unchecked")
    private void initialize() {
        // Initialize the person table with the two columns.
        colonnaNome.setCellValueFactory(cellData -> cellData.getValue().nomeProperty());
        colonnaCognome.setCellValueFactory(cellData -> cellData.getValue().cognomeProperty());
        colonnaNumero.setCellValueFactory(cellData -> cellData.getValue().IDCiabattaProperty());


        //Insert Button
        TableColumn col_action = new TableColumn<>("");

        tabellaPrestiti.getColumns().add(col_action);

        col_action.setCellValueFactory(
                (Callback<TableColumn.CellDataFeatures<Record, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));

        //Adding the Button to the cell
        col_action.setCellFactory(
                (Callback<TableColumn<Record, Boolean>, TableCell<Record, Boolean>>) p -> new ButtonCell());

    }

    public void setApp(CiabatteUnivr app) {
        this.app = app;

        tabellaPrestiti.setItems(app.getPrestiti());
        comboBoxTipologiaCiabatte.setItems(app.getPowerStripTypes());
        comboBoxNumeriCiabatte.setItems(app.getPowerStripNumbers());

        comboBoxNumeriCiabatte.setValue(app.getPowerStripNumbers().get(0));
        comboBoxTipologiaCiabatte.setValue(app.getPowerStripTypes().get(0));
    }

    @FXML
    public void onAddButton() {
        if (nome.getText().isEmpty() || cognome.getText().isEmpty()) return;

        Prestito tmp = new Prestito(nome.getText(), cognome.getText(),
                String.format("%s %d", comboBoxTipologiaCiabatte.getValue(), comboBoxNumeriCiabatte.getValue()));

        try {
            if (app == null) System.out.println("app null");
            app.getPrestiti().addAll(tmp);
            LoanSave.addEntry(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Define the button cell
    private class ButtonCell extends TableCell<Record, Boolean> {
        final Button cellButton = new Button("Rientra");

        ButtonCell() {

            //Action when the button is pressed
            cellButton.setOnAction(t -> {
                        // get Selected Item
                        Prestito p = (Prestito) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                        //remove selected item from the table list
                        app.getPrestiti().remove(p);
                        LoanSave.removeEntry(p);
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

}
