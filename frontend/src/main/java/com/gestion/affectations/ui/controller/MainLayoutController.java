package com.gestion.affectations.ui.controller;

import com.gestion.affectations.ui.service.AuthContext;
import com.gestion.affectations.ui.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML
    private Label usernameLabel;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        usernameLabel.setText("Connecté : " + AuthContext.getInstance().getUsername());
        showEmployes();
    }

    @FXML
    public void showEmployes() {
        loadView("/fxml/EmployeView.fxml");
    }

    @FXML
    public void showLieux() {
        loadView("/fxml/LieuView.fxml");
    }

    @FXML
    public void showAffectations() {
        loadView("/fxml/AffectationView.fxml");
    }

    @FXML
    public void handleLogout() {
        AuthContext.getInstance().logout();
        NavigationManager.navigateTo("/fxml/LoginView.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
            // Optional: show an error in the content area
        }
    }
}
