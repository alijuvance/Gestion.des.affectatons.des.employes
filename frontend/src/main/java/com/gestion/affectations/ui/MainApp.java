package com.gestion.affectations.ui;

import com.gestion.affectations.ui.util.NavigationManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        NavigationManager.setPrimaryStage(primaryStage);

        // Load the login view by default
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 700);
        
        // Apply the global CSS theme
        String css = getClass().getResource("/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Gestion des Affectations - Authentification");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
