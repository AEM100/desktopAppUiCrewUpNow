module org.example.desktopappuicrewupnow {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires javafx.web;


    opens org.example.desktopappuicrewupnow to javafx.fxml;
    exports org.example.desktopappuicrewupnow;
}