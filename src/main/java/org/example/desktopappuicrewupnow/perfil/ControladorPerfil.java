package org.example.desktopappuicrewupnow.perfil;

import org.example.desktopappuicrewupnow.perfil.modelo.UserProfile;

public class ControladorPerfil {
    private final UserProfile model;

    public ControladorPerfil(UserProfile model) {
        this.model = model;
    }

    public void cambiarNombre(String nuevoNombre) {
        if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
            model.setNombre(nuevoNombre);
        }
    }

}