package it.teamunivr.powerstrips.view;

import it.teamunivr.powerstrips.Config;
import it.teamunivr.powerstrips.model.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PreferenceController {

    @FXML
    private TreeView<String> treeView;

    private TreeItem<String> root;
    private MainController parentController;

    @FXML
    private void initialize() {
        if (!setTreeView()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Non è possibile ottenere la lista degli oggetti prestati");
            alert.setContentText("Si è verificato un errore leggendo la lista degli oggetti prestati");

            alert.showAndWait();
        }
    }

    private boolean setTreeView() {
        root = new TreeItem<>();
        Map<String, ArrayList<String>> itemMap;

        try {
            itemMap = Config.getInstance().getLoanableItems();
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        } catch (Config.BadConfigFileException e) {
            return false;
        }

        for (Map.Entry<String, ArrayList<String>> e : itemMap.entrySet()) {
            TreeItem<String> tmp = new TreeItem<>(e.getKey());

            for (String id : e.getValue())
                tmp.getChildren().add(new TreeItem<>(id));

            root.getChildren().add(tmp);
        }

        MenuItem addType = new MenuItem("Aggiungi Tipologia");

        addType.setOnAction(t -> root.getChildren().add(new TreeItem<>("Nuova Tipologia")));

        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.setCellFactory(p -> new CustomTextFieldTreeCell());
        treeView.setEditable(true);

        treeView.setContextMenu(new ContextMenu(addType));

        return true;
    }

    @FXML
    private void onOk() {
        Map<String, ArrayList<String>> loanableItems = new HashMap<>();
        Map<String, ArrayList<String>> oldLoanableItems;
        ArrayList<Loan> modifiedLoans = new ArrayList<>();
        ArrayList<Loan> loanedObjects = Config.getInstance().getLoanSave().getLoans();

        for (TreeItem<String> t : root.getChildren()) {
            loanableItems.computeIfAbsent(t.getValue(), k -> new ArrayList<>());
            for (TreeItem<String> id : t.getChildren())
                loanableItems.get(t.getValue()).add(id.getValue());
        }

        try {

            try {
                oldLoanableItems = Config.getInstance().getLoanableItems();
            } catch (Config.BadConfigFileException e) {
                oldLoanableItems = null;
            }

            if (oldLoanableItems != null && !oldLoanableItems.equals(loanableItems)) {

                for (Map.Entry<String, ArrayList<String>> e : oldLoanableItems.entrySet()) {
                    if (loanableItems.get(e.getKey()) != null) {
                        for (String s : e.getValue())
                            if (!loanableItems.get(e.getKey()).contains(s)) // se un vecchio id non c'è nei nuovi
                                for (Loan l : loanedObjects)
                                    if (l.getPowerStripID().contains(s))
                                        modifiedLoans.add(l);
                    } else {
                        for (Loan l : loanedObjects) {
                            if (l.getPowerStripID().contains(e.getKey()))
                                modifiedLoans.add(l);
                        }
                    }

                }

                if (!modifiedLoans.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Conferma");
                    alert.setHeaderText("Oggetti in prestito con ID cancellati o modificati");
                    alert.setContentText("Vi sono degli oggetti in prestito con ID che verrebbero cancellati o modificati" +
                            " dalle modifiche effettuate, se premerai su Ok i suddetti prestiti verranno eliminati");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent())
                        if (result.get() != ButtonType.OK)
                            return;
                }
            }

            if (loanableItems.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("È necessario aggiungere almeno una tipologia");
                alert.setContentText("È possibile aggiungere le tipologie di oggetti cliccando col tasto destro sulla tree view");

                alert.showAndWait();
                return;
            }

            Config.getInstance().setLoanableItems(loanableItems);

            for (Loan l : modifiedLoans)
                Config.getInstance().getLoanSave().removeEntry(l);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (parentController != null) {
            parentController.setComboBoxes();
            parentController.updateLoans();
        }

        ((Stage) treeView.getScene().getWindow()).close();
    }

    @FXML
    private void onCancel() {
        ((Stage) treeView.getScene().getWindow()).close();
    }

    public void setParentController(MainController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void onResetDefault() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma");
        alert.setHeaderText("Confgermi di voler Resettare il file di config?");
        alert.setContentText("Attenzione: tutti le tipologie e gli id modificati andranno persi!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent())
            if (result.get() == ButtonType.OK) {
                try {
                    Config.getInstance().resetConfigFile();
                    setTreeView();
                } catch (IOException e) {
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Errore");
                    alert2.setHeaderText("Impossibile resettare il file di default.");
                    alert2.setContentText(
                            "Si consiglia di eliminare il file " + Config.getInstance().getConfigFile().toString()
                                    + " e di riavviare l'applicazione"
                    );

                    alert2.showAndWait();
                }
            }

    }

    private final class CustomTextFieldTreeCell extends TextFieldTreeCell<String> {
        private ContextMenu addMenu = new ContextMenu();

        CustomTextFieldTreeCell() {
            super(new StringConverter<String>() {
                @Override
                public String toString(String object) {
                    return object;
                }

                @Override
                public String fromString(String string) {
                    return string;
                }
            });

            MenuItem addIDMenuItem = new MenuItem("Aggiungi ID");
            MenuItem addTypeMenuItem = new MenuItem("Aggiungi Tipologia");
            MenuItem removeMenuItem = new MenuItem("Rimuovi");

            addMenu.getItems().add(addTypeMenuItem);
            addMenu.getItems().add(addIDMenuItem);
            addMenu.getItems().add(removeMenuItem);

            addTypeMenuItem.setOnAction(t -> root.getChildren().add(new TreeItem<>("Nuova Tipologia")));

            addIDMenuItem.setOnAction(t -> {
                if (getTreeItem().getParent() == root)
                    getTreeItem().getChildren().add(new TreeItem<>("Nuovo ID"));
                else if (getTreeItem().getParent() != null) {
                    getTreeItem().getParent().getChildren().add(new TreeItem<>("Nuovo ID"));
                }
            });

            removeMenuItem.setOnAction(t -> {
                if (getTreeItem().getParent() != null)
                    getTreeItem().getParent().getChildren().remove(getTreeItem());
            });
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (!empty && !isEditing() && getTreeItem().getParent() != null) {
                setContextMenu(addMenu);
            }
        }
    }
}
