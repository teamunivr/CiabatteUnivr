package it.teamunivr.ciabatte.view;

import it.teamunivr.ciabatte.CiabatteUnivr;
import it.teamunivr.ciabatte.model.Prestito;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CiabatteController {
    @FXML
    private TableView<Prestito> tabellaPrestiti;

    @FXML
    private TableColumn<Prestito, String> colonnaNome;

    @FXML
    private TableColumn<Prestito, String> colonnaCognome;
    @FXML
    private TableColumn<Prestito, Integer> colonnaNumero;

    @FXML
    private TableColumn<Prestito, Button> colonnaBottone;

    @FXML
    private ComboBox<Integer> comboBoxNumeriCiabatte;

    @FXML
    private ComboBox<Character> comboBoxTipologiaCiabatte;

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
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        colonnaNome.setCellValueFactory(cellData -> cellData.getValue().nomeProperty());
        colonnaCognome.setCellValueFactory(cellData -> cellData.getValue().cognomeProperty());
    }

    public void setApp(CiabatteUnivr app) {
        this.app = app;

        tabellaPrestiti.setItems(app.getPrestiti());
    }

    @FXML
    public void onAddButton() {
        if (nome.getText().isEmpty() || cognome.getText().isEmpty()) return;

        Prestito tmp = new Prestito(nome.getText(), cognome.getText(), 0);

        try {
            if (app == null) System.out.println("app null");
            app.getPrestiti().addAll(tmp);
        }catch (Exception e){
            System.out.println("Errore:");
            e.printStackTrace();
        }
    }


}
