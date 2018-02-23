package it.teamunivr.ciabatte.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Prestito {
    private final StringProperty nome;
    private final StringProperty cognome;
    private final IntegerProperty IDCiabatta;

    public Prestito(StringProperty nome, StringProperty cognome, IntegerProperty IDCiabatta) {
        this.nome = nome;
        this.cognome = cognome;
        this.IDCiabatta = IDCiabatta;
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

    public int getIDCiabatta() {
        return IDCiabatta.get();
    }

    public IntegerProperty IDCiabattaProperty() {
        return IDCiabatta;
    }

    public void setIDCiabatta(int IDCiabatta) {
        this.IDCiabatta.set(IDCiabatta);
    }
}
