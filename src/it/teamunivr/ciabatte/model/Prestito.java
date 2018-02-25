package it.teamunivr.ciabatte.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Prestito {
    private final StringProperty nome;
    private final StringProperty cognome;
    private final StringProperty IDCiabatta;

    public Prestito(String nome, String cognome, String IDCiabatta) {
        this.nome = new SimpleStringProperty(nome);
        this.cognome = new SimpleStringProperty(cognome);
        this.IDCiabatta = new SimpleStringProperty(IDCiabatta);
    }

    public String getNome() {
        return nome.get();
    }

    public StringProperty nomeProperty() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public String getCognome() {
        return cognome.get();
    }

    public StringProperty cognomeProperty() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome.set(cognome);
    }

    public String getIDCiabatta() {
        return IDCiabatta.get();
    }

    public StringProperty IDCiabattaProperty() {
        return IDCiabatta;
    }

    public void setIDCiabatta(String IDCiabatta) {
        this.IDCiabatta.set(IDCiabatta);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Prestito) {
            Prestito other = (Prestito) obj;
            return this.cognome.equals(other.cognome) && this.nome.equals(other.nome) && this.IDCiabatta.equals(other.IDCiabatta);
        }

        return false;
    }
}
