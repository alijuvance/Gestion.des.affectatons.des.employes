module com.gestion.affectations.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;
    requires static lombok;

    opens com.gestion.affectations.ui to javafx.fxml;
    opens com.gestion.affectations.ui.controller to javafx.fxml;
    
    // Allow Gson to serialize/deserialize models
    opens com.gestion.affectations.ui.model to com.google.gson;

    exports com.gestion.affectations.ui;
    exports com.gestion.affectations.ui.controller;
    exports com.gestion.affectations.ui.model;
}
