package com.gestion.affectations.ui.controller;

import com.gestion.affectations.ui.model.Lieu;
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
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.util.Callback;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.lang.reflect.Type;
import java.util.List;

public class LieuController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Lieu> lieuTable;
    @FXML private TableColumn<Lieu, String> colNom;
    @FXML private TableColumn<Lieu, String> colAdresse;
    @FXML private TableColumn<Lieu, String> colVille;
    @FXML private TableColumn<Lieu, Integer> colCapacite;
    @FXML private TableColumn<Lieu, Void> colActions;

    @FXML private VBox formPanel;
    @FXML private Label formTitle;
    @FXML private TextField txtNom;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtVille;
    @FXML private TextField txtCapacite;

    private ObservableList<Lieu> lieuxList = FXCollections.observableArrayList();
    private Lieu lieuEnCours = null;

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capaciteMax"));

        // Configuration de la colonne d'actions
        Callback<TableColumn<Lieu, Void>, TableCell<Lieu, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Lieu, Void> call(final TableColumn<Lieu, Void> param) {
                return new TableCell<>() {
                    private final Button btnModifier = new Button("✎");
                    private final Button btnSupprimer = new Button("🗑");
                    private final HBox pane = new HBox(10, btnModifier, btnSupprimer);

                    {
                        pane.setAlignment(Pos.CENTER);
                        btnModifier.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563EB; -fx-border-color: #2563EB; -fx-border-radius: 4px; -fx-cursor: hand; -fx-font-size: 16px; -fx-padding: 2 8 2 8;");
                        btnSupprimer.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-background-radius: 4px; -fx-cursor: hand; -fx-font-size: 16px; -fx-padding: 2 8 2 8;");

                        btnModifier.setOnAction(event -> {
                            Lieu data = getTableView().getItems().get(getIndex());
                            handleModifier(data);
                        });

                        btnSupprimer.setOnAction(event -> {
                            Lieu data = getTableView().getItems().get(getIndex());
                            handleSupprimer(data);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };
        colActions.setCellFactory(cellFactory);

        // Configuration de la recherche en temps réel
        FilteredList<Lieu> filteredData = new FilteredList<>(lieuxList, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(lieu -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (lieu.getNom() != null && lieu.getNom().toLowerCase().contains(lowerCaseFilter)) return true;
                if (lieu.getVille() != null && lieu.getVille().toLowerCase().contains(lowerCaseFilter)) return true;
                if (lieu.getAdresse() != null && lieu.getAdresse().toLowerCase().contains(lowerCaseFilter)) return true;
                
                return false;
            });
        });

        SortedList<Lieu> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(lieuTable.comparatorProperty());
        lieuTable.setItems(sortedData);

        loadLieux();
    }

    @FXML
    public void loadLieux() {
        new Thread(() -> {
            try {
                String json = ApiService.getInstance().get("/lieux");
                Type listType = new TypeToken<List<Lieu>>(){}.getType();
                List<Lieu> lieux = ApiService.getInstance().getGson().fromJson(json, listType);
                
                Platform.runLater(() -> {
                    lieuxList.setAll(lieux);
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertUtils.showError("Impossible de charger les lieux : " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void handleNouveau() {
        lieuEnCours = new Lieu();
        formTitle.setText("Ajouter un Lieu");
        clearForm();
        showForm(true);
    }

    @FXML
    public void handleModifier(Lieu selected) {
        if (selected == null) return;
        lieuEnCours = selected;
        formTitle.setText("Modifier le Lieu");
        txtNom.setText(selected.getNom());
        txtAdresse.setText(selected.getAdresse());
        txtVille.setText(selected.getVille());
        txtCapacite.setText(String.valueOf(selected.getCapaciteMax()));
        
        showForm(true);
    }

    @FXML
    public void handleSupprimer(Lieu selected) {
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer ce lieu ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            new Thread(() -> {
                try {
                    ApiService.getInstance().delete("/lieux/" + selected.getId());
                    Platform.runLater(() -> {
                        loadLieux();
                        AlertUtils.showInfo("Lieu supprimé.");
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> AlertUtils.showError("Erreur lors de la suppression : " + e.getMessage()));
                }
            }).start();
        }
    }

    @FXML
    public void handleEnregistrer() {
        if (txtNom.getText().isBlank() || txtAdresse.getText().isBlank() || txtVille.getText().isBlank() || txtCapacite.getText().isBlank()) {
            AlertUtils.showError("Veuillez remplir les champs obligatoires (*).");
            return;
        }

        int capacite;
        try {
            capacite = Integer.parseInt(txtCapacite.getText());
        } catch (NumberFormatException e) {
            AlertUtils.showError("La capacité doit être un nombre valide.");
            return;
        }

        lieuEnCours.setNom(txtNom.getText());
        lieuEnCours.setAdresse(txtAdresse.getText());
        lieuEnCours.setVille(txtVille.getText());
        lieuEnCours.setCapaciteMax(capacite);

        new Thread(() -> {
            try {
                if (lieuEnCours.getId() == null) {
                    ApiService.getInstance().post("/lieux", lieuEnCours);
                } else {
                    ApiService.getInstance().put("/lieux/" + lieuEnCours.getId(), lieuEnCours);
                }
                
                Platform.runLater(() -> {
                    showForm(false);
                    loadLieux();
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
        txtNom.clear();
        txtAdresse.clear();
        txtVille.clear();
        txtCapacite.clear();
    }

    private void showForm(boolean show) {
        formPanel.setVisible(show);
        formPanel.setManaged(show);
    }
}
