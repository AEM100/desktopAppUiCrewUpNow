package org.example.desktopappuicrewupnow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import org.example.desktopappuicrewupnow.chat.Chat;
import org.example.desktopappuicrewupnow.chat.Mensaje;
import org.example.desktopappuicrewupnow.chat.VistaChat;
import org.example.desktopappuicrewupnow.configuracion.ControladorConfig;
import org.example.desktopappuicrewupnow.configuracion.VistaConfig;
import org.example.desktopappuicrewupnow.mapa.Evento;
import org.example.desktopappuicrewupnow.mapa.VistaEventos;
import org.example.desktopappuicrewupnow.perfil.ControladorPerfil;
import org.example.desktopappuicrewupnow.perfil.VistaPerfil;
import org.example.desktopappuicrewupnow.perfil.modelo.UserProfile;
import org.example.desktopappuicrewupnow.auth.AuthController;
import org.example.desktopappuicrewupnow.auth.AuthModel;
import org.example.desktopappuicrewupnow.auth.SocketCliente;
import org.example.desktopappuicrewupnow.auth.VistaAuth;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {

    private BorderPane root;
    private ObjectMapper mapper = new ObjectMapper();


    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        showAuthScreen(true);
        Scene scene = new Scene(root, 1000, 600);

        String cssPath = getClass().getResource("/estiloEventos.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setTitle("CrewUpNow");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showAuthScreen(boolean isLogin) {
        root.setLeft(null);
        root.setCenter(null);
        AuthController authController = new AuthController(this);
        root.setCenter(new VistaAuth(authController, isLogin, this));
    }


    public void showMainApp() {
        root.setCenter(null);
        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);
        updateCenter(new Label("Bienvenido a CrewUpNow"));
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(75);
        sidebar.setStyle("-fx-background-color: #000000;");

        Button btnChat = createSidebarButton("💬");
        Button btnPerfil = createSidebarButton("👤");
        Button btnEventos = createSidebarButton("📅");
        Button btnConfig = createSidebarButton("⚙");

        btnEventos.setOnAction(e -> {
            new Thread(this::cargarEventosDesdeServidor).start();
        });
        btnPerfil.setOnAction(e -> {
            UserProfile model = new UserProfile();

            model.setDatos(AuthModel.getName(), AuthModel.getEmail(), AuthModel.getBio());
            model.setFotoBase64(AuthModel.getFotoBase64());

            ControladorPerfil controller = new ControladorPerfil(model);
            updateCenter(new VistaPerfil(model, controller, this));
        });
        btnChat.setOnAction(e -> {

            new Thread(this::cargarChatsDesdeServidor).start();
        });
        btnConfig.setOnAction(e -> {
            ControladorConfig controller = new ControladorConfig();
            VistaConfig vista = new VistaConfig(controller,
                    () -> {
                        AuthModel.setLoggedUserId(null);
                        showAuthScreen(true);
                    },
                    () -> {
                        showAuthScreen(true);
                    }
            );
            updateCenter(vista);
        });

        sidebar.getChildren().addAll(btnChat, btnPerfil, btnEventos, btnConfig);
        for (Node node : sidebar.getChildren()) VBox.setVgrow(node, Priority.ALWAYS);

        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 24px;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 24px;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 24px;"));
        return btn;
    }

    public void updateCenter(Node content) {
        root.setCenter(content);
    }
    public void mostrarPopupReconexion() {
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog("localhost");
            dialog.setTitle("Error de Conexión");
            dialog.setHeaderText("No se pudo conectar con el servidor.");
            dialog.setContentText("Por favor, introduce la nueva IP del servidor:");

            dialog.showAndWait().ifPresent(nuevaIp -> {
                SocketCliente.getInstance().setServerIp(nuevaIp);
                System.out.println("IP actualizada a: " + nuevaIp);
            });
        });
    }
    public void cargarEventosDesdeServidor() {
        System.out.println("DEBUG: Botón presionado, intentando conectar...");

        ObjectNode request = mapper.createObjectNode();
        request.put("userId", AuthModel.getLoggedUserId());

        JsonNode response = SocketCliente.getInstance().sendRequest("FETCH_EVENTS", request);

        if (response == null || !"SUCCESS".equals(response.get("status").asText())) {
            System.out.println("DEBUG: Error o respuesta nula.");
            return;
        }

        JsonNode lista = response.get("events");
        List<Evento> eventos = new ArrayList<>();

        for (JsonNode item : lista) {
            eventos.add(new Evento(
                    item.get("id").asInt(),
                    item.get("creatorId").asInt(),
                    item.get("title").asText(),
                    item.get("description").asText(),
                    item.get("fecha").asText(),
                    item.get("ubicacion").asText(),
                    item.get("participantsCount").asInt(),
                    item.get("isUserJoined").asBoolean()
            ));
        }

        Platform.runLater(() -> updateCenter(new VistaEventos(eventos, this)));
    }

    public void cargarChatsDesdeServidor() {
        ObjectNode request = mapper.createObjectNode();
        request.put("userId", AuthModel.getLoggedUserId());
        request.put("action", "FETCH_CHAT_LIST");

        JsonNode response = SocketCliente.getInstance().sendRequest("FETCH_CHAT_LIST", request);

        if (response != null && "SUCCESS".equals(response.get("status").asText())) {
            List<Chat> listaChats = new ArrayList<>();
            JsonNode chatsNode = response.get("chats");

            for (JsonNode n : chatsNode) {
                listaChats.add(new Chat(
                        n.get("chatId").asInt(),
                        n.get("title").asText()
                ));
            }

            Platform.runLater(() -> updateCenter(new VistaChat(listaChats, this)));
        }
    }




    public void enviarMensajeAlServidor(int chatId, String contenido) {
        new Thread(() -> {
            ObjectNode request = mapper.createObjectNode();
            request.put("action", "SEND_CHAT_MESSAGE");
            request.put("chatId", chatId);
            request.put("userId", AuthModel.getLoggedUserId());
            request.put("content", contenido);

            JsonNode response = SocketCliente.getInstance().sendRequest("SEND_CHAT_MESSAGE", request);

            if (response != null && "SUCCESS".equals(response.get("status").asText())) {
                System.out.println("Mensaje enviado con éxito");
            }
        }).start();
    }

    public List<Mensaje> obtenerMensajesDelServidor(int chatId) {
        ObjectNode request = mapper.createObjectNode();
        request.put("action", "FETCH_CHAT_MESSAGES");
        request.put("chatId", chatId);

        JsonNode response = SocketCliente.getInstance().sendRequest("FETCH_CHAT_MESSAGES", request);

        List<Mensaje> lista = new ArrayList<>();
        if (response != null && "SUCCESS".equals(response.get("status").asText())) {
            for (JsonNode n : response.get("messages")) {
                lista.add(new Mensaje(
                        n.get("senderName").asText(),
                        n.get("content").asText()
                ));
            }
        }
        return lista;
    }
    public static void main(String[] args) {
        launch(args);
    }
}