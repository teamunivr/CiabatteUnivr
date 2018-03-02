package it.teamunivr.ciabatte.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Loan {
    private final StringProperty name;
    private final StringProperty lastName;
    private final StringProperty powerStripID;

    public Loan(String nome, String cognome, String IDCiabatta) {
        this.name = new SimpleStringProperty(nome);
        this.lastName = new SimpleStringProperty(cognome);
        this.powerStripID = new SimpleStringProperty(IDCiabatta);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty powerStripIDProperty() {
        return powerStripID;
    }

    public String getPowerStripID() {
        return powerStripID.get();
    }

    public String getName() {
        return name.get();
    }

    public String getLastName() {
        return lastName.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Loan) {
            Loan other = (Loan) obj;
            return this.lastName.equals(other.lastName) && this.name.equals(other.name) && this.powerStripID.equals(other.powerStripID);
        }

        return false;
    }
}
