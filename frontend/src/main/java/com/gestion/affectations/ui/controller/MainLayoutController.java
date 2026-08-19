package com.gestion.affectations.ui.controller;

import com.gestion.affectations.ui.service.AuthContext;
import com.gestion.affectations.ui.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainLayoutController {

    @FXML private Label usernameLabel;
    @FXML private StackPane contentArea;

    @FXML private Button btnEmployes;
    @FXML private Button btnLieux;
    @FXML private Button btnAffectations;

    @FXML
    public void initialize() {
        usernameLabel.setText("Connecté : " + AuthContext.getInstance().getUsername());
        showEmployes();
    }

    @FXML
    public void showEmployes() {
        setActiveButton(btnEmployes);
        loadView("/fxml/EmployeView.fxml");
    }

    @FXML
    public void showLieux() {
        setActiveButton(btnLieux);
        loadView("/fxml/LieuView.fxml");
    }

    @FXML
    public void showAffectations() {
        setActiveButton(btnAffectations);
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

    private void setActiveButton(Button activeBtn) {
        if(btnEmployes != null) btnEmployes.getStyleClass().remove("sidebar-btn-active");
        if(btnLieux != null) btnLieux.getStyleClass().remove("sidebar-btn-active");
        if(btnAffectations != null) btnAffectations.getStyleClass().remove("sidebar-btn-active");

        if(activeBtn != null && !activeBtn.getStyleClass().contains("sidebar-btn-active")) {
            activeBtn.getStyleClass().add("sidebar-btn-active");
        }
    }
}
