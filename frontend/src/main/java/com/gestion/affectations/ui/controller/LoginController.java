package com.gestion.affectations.ui.controller;

import com.gestion.affectations.ui.model.JwtResponse;
import com.gestion.affectations.ui.model.LoginRequest;
import com.gestion.affectations.ui.service.ApiService;
import com.gestion.affectations.ui.service.AuthContext;
import com.gestion.affectations.ui.util.NavigationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        // Exécuter la requête réseau dans un thread séparé pour ne pas bloquer l'UI
        new Thread(() -> {
            try {
                LoginRequest loginRequest = new LoginRequest(username, password);
                String responseJson = ApiService.getInstance().post("/auth/login", loginRequest);
                
                JwtResponse jwtResponse = ApiService.getInstance().getGson().fromJson(responseJson, JwtResponse.class);
                
                // Mettre à jour le contexte avec le token
                AuthContext.getInstance().setSession(jwtResponse.getToken(), jwtResponse.getUsername());

                // Retourner sur le thread UI pour changer de vue
                Platform.runLater(() -> {
                    NavigationManager.navigateTo("/fxml/MainLayout.fxml");
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> showError("Identifiants incorrects ou serveur inaccessible."));
            }
        }).start();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
