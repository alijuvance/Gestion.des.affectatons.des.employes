package com.gestion.affectations.ui.controller;

import com.gestion.affectations.ui.model.Affectation;
import com.gestion.affectations.ui.model.AffectationRequest;
import com.gestion.affectations.ui.model.Employe;
import com.gestion.affectations.ui.model.Lieu;
import com.gestion.affectations.ui.service.ApiService;
import com.gestion.affectations.ui.util.AlertUtils;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public class AffectationController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Affectation> affectationTable;
    @FXML private TableColumn<Affectation, String> colEmploye;
    @FXML private TableColumn<Affectation, String> colLieu;
    @FXML private TableColumn<Affectation, LocalDate> colDateDebut;
    @FXML private TableColumn<Affectation, LocalDate> colDateFin;

    @FXML private VBox formPanel;
    @FXML private ComboBox<Employe> comboEmploye;
    @FXML private ComboBox<Lieu> comboLieu;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;

    private ObservableList<Affectation> affectationsList = FXCollections.observableArrayList();
    private ObservableList<Employe> employesList = FXCollections.observableArrayList();
    private ObservableList<Lieu> lieuxList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colEmploye.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getEmploye().getPrenom() + " " + cellData.getValue().getEmploye().getNom()));
            
        colLieu.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getLieu().getNom() + " (" + cellData.getValue().getLieu().getVille() + ")"));
            
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));

        // Configuration de la recherche en temps réel
        FilteredList<Affectation> filteredData = new FilteredList<>(affectationsList, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(affectation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                
                String nomEmploye = affectation.getEmploye().getPrenom() + " " + affectation.getEmploye().getNom();
                String nomLieu = affectation.getLieu().getNom() + " " + affectation.getLieu().getVille();
                
                if (nomEmploye.toLowerCase().contains(lowerCaseFilter)) return true;
                if (nomLieu.toLowerCase().contains(lowerCaseFilter)) return true;
                
                return false;
            });
        });

        SortedList<Affectation> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(affectationTable.comparatorProperty());
        affectationTable.setItems(sortedData);

        comboEmploye.setItems(employesList);
        comboLieu.setItems(lieuxList);

        setupConverters();
        loadData();
    }

    private void setupConverters() {
        comboEmploye.setConverter(new StringConverter<Employe>() {
            @Override
            public String toString(Employe e) {
                return e == null ? "" : e.getMatricule() + " - " + e.getPrenom() + " " + e.getNom();
            }
            @Override
            public Employe fromString(String string) { return null; }
        });

        comboLieu.setConverter(new StringConverter<Lieu>() {
            @Override
            public String toString(Lieu l) {
                return l == null ? "" : l.getNom() + " (" + l.getVille() + ")";
            }
            @Override
            public Lieu fromString(String string) { return null; }
        });
    }

    @FXML
    public void loadData() {
        new Thread(() -> {
            try {
                String jsonAff = ApiService.getInstance().get("/affectations");
                String jsonEmp = ApiService.getInstance().get("/employes");
                String jsonLieu = ApiService.getInstance().get("/lieux");

                Type listAffType = new TypeToken<List<Affectation>>(){}.getType();
                Type listEmpType = new TypeToken<List<Employe>>(){}.getType();
                Type listLieuType = new TypeToken<List<Lieu>>(){}.getType();

                List<Affectation> affectations = ApiService.getInstance().getGson().fromJson(jsonAff, listAffType);
                List<Employe> employes = ApiService.getInstance().getGson().fromJson(jsonEmp, listEmpType);
                List<Lieu> lieux = ApiService.getInstance().getGson().fromJson(jsonLieu, listLieuType);

                Platform.runLater(() -> {
                    affectationsList.setAll(affectations);
                    employesList.setAll(employes);
                    lieuxList.setAll(lieux);
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertUtils.showError("Impossible de charger les données : " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void handleNouveau() {
        comboEmploye.getSelectionModel().clearSelection();
        comboLieu.getSelectionModel().clearSelection();
        dateDebut.setValue(LocalDate.now());
        dateFin.setValue(null);
        showForm(true);
    }

    @FXML
    public void handleSupprimer() {
        Affectation selected = affectationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showInfo("Veuillez sélectionner une affectation à annuler.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment annuler cette affectation ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            new Thread(() -> {
                try {
                    ApiService.getInstance().delete("/affectations/" + selected.getId());
                    Platform.runLater(() -> {
                        loadData();
                        AlertUtils.showInfo("Affectation annulée.");
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> AlertUtils.showError("Erreur lors de l'annulation : " + e.getMessage()));
                }
            }).start();
        }
    }

    @FXML
    public void handleEnregistrer() {
        Employe emp = comboEmploye.getSelectionModel().getSelectedItem();
        Lieu lieu = comboLieu.getSelectionModel().getSelectedItem();
        LocalDate dDebut = dateDebut.getValue();

        if (emp == null || lieu == null || dDebut == null) {
            AlertUtils.showError("Veuillez sélectionner un employé, un lieu et une date de début.");
            return;
        }

        AffectationRequest request = new AffectationRequest(emp.getId(), lieu.getId(), dDebut, dateFin.getValue());

        new Thread(() -> {
            try {
                ApiService.getInstance().post("/affectations", request);
                Platform.runLater(() -> {
                    showForm(false);
                    loadData();
                    AlertUtils.showInfo("Affectation créée avec succès !");
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertUtils.showError(e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void handleAnnuler() {
        showForm(false);
    }

    private void showForm(boolean show) {
        formPanel.setVisible(show);
        formPanel.setManaged(show);
    }
}
