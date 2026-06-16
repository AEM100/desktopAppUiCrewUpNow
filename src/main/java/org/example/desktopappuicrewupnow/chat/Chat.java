package org.example.desktopappuicrewupnow.chat;

public class Chat {
    private int id;
    private String nombreEvento;

    public Chat(int id, String nombreEvento) {
        this.id = id;
        this.nombreEvento = nombreEvento;
    }

    public int getId() { return id; }
    public String getNombreEvento() { return nombreEvento; }

    @Override
    public String toString() {
        return nombreEvento;
    }
}