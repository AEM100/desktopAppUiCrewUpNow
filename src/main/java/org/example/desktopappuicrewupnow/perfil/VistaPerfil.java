package org.example.desktopappuicrewupnow.perfil;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.example.desktopappuicrewupnow.Main;
import org.example.desktopappuicrewupnow.perfil.modelo.UserProfile;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class VistaPerfil extends VBox {

    private Main mainApp;

    public VistaPerfil(UserProfile model, ControladorPerfil controller, Main mainApp) {
        this.mainApp = mainApp;

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(40));
        this.setSpacing(20);
        this.getStyleClass().add("perfil-container");

        ImageView avatarView = new ImageView();
        avatarView.setFitWidth(120);
        avatarView.setFitHeight(120);
        avatarView.setPreserveRatio(true);

        Circle clip = new Circle(60, 60, 60);
        avatarView.setClip(clip);

        if (model.fotoBase64Property().get() != null && !model.fotoBase64Property().get().isEmpty()) {
            avatarView.setImage(base64ToImage(model.fotoBase64Property().get()));
        }

        model.fotoBase64Property().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                Image img = base64ToImage(newVal);
                Platform.runLater(() -> avatarView.setImage(img));
            }
        });

        Label lblNombre = new Label();
        lblNombre.textProperty().bind(model.nombreProperty());
        lblNombre.getStyleClass().add("perfil-nombre");

        Label lblEmail = new Label();
        lblEmail.textProperty().bind(model.emailProperty());
        lblEmail.getStyleClass().add("perfil-email");

        Label lblBiografia = new Label();
        lblBiografia.textProperty().bind(model.bioProperty());
        lblBiografia.getStyleClass().add("perfil-bio");
        lblBiografia.setWrapText(true);
        lblBiografia.setMaxWidth(400);
        lblBiografia.setTextAlignment(TextAlignment.CENTER);

        VBox spacer = new VBox();
        spacer.setPrefHeight(10);

        Button btnMisChats = createMenuButton("💬 Mis Chats de Eventos");

        btnMisChats.setOnAction(e -> {
            new Thread(mainApp::cargarChatsDesdeServidor).start();
        });

        this.getChildren().addAll(avatarView, lblNombre, lblEmail, spacer, lblBiografia, btnMisChats);
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(280);
        btn.setPrefHeight(45);
        btn.getStyleClass().add("perfil-button");
        return btn;
    }

    private Image base64ToImage(String base64) {
        try {
            if (base64 == null || base64.isEmpty()) return null;
            String data = base64.contains(",") ? base64.split(",")[1] : base64;
            data = data.replaceAll("\\s", "");
            byte[] bytes = java.util.Base64.getDecoder().decode(data);
            return new Image(new java.io.ByteArrayInputStream(bytes));
        } catch (Exception e) {
            System.out.println("Error decodificando imagen: " + e.getMessage());
            return null;
        }
    }
}