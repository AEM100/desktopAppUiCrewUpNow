package org.example.desktopappuicrewupnow.mapa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import org.example.desktopappuicrewupnow.auth.AuthModel;

import java.util.List;
import org.example.desktopappuicrewupnow.Main;
import org.example.desktopappuicrewupnow.auth.SocketCliente;

public class VistaEventos extends VBox {

    private final Main main;
    private final List<Evento> eventosActuales;
    private final TilePane tilePane;

    public VistaEventos(List<Evento> eventos, Main main) {
        this.main = main;
        this.eventosActuales = eventos;
        this.setPadding(new Insets(30));
        this.setFillWidth(true);
        Label titulo = new Label("Eventos en progreso");
        titulo.getStyleClass().add("titulo-pagina");
        TextField buscador = new TextField();
        buscador.setPromptText("Buscar evento...");
        buscador.getStyleClass().add("campo-busqueda");
        buscador.textProperty().addListener((o, old, n) -> filtrarEventos(n));

        BorderPane cabecera = new BorderPane();
        cabecera.setLeft(titulo); cabecera.setRight(buscador);

        tilePane = new TilePane();
        tilePane.setHgap(30); tilePane.setVgap(30);
        tilePane.setPrefColumns(3);
        actualizarGrid(eventosActuales);

        ScrollPane scroll = new ScrollPane(tilePane);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        this.getChildren().addAll(cabecera, scroll);
    }

    private VBox crearTarjetaEvento(Evento e) {
        VBox tarjeta = new VBox(10);
        tarjeta.setPrefSize(300, 220);
        tarjeta.getStyleClass().add("tarjeta-evento");
        tarjeta.setOnMouseClicked(ev -> mostrarDetallesEvento(e));

        Label nombre = new Label(e.getNombre()); nombre.getStyleClass().add("tarjeta-nombre");
        Label desc = new Label(e.getDescripcion()); desc.getStyleClass().add("tarjeta-desc");
        Label part = new Label("👥 " + e.getParticipantes() + " participantes"); part.getStyleClass().add("tarjeta-info");
        Label fecha = new Label("📅 " + e.getFecha()); fecha.getStyleClass().add("tarjeta-info");

        tarjeta.getChildren().addAll(nombre, desc, part, fecha);
        return tarjeta;
    }

    private void mostrarDetallesEvento(Evento e) {
        VBox tarjeta = new VBox(15);
        tarjeta.getStyleClass().add("contenedor-tarjeta");
        tarjeta.setMaxHeight(Region.USE_PREF_SIZE);


        Button btnVolver = new Button("← Volver");
        btnVolver.getStyleClass().add("btn-volver-redondo");
        btnVolver.setOnAction(ev -> main.updateCenter(new VistaEventos(eventosActuales, main)));


        Label tit = new Label(e.getNombre());
        tit.getStyleClass().add("titulo-evento");

        Label desc = new Label(e.getDescripcion());
        desc.getStyleClass().add("desc-evento");

        HBox datos = new HBox(20);
        datos.getChildren().addAll(
                new Label("📅 " + e.getFecha()),
                new Label("👥 " + e.getParticipantes() + " participantes")
        );
        datos.getChildren().forEach(n -> n.getStyleClass().add("info-datos"));

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER_LEFT);
        tarjeta.getChildren().addAll(btnVolver, tit, desc, datos, botones);
        if (e.getCreatorId() != AuthModel.getLoggedUserId()) {
            Button btnJoin = new Button(e.isUserJoined() ? "Desapuntarse" : "Unirse");
            btnJoin.getStyleClass().addAll("btn-accion", e.isUserJoined() ? "btn-desapuntarse" : "btn-unirse");
            btnJoin.setOnAction(ev -> {
                e.setUserJoined(!e.isUserJoined());
                SocketCliente.getInstance().sendRequest("TOGGLE_JOIN", createPayload(e));
                mostrarDetallesEvento(e);
            });
            botones.getChildren().add(btnJoin);
        }

        if (AuthModel.isAdmin() || e.getCreatorId() == AuthModel.getLoggedUserId()) {
            Button btnDel = new Button("Eliminar Evento");
            btnDel.getStyleClass().addAll("btn-accion", "btn-eliminar");
            btnDel.setOnAction(ev -> {
                SocketCliente.getInstance().sendRequest("DELETE_EVENT", createPayload(e));
                main.updateCenter(new VistaEventos(eventosActuales, main));
            });
            botones.getChildren().add(btnDel);
        }

        VBox contFinal = new VBox(15);
        contFinal.getChildren().addAll(tarjeta);

        if (AuthModel.isAdmin()) {
            Button btnBan = new Button("Banear Creador");
            btnBan.getStyleClass().addAll("btn-accion", "btn-banear");
            btnBan.setOnAction(ev -> {
                ObjectNode data = new ObjectMapper().createObjectNode();
                data.put("userIdToBan", e.getCreatorId());
                data.put("adminId", AuthModel.getLoggedUserId());
                SocketCliente.getInstance().sendRequest("BAN_USER", data);
            });
            tarjeta.getChildren().add(btnBan);
        }

        StackPane centrado = new StackPane(tarjeta);

        centrado.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        centrado.setAlignment(Pos.CENTER);


        main.updateCenter(centrado);
    }

    private ObjectNode createPayload(Evento e) {
        ObjectNode data = new ObjectMapper().createObjectNode();
        data.put("userId", AuthModel.getLoggedUserId());
        data.put("eventId", e.getId());
        data.put("join", e.isUserJoined());
        return data;
    }

    private void filtrarEventos(String texto) {
        List<Evento> filtrados = eventosActuales.stream()
                .filter(e -> e.getNombre().toLowerCase().contains(texto.toLowerCase()))
                .toList();
        actualizarGrid(filtrados);
    }

    private void actualizarGrid(List<Evento> eventos) {
        tilePane.getChildren().clear();
        for (Evento e : eventos) {
            tilePane.getChildren().add(crearTarjetaEvento(e));
        }
    }


}