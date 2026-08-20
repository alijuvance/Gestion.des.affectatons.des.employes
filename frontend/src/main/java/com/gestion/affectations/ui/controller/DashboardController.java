package com.gestion.affectations.ui.controller;

import com.gestion.affectations.ui.model.Affectation;
import com.gestion.affectations.ui.model.Employe;
import com.gestion.affectations.ui.model.Lieu;
import com.gestion.affectations.ui.service.ApiService;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardController {

    @FXML private Label lblTotalEmployes;
    @FXML private Label lblTotalLieux;
    @FXML private Label lblTotalAffectations;
    @FXML private BarChart<String, Number> barChart;

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        new Thread(() -> {
            try {
                String jsonEmp = ApiService.getInstance().get("/employes");
                String jsonLieux = ApiService.getInstance().get("/lieux");
                String jsonAff = ApiService.getInstance().get("/affectations");

                Type listEmpType = new TypeToken<List<Employe>>(){}.getType();
                Type listLieuxType = new TypeToken<List<Lieu>>(){}.getType();
                Type listAffType = new TypeToken<List<Affectation>>(){}.getType();

                List<Employe> employes = ApiService.getInstance().getGson().fromJson(jsonEmp, listEmpType);
                List<Lieu> lieux = ApiService.getInstance().getGson().fromJson(jsonLieux, listLieuxType);
                List<Affectation> affectations = ApiService.getInstance().getGson().fromJson(jsonAff, listAffType);

                Platform.runLater(() -> updateUI(employes, lieux, affectations));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateUI(List<Employe> employes, List<Lieu> lieux, List<Affectation> affectations) {
        lblTotalEmployes.setText(String.valueOf(employes.size()));
        lblTotalLieux.setText(String.valueOf(lieux.size()));
        lblTotalAffectations.setText(String.valueOf(affectations.size()));

        // Calcul de la répartition par lieu
        Map<String, Integer> repartition = new HashMap<>();
        for (Affectation aff : affectations) {
            String nomLieu = aff.getLieu().getNom();
            repartition.put(nomLieu, repartition.getOrDefault(nomLieu, 0) + 1);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : repartition.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().clear();
        barChart.getData().add(series);
    }
}
