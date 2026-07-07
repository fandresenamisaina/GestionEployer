package com.example.gestionemployer.controller;

import com.example.gestionemployer.MainApp;
import com.example.gestionemployer.data.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML private TextField champUtilisation;
    @FXML private PasswordField champMotDePasse;
    @FXML private Label labeler;

    @FXML
    protected void onLoginClick() {
        String utilisateur = champUtilisation.getText() == null ? "" : champUtilisation.getText().trim();
        String motDePasse = champMotDePasse.getText() == null ? "" : champMotDePasse.getText();

        com.example.gestionemployer.model.Entreprise entreprise = DatabaseManager.getInstance().getEntreprise();

        if (utilisateur.isEmpty() || motDePasse.isEmpty()) {
            labeler.setText("Veuillez renseigner le nom d'utilisateur et le mot de passe.");
            return;
        }

        if (utilisateur.equals(entreprise.getNomUtilisateur()) && motDePasse.equals(entreprise.getMotDePasse())) {
            labeler.setText("");
            try {
                MainApp.switchScene("dashboard.fxml", "Gestion des employes - " + entreprise.getNom(), 1100, 680);
            } catch (IOException e) {
                labeler.setText("Erreur lors du chargement du tableau de bord.");
            }
        } else {
            labeler.setText("Identifiants incorrects.");
        }
    }
}