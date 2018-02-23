package it.teamunivr.ciabatte.view;

import it.teamunivr.ciabatte.CiabatteUnivr;
import it.teamunivr.ciabatte.model.Prestito;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CiabatteController {
    @FXML
    private TableView<Prestito> tabellaPrestiti;

    @FXML
    private TableColumn<Prestito, String> colonnaNome;

    @FXML
    private TableColumn<Prestito, String> colonnaCognome;

    @FXML
    private TableColumn<Prestito, Button> colonnaBottone;

    @FXML
    private ComboBox<Integer> comboBoxNumeriCiabatte;

    @FXML
    private ComboBox<Character> comboBoxTipologiaCiabatte;

    // Reference to the main application.
    private CiabatteUnivr app;

    public CiabatteController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        colonnaNome.setCellValueFactory(cellData -> cellData.getValue().nomeProperty());
        colonnaCognome.setCellValueFactory(cellData -> cellData.getValue().cognomeProperty());

    }
}
