package org.example.desktopappuicrewupnow.chat;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.desktopappuicrewupnow.Main;
import java.util.List;


import org.example.desktopappuicrewupnow.auth.AuthModel;

public class VistaChat extends BorderPane {
    private Main mainApp;
    private ListView<Mensaje> listaMensajesUI;

    public VistaChat(List<Chat> chats, Main mainApp) {
        this.mainApp = mainApp;

        ListView<Chat> chatListView = new ListView<>();
        chatListView.setPrefWidth(280);

        chatListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Chat item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label icon = new Label("#");
                    icon.setStyle("-fx-text-fill: #949ba4; -fx-font-weight: bold; -fx-font-size: 16px;");

                    Label name = new Label(item.getNombreEvento());
                    HBox box = new HBox(12, icon, name);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });
        chatListView.getItems().addAll(chats);

        StackPane chatArea = new StackPane();
        chatArea.getStyleClass().add("chat-background");

        Label lblWelcome = new Label("Selecciona un canal para comenzar a hablar");
        lblWelcome.getStyleClass().add("welcome-label");
        chatArea.getChildren().add(lblWelcome);

        chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                chatArea.getChildren().clear();
                chatArea.getChildren().add(crearInterfazMensajes(newVal));
                cargarMensajes(newVal.getId());
            }
        });

        SplitPane split = new SplitPane(chatListView, chatArea);
        split.setDividerPositions(0.28);
        setCenter(split);
    }

    private VBox crearInterfazMensajes(Chat chat) {
        VBox mainBox = new VBox();
        VBox.setVgrow(mainBox, Priority.ALWAYS);

        listaMensajesUI = new ListView<>();
        VBox.setVgrow(listaMensajesUI, Priority.ALWAYS);

        listaMensajesUI.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Mensaje item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cellContainer = new VBox(2);

                    Label contentLabel = new Label(item.getContent());
                    contentLabel.setWrapText(true);

                    HBox bubble = new HBox(contentLabel);
                    bubble.getStyleClass().add("msg-container");

                    boolean esMio = item.getSenderName().equalsIgnoreCase(AuthModel.getName());

                    if (esMio) {
                        cellContainer.setAlignment(Pos.CENTER_RIGHT);
                        bubble.getStyleClass().add("msg-me");
                        contentLabel.getStyleClass().add("msg-text-me");
                        cellContainer.getChildren().add(bubble);
                    } else {
                        cellContainer.setAlignment(Pos.CENTER_LEFT);

                        Label senderLabel = new Label(item.getSenderName());
                        senderLabel.getStyleClass().add("msg-sender");

                        bubble.getStyleClass().add("msg-other");
                        contentLabel.getStyleClass().add("msg-text-other");

                        cellContainer.getChildren().addAll(senderLabel, bubble);
                    }

                    HBox rootCellBox = new HBox(cellContainer);
                    rootCellBox.setAlignment(esMio ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

                    setGraphic(rootCellBox);
                    setStyle("-fx-background-color: transparent;");
                }
            }
        });

        TextField input = new TextField();
        input.setPromptText("Enviar mensaje a #" + chat.getNombreEvento());
        input.getStyleClass().add("chat-input");
        HBox.setHgrow(input, Priority.ALWAYS);

        Button btnEnviar = new Button("Enviar");
        btnEnviar.getStyleClass().add("chat-button");

        Runnable enviarAccion = () -> {
            String texto = input.getText().trim();
            if (!texto.isEmpty()) {
                mainApp.enviarMensajeAlServidor(chat.getId(), texto);
                input.clear();
                cargarMensajes(chat.getId());
            }
        };

        btnEnviar.setOnAction(e -> enviarAccion.run());
        input.setOnAction(e -> enviarAccion.run());

        HBox inputBox = new HBox(12, input, btnEnviar);
        inputBox.getStyleClass().add("chat-input-box");
        inputBox.setAlignment(Pos.CENTER);

        mainBox.getChildren().addAll(listaMensajesUI, inputBox);
        return mainBox;
    }

    private void cargarMensajes(int chatId) {
        new Thread(() -> {
            List<Mensaje> msgs = mainApp.obtenerMensajesDelServidor(chatId);
            Platform.runLater(() -> {
                if (listaMensajesUI != null) {
                    listaMensajesUI.getItems().setAll(msgs);
                    listaMensajesUI.scrollTo(msgs.size() - 1);
                }
            });
        }).start();
    }
}