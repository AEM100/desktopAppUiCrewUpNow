package org.example.desktopappuicrewupnow.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import org.example.desktopappuicrewupnow.Main;

import java.util.function.Consumer;

public class AuthController {

    private final Main mainApp;
    private final SocketCliente client;
    private final ObjectMapper mapper;

    public AuthController(Main mainApp) {
        this.mainApp = mainApp;
        this.client = SocketCliente.getInstance();
        this.mapper = new ObjectMapper();
    }

    public void login(String email, String password, Consumer<String> onError) {
        if (email.isEmpty() || password.isEmpty()) {
            onError.accept("Por favor, rellena todos los campos.");
            return;
        }

        ObjectNode request = mapper.createObjectNode();
        request.put("email", email);
        request.put("password", password);

        new Thread(() -> {
            var response = client.sendRequest("LOGIN", request);

            if (response == null) {
                Platform.runLater(() -> {
                    mainApp.mostrarPopupReconexion();
                    onError.accept("Error de red: no se pudo contactar con el servidor.");
                });
                return;
            }

            if ("SUCCESS".equals(response.get("status").asText())) {
                if (response.has("isBanned") && response.get("isBanned").asBoolean()) {
                    Platform.runLater(() -> onError.accept("Tu cuenta ha sido suspendida."));
                } else {
                    AuthModel.setLoggedUserId(response.get("id").asInt());
                    AuthModel.setInfo(
                            response.get("name").asText(),
                            response.get("email").asText(),
                            response.get("bio").asText(),
                            response.has("foto_base64") ? response.get("foto_base64").asText() : "",
                            response.has("isAdmin") && response.get("isAdmin").asBoolean() // Nuevo parámetro
                    );
                    Platform.runLater(mainApp::showMainApp);
                }
            } else {
                String msg = response.has("message") ? response.get("message").asText() : "Error desconocido.";
                Platform.runLater(() -> onError.accept(msg));
            }
        }).start();
    }

    public void registrar(String nombre, String email, String password, String confirmPassword, Consumer<String> onError) {
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            onError.accept("Todos los campos son obligatorios.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            onError.accept("Las contraseñas no coinciden.");
            return;
        }

        ObjectNode request = mapper.createObjectNode();
        request.put("name", nombre);
        request.put("email", email);
        request.put("password", password);
        request.put("bio", "");

        new Thread(() -> {
            var response = client.sendRequest("REGISTER", request);

            if (response == null) {
                Platform.runLater(() -> {
                    mainApp.mostrarPopupReconexion();
                    onError.accept("Error de red: no se pudo contactar con el servidor.");
                });
                return;
            }

            if ("SUCCESS".equals(response.get("status").asText())) {
                AuthModel.setLoggedUserId(response.get("id").asInt());
                AuthModel.setInfo(
                        response.get("name").asText(),
                        response.get("email").asText(),
                        response.get("bio").asText(),
                        response.has("foto_base64") ? response.get("foto_base64").asText() : "",
                        false
                );
                Platform.runLater(mainApp::showMainApp);
            } else {
                String msg = response.has("message") ? response.get("message").asText() : "Error en el registro.";
                Platform.runLater(() -> onError.accept(msg));
            }
        }).start();
    }
}
