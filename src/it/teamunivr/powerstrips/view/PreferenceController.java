package it.teamunivr.powerstrips.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PreferenceController {

    @FXML
    private Button close;

    @FXML
    private void initialize() {

    }

    @FXML
    private void onClose() {
        ((Stage) close.getScene().getWindow()).close();
    }

}
