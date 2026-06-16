package org.example.desktopappuicrewupnow.perfil.modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserProfile {
    private final StringProperty nombre = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty bio = new SimpleStringProperty("");
    private final StringProperty fotoBase64 = new SimpleStringProperty("");

    public StringProperty fotoBase64Property() { return fotoBase64; }
    public void setFotoBase64(String base64) { this.fotoBase64.set(base64); }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty emailProperty() { return email; }
    public StringProperty bioProperty() { return bio; }
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }
    public void setDatos(String nombre, String email, String bio) {
        this.nombre.set(nombre);
        this.email.set(email);
        this.bio.set(bio);
    }

}