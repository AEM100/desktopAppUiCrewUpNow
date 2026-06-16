package org.example.desktopappuicrewupnow.mapa;

public class Evento {
    private int id;
    private int creatorId;
    private boolean isUserJoined;
    private String ubicacion;

    private String nombre, descripcion, fecha;
    private int participantes;

    public Evento(int id, int creatorId, String nombre, String descripcion,
                  String fecha, String ubicacion, int participantes, boolean isUserJoined) {
        this.id = id;
        this.creatorId = creatorId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.participantes = participantes;
        this.isUserJoined = isUserJoined;
    }

    public int getId() { return id; }
    public int getCreatorId() { return creatorId; }
    public boolean isUserJoined() { return isUserJoined; }
    public String getUbicacion() { return ubicacion; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getFecha() { return fecha; }
    public int getParticipantes() { return participantes; }

    public void setId(int id) { this.id = id; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }
    public void setUserJoined(boolean userJoined) { isUserJoined = userJoined; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}