package org.example.desktopappuicrewupnow.auth;


import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.desktopappuicrewupnow.Main;


public class VistaAuth extends StackPane {

    private Label lblError = new Label();
    private final Main mainApp;
    public VistaAuth(AuthController controller, boolean isLogin, Main mainApp) {
        this.setStyle("-fx-background-color: #121212;");
        this.mainApp = mainApp;
        lblError.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        VBox box = new VBox(15);
        box.setMaxSize(350, 500);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new javafx.geometry.Insets(30));
        box.setStyle("-fx-background-color: #1e1e1e; -fx-background-radius: 15;");

        Label lblTitle = new Label(isLogin ? "Iniciar Sesión" : "Crear Cuenta");
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        TextField txtName = isLogin ? null : createStyledTextField("Nombre");
        TextField txtEmail = createStyledTextField("Correo Electrónico");
        PasswordField txtPass = createStyledPasswordField("Contraseña");
        PasswordField txtConfirmPass = isLogin ? null : createStyledPasswordField("Confirmar Contraseña");

        Button btnAccion = new Button(isLogin ? "ENTRAR" : "REGISTRARSE");
        btnAccion.setPrefWidth(Double.MAX_VALUE);
        btnAccion.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

        Button btnSwitch = new Button(isLogin ? "¿No tienes cuenta? Regístrate" : "Ya tengo cuenta, Login");
        btnSwitch.setStyle("-fx-background-color: transparent; -fx-text-fill: #3498db; -fx-underline: true;");
        btnSwitch.setOnAction(e -> {
            mainApp.showAuthScreen(!isLogin);
        });

        btnAccion.setOnAction(e -> {
            if (isLogin) {
                controller.login(txtEmail.getText(), txtPass.getText(), lblError::setText);
            } else {
                controller.registrar(
                        txtName.getText(),
                        txtEmail.getText(),
                        txtPass.getText(),
                        txtConfirmPass.getText(),
                        lblError::setText
                );
            }
        });

        box.getChildren().add(lblTitle);
        if (txtName != null) box.getChildren().add(txtName);
        box.getChildren().addAll(txtEmail, txtPass);
        if (txtConfirmPass != null) box.getChildren().add(txtConfirmPass);
        box.getChildren().addAll(btnAccion, lblError, btnSwitch);

        this.getChildren().add(box);
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #2c2c2c; -fx-text-fill: white; -fx-prompt-text-fill: #777; -fx-background-radius: 5;");
        return field;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle("-fx-background-color: #2c2c2c; -fx-text-fill: white; -fx-prompt-text-fill: #777; -fx-background-radius: 5;");
        return field;
    }
}