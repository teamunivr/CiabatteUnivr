package it.teamunivr.powerstrips.view;

import it.teamunivr.powerstrips.Config;
import it.teamunivr.powerstrips.model.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.json.simple.parser.ParseException;

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
        Map<String, ArrayList<String>> itemMap;
        root = new TreeItem<>();

        try {
            itemMap = Config.getInstance().getLoanableItems();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        for (Map.Entry<String, ArrayList<String>> e : itemMap.entrySet()) {
            TreeItem<String> tmp = new TreeItem<>(e.getKey());

            for (String id : e.getValue())
                tmp.getChildren().add(new TreeItem<>(id));

            root.getChildren().add(tmp);
        }

        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.setCellFactory(p -> new CustomTextFieldTreeCell());
        treeView.setEditable(true);
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
            oldLoanableItems = Config.getInstance().getLoanableItems();

            if (!oldLoanableItems.equals(loanableItems)) {

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

            Config.getInstance().setLoanableItemes(loanableItems);

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

    private final class CustomTextFieldTreeCell extends TextFieldTreeCell<String> {
        private ContextMenu addMenu = new ContextMenu();

        CustomTextFieldTreeCell() {
            super(new StringConverter<>() {
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
