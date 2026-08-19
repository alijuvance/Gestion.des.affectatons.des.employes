package com.gestion.affectations.ui.controller;

import com.gestion.affectations.ui.model.Employe;
import com.gestion.affectations.ui.service.ApiService;
import com.gestion.affectations.ui.util.AlertUtils;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public class EmployeController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Employe> employeTable;
    @FXML private TableColumn<Employe, String> colMatricule;
    @FXML private TableColumn<Employe, String> colNom;
    @FXML private TableColumn<Employe, String> colPrenom;
    @FXML private TableColumn<Employe, String> colEmail;
    @FXML private TableColumn<Employe, String> colTelephone;
    @FXML private TableColumn<Employe, String> colFonction;
    @FXML private TableColumn<Employe, LocalDate> colDate;

    @FXML private VBox formPanel;
    @FXML private Label formTitle;
    @FXML private TextField txtMatricule;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private TextField txtFonction;
    @FXML private DatePicker dateEmbauche;

    private ObservableList<Employe> employesList = FXCollections.observableArrayList();
    private Employe employeEnCours = null;

    @FXML
    public void initialize() {
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colFonction.setCellValueFactory(new PropertyValueFactory<>("fonction"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateEmbauche"));

        // Configuration de la recherche en temps réel
        FilteredList<Employe> filteredData = new FilteredList<>(employesList, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(employe -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (employe.getMatricule() != null && employe.getMatricule().toLowerCase().contains(lowerCaseFilter)) return true;
                if (employe.getNom() != null && employe.getNom().toLowerCase().contains(lowerCaseFilter)) return true;
                if (employe.getPrenom() != null && employe.getPrenom().toLowerCase().contains(lowerCaseFilter)) return true;
                if (employe.getFonction() != null && employe.getFonction().toLowerCase().contains(lowerCaseFilter)) return true;
                
                return false;
            });
        });

        SortedList<Employe> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(employeTable.comparatorProperty());
        employeTable.setItems(sortedData);

        loadEmployes();
    }

    @FXML
    public void loadEmployes() {
        new Thread(() -> {
            try {
                String json = ApiService.getInstance().get("/employes");
                Type listType = new TypeToken<List<Employe>>(){}.getType();
                List<Employe> employes = ApiService.getInstance().getGson().fromJson(json, listType);
                
                Platform.runLater(() -> {
                    employesList.setAll(employes);
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertUtils.showError("Impossible de charger les employés : " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void handleNouveau() {
        employeEnCours = new Employe();
        formTitle.setText("Ajouter un Employé");
        clearForm();
        showForm(true);
    }

    @FXML
    public void handleModifier() {
        Employe selected = employeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showInfo("Veuillez sélectionner un employé à modifier.");
            return;
        }
        employeEnCours = selected;
        formTitle.setText("Modifier l'Employé");
        txtMatricule.setText(selected.getMatricule());
        txtNom.setText(selected.getNom());
        txtPrenom.setText(selected.getPrenom());
        txtEmail.setText(selected.getEmail());
        txtTelephone.setText(selected.getTelephone());
        txtFonction.setText(selected.getFonction());
        dateEmbauche.setValue(selected.getDateEmbauche());
        
        showForm(true);
    }

    @FXML
    public void handleSupprimer() {
        Employe selected = employeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showInfo("Veuillez sélectionner un employé à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cet employé ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            new Thread(() -> {
                try {
                    ApiService.getInstance().delete("/employes/" + selected.getId());
                    Platform.runLater(() -> {
                        loadEmployes();
                        AlertUtils.showInfo("Employé supprimé.");
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> AlertUtils.showError("Erreur lors de la suppression : " + e.getMessage()));
                }
            }).start();
        }
    }

    @FXML
    public void handleEnregistrer() {
        if (txtMatricule.getText().isBlank() || txtNom.getText().isBlank() || txtPrenom.getText().isBlank() || txtEmail.getText().isBlank() || txtFonction.getText().isBlank()) {
            AlertUtils.showError("Veuillez remplir les champs obligatoires (*).");
            return;
        }

        employeEnCours.setMatricule(txtMatricule.getText());
        employeEnCours.setNom(txtNom.getText());
        employeEnCours.setPrenom(txtPrenom.getText());
        employeEnCours.setEmail(txtEmail.getText());
        employeEnCours.setTelephone(txtTelephone.getText());
        employeEnCours.setFonction(txtFonction.getText());
        employeEnCours.setDateEmbauche(dateEmbauche.getValue() != null ? dateEmbauche.getValue() : LocalDate.now());

        new Thread(() -> {
            try {
                if (employeEnCours.getId() == null) {
                    // Création
                    ApiService.getInstance().post("/employes", employeEnCours);
                } else {
                    // Modification
                    ApiService.getInstance().put("/employes/" + employeEnCours.getId(), employeEnCours);
                }
                
                Platform.runLater(() -> {
                    showForm(false);
                    loadEmployes();
                    AlertUtils.showInfo("Enregistré avec succès !");
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertUtils.showError("Erreur lors de l'enregistrement : " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void handleAnnuler() {
        showForm(false);
    }

    private void clearForm() {
        txtMatricule.clear();
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtTelephone.clear();
        txtFonction.clear();
        dateEmbauche.setValue(LocalDate.now());
    }

    private void showForm(boolean show) {
        formPanel.setVisible(show);
        formPanel.setManaged(show);
    }
}
