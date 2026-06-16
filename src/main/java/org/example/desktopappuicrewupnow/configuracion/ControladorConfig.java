package org.example.desktopappuicrewupnow.configuracion;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.desktopappuicrewupnow.auth.AuthModel;
import org.example.desktopappuicrewupnow.auth.SocketCliente;



public class ControladorConfig {

    private final SocketCliente client = SocketCliente.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    public void actualizarPerfil(String nombre, String email, String password, String bio, String nuevaFotoBase64) {
        ObjectNode request = mapper.createObjectNode();
        request.put("userId", AuthModel.getLoggedUserId());
        request.put("name", nombre);
        request.put("email", email);
        request.put("password", password);
        request.put("bio", bio);

        String fotoFinal = (nuevaFotoBase64 != null && !nuevaFotoBase64.isEmpty())
                ? nuevaFotoBase64
                : AuthModel.getFotoBase64();

        if (fotoFinal != null && !fotoFinal.isEmpty()) {
            request.put("foto_base64", fotoFinal);
        }

        var response = client.sendRequest("UPDATE_PROFILE", request);

        if (response != null && "SUCCESS".equals(response.get("status").asText())) {
            AuthModel.setInfo(
                    nombre,
                    email,
                    bio,
                    fotoFinal,
                    AuthModel.isAdmin()
            );
            System.out.println("Perfil actualizado con éxito.");
        }
    }

    public boolean probarReconexion(String ip) {
        return ip != null && ip.matches("^(\\d{1,3}\\.){3}\\d{1,3}$");
    }

    public void eliminarCuenta() {
        ObjectNode request = mapper.createObjectNode();
        request.put("userId", AuthModel.getLoggedUserId());

        var response = client.sendRequest("DELETE_ACCOUNT", request);

        if (response != null && "SUCCESS".equals(response.get("status").asText())) {
            AuthModel.setLoggedUserId(null);
        }
    }
}