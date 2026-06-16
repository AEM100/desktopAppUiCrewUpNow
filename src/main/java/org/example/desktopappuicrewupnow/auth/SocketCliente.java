package org.example.desktopappuicrewupnow.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



public class SocketCliente {
    private static SocketCliente instance;
    private String serverIp = "localhost";
    private final int port = 9000;
    private final ObjectMapper mapper = new ObjectMapper();

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private SocketCliente() {}

    public static synchronized SocketCliente getInstance() {
        if (instance == null) instance = new SocketCliente();
        return instance;
    }

    private void cerrarConexion() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out = null;
        in = null;
        socket = null;
    }

    public synchronized void conectar() throws IOException {
        cerrarConexion();
        socket = new Socket(serverIp, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public JsonNode sendRequest(String action, ObjectNode data) {
        if (out == null) {
            try {
                conectar();
            } catch (IOException e) {
                System.err.println(" Fallo en conexión: " + e.getMessage());
                return null;
            }
        }

        try {
            data.put("action", action);
            out.println(data.toString());

            String response = in.readLine();
            if (response == null) {
                cerrarConexion();
                return null;
            }
            return mapper.readTree(response);
        } catch (Exception e) {
            System.err.println(" Error en comunicación, reseteando...");
            cerrarConexion();
            return null;
        }
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
        cerrarConexion();
    }
}