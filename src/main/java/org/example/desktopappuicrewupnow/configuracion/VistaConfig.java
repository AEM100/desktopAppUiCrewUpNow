package org.example.desktopappuicrewupnow.configuracion;


import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import org.example.desktopappuicrewupnow.auth.AuthModel;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;


public class VistaConfig extends VBox {
    public VistaConfig(ControladorConfig controller, Runnable onLogout, Runnable onAccountDeleted) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #ffffff;");

        Label lblTitle = new Label("Configuración");
        lblTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox menuBox = new VBox(5);
        menuBox.setMaxWidth(300);
        menuBox.getChildren().addAll(
                createMenuOption("👤 Editar Perfil", e -> openEditProfileForm(controller)),
                createMenuOption("🌐 Configurar Servidor", e -> openServerSettings()),
                createMenuOption("ℹ️ Acerca de", e -> showInfoPopup()),
                new Separator(),
                createMenuOption("🚪 Cerrar Sesión", e -> onLogout.run()),
                createMenuOption("⚠️ Eliminar Cuenta", e -> {
                    controller.eliminarCuenta();
                    onAccountDeleted.run();
                })
        );
        this.getChildren().addAll(lblTitle, menuBox);
    }

    private Button createMenuOption(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #34495e; -fx-font-size: 15px; -fx-padding: 12;");
        btn.setOnAction(action);
        return btn;
    }

    private void openEditProfileForm(ControladorConfig controller) {
        Stage modal = new Stage();
        modal.setTitle("Editar Perfil");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(15); grid.setPadding(new Insets(20));

        TextField name = new TextField(AuthModel.getName());
        TextField email = new TextField(AuthModel.getEmail());
        PasswordField pass = new PasswordField();
        TextArea bio = new TextArea(AuthModel.getBio()); bio.setPrefHeight(60);

        final String[] base64Foto = {AuthModel.getFotoBase64()};
        Button btnFoto = new Button("Cambiar Foto");
        btnFoto.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(modal);
            if (file != null) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    byte[] bytes = fis.readAllBytes();
                    base64Foto[0] = Base64.getEncoder().encodeToString(bytes);
                    System.out.println("Foto cargada y convertida a Base64");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        grid.add(new Label("Nombre:"), 0, 0); grid.add(name, 1, 0);
        grid.add(new Label("Email:"), 0, 1); grid.add(email, 1, 1);
        grid.add(new Label("Password:"), 0, 2); grid.add(pass, 1, 2);
        grid.add(new Label("Bio:"), 0, 3); grid.add(bio, 1, 3);
        grid.add(new Label("Foto:"), 0, 4); grid.add(btnFoto, 1, 4);

        Button save = new Button("Guardar Cambios");
        save.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        save.setOnAction(e -> {
            controller.actualizarPerfil(
                    name.getText(),
                    email.getText(),
                    pass.getText(),
                    bio.getText(),
                    base64Foto[0]
            );
            modal.close();
        });
        grid.add(save, 1, 5);

        modal.setScene(new Scene(grid));
        modal.show();
    }

    private void openServerSettings() {
        TextInputDialog dialog = new TextInputDialog("127.0.0.1");
        dialog.setTitle("Conexión");
        dialog.setHeaderText("Configurar IP del Servidor");
        dialog.setContentText("Ingrese la nueva IP:");
        dialog.showAndWait().ifPresent(ip -> {
            System.out.println("Intentando conectar a: " + ip);
        });
    }

    private void showInfoPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información del Sistema");
        alert.setHeaderText("CrewUpNow v1.0");
        alert.setContentText("CrewUpNow es una plataforma diseñada para conectar personas a través de eventos sociales. " +
                "Gestiona tus grupos, chats y asistentes en un entorno colaborativo y seguro.\n\n" +
                "Desarrollado en JavaFX / Spring Boot.");
        alert.showAndWait();
    }

}
