package org.example.desktopappuicrewupnow.chat;

public class Mensaje {
    private String senderName;
    private String content;

    public Mensaje(String senderName, String content) {
        this.senderName = senderName;
        this.content = content;
    }

    public String getSenderName() { return senderName; }
    public String getContent() { return content; }

    @Override
    public String toString() {
        return senderName + ": " + content;
    }
}