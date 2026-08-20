package com.gestion.affectations.ui.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class AlertUtils {

    public static void showError(String message) {
        if (Platform.isFxApplicationThread()) {
            showToast(message, "#EF4444", "❌"); // Rouge
        } else {
            Platform.runLater(() -> showToast(message, "#EF4444", "❌"));
        }
    }

    public static void showInfo(String message) {
        if (Platform.isFxApplicationThread()) {
            showToast(message, "#10B981", "✅"); // Vert
        } else {
            Platform.runLater(() -> showToast(message, "#10B981", "✅"));
        }
    }

    private static void showToast(String message, String colorHex, String iconText) {
        Stage toastStage = new Stage();
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.setAlwaysOnTop(true);

        Label icon = new Label(iconText);
        icon.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        
        Label text = new Label(message);
        text.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        text.setWrapText(true);
        text.setMaxWidth(300);

        HBox root = new HBox(15, icon, text);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPadding(new Insets(15, 20, 15, 20));
        root.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);

        // Position: Bottom Right
        toastStage.setOnShown(event -> {
            var bounds = Screen.getPrimary().getVisualBounds();
            toastStage.setX(bounds.getMaxX() - root.getWidth() - 20);
            toastStage.setY(bounds.getMaxY() - root.getHeight() - 20);
        });

        toastStage.show();

        // Animation Fade In
        toastStage.setOpacity(0);
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(toastStage.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(300), new KeyValue(toastStage.opacityProperty(), 1))
        );

        // Animation Fade Out and Close
        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.millis(2500), new KeyValue(toastStage.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(3000), new KeyValue(toastStage.opacityProperty(), 0))
        );
        fadeOut.setOnFinished(e -> toastStage.close());

        fadeIn.setOnFinished(e -> fadeOut.play());
        fadeIn.play();
    }
}
