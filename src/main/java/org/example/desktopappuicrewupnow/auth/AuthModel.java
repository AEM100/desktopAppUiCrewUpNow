package org.example.desktopappuicrewupnow.auth;


public class AuthModel {
    private static String name;
    private static String email;
    private static String bio;
    private static String fotoBase64;
    private static Integer loggedUserId = null;
    private static boolean isAdmin = false; // 🔥 Campo necesario para permisos

    public static void setInfo(String n, String e, String b, String f, boolean admin) {
        name = n;
        email = e;
        bio = b;
        fotoBase64 = f;
        isAdmin = admin;
    }

    public static void setLoggedUserId(Integer id) { loggedUserId = id; }
    public static Integer getLoggedUserId() { return loggedUserId; }

    public static String getName() { return name; }
    public static String getEmail() { return email; }
    public static String getBio() { return bio; }
    public static String getFotoBase64() { return fotoBase64; }

    public static boolean isAdmin() { return isAdmin; }
    public static void setIsAdmin(boolean admin) { isAdmin = admin; }

    public static void setFotoBase64(String foto) { fotoBase64 = foto; }
}