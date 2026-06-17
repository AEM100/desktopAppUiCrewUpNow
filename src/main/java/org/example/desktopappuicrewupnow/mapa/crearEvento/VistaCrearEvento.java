package org.example.desktopappuicrewupnow.mapa.crearEvento;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.desktopappuicrewupnow.Main;
import org.example.desktopappuicrewupnow.auth.AuthModel;
import org.example.desktopappuicrewupnow.auth.SocketCliente;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VistaCrearEvento extends Stage {

    private final ObjectMapper mapper = new ObjectMapper();

    public VistaCrearEvento(Main main) {

        setTitle("Crear Evento");

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white;");

        Label titulo = new Label("Crear Evento");
        titulo.getStyleClass().add("titulo-pagina");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del evento");

        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Descripción");

        TextField txtUbicacion = new TextField();
        txtUbicacion.setPromptText("Ubicación");

        DatePicker fechaPicker = new DatePicker();

        Spinner<Integer> hora = new Spinner<>(0, 23, 20);

        Spinner<Integer> minuto =
                new Spinner<>(0, 59, 0);

        HBox fechaHora = new HBox(10, fechaPicker, hora, minuto
        );

        Button btnCrear = new Button("Crear Evento");
        btnCrear.getStyleClass().add("btn-crear-evento");

        btnCrear.setOnAction(e -> {

            try {

                LocalDate fecha = fechaPicker.getValue();

                LocalDateTime fechaCompleta = fecha.atTime(hora.getValue(), minuto.getValue());

                ObjectNode request = mapper.createObjectNode();

                request.put("userId", AuthModel.getLoggedUserId()
                );

                request.put("title", txtNombre.getText()
                );

                request.put("description", txtDescripcion.getText()
                );

                request.put("ubicacion", txtUbicacion.getText()
                );

                request.put("fecha", fechaCompleta.toString());

                JsonNode response = SocketCliente.getInstance().sendRequest("CREATE_EVENT", request);

                if (response != null && "SUCCESS".equals(response.get("status").asText())
                ) {
                    close();
                    main.cargarEventosDesdeServidor();

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);

                    alert.setHeaderText("No se pudo crear el evento");

                    alert.showAndWait();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        root.getChildren().addAll(
                titulo,
                txtNombre,
                txtDescripcion,
                txtUbicacion,
                fechaHora,
                btnCrear
        );

        Scene scene = new Scene(root, 500, 450);

        scene.getStylesheets().add(getClass().getResource("/estiloEventos.css").toExternalForm()
        );

        setScene(scene);

        initModality(Modality.APPLICATION_MODAL);
    }
}