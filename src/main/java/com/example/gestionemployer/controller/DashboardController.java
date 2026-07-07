package com.example.gestionemployer.controller;

import com.example.gestionemployer.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DashboardController {

    @FXML private StackPane zoneContenu;

    @FXML
    public void initialize() {
        chargerVue("dashboard-home.fxml");
    }

    @FXML protected void onAccueilClick() { chargerVue("dashboard-home.fxml"); }
    @FXML protected void onEmployesClick() { chargerVue("employe-list.fxml"); }
    @FXML protected void onPaiementsClick() { chargerVue("paiements.fxml"); }
    @FXML protected void onParametresClick() { chargerVue("settings.fxml"); }

    @FXML
    protected void onDeconnexionClick() {
        try {
            MainApp.switchScene("login.fxml", "Connexion - Gestion des employes", 480, 380);
        } catch (IOException ignored) {
        }
    }

    private void chargerVue(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gestionemployer/" + fxml));
            Node vue = loader.load();
            zoneContenu.getChildren().setAll(vue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}