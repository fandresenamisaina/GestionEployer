package com.example.gestionemployer.controller;

import com.example.gestionemployer.data.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SettingsController {

    @FXML private TextField champNom;
    @FXML private TextField champAdresse;
    @FXML private TextField champTelephone;
    @FXML private TextField champEmail;
    @FXML private TextField champResponsable;
    @FXML private TextField champNomBanque;
    @FXML private TextField champNumeroCompte;
    @FXML private TextField champNumeroCarteBancaire;
    @FXML private TextField champSolde;
    @FXML private TextField champNomUtilisateur;
    @FXML private TextField champMotDePasse;
    @FXML private Label labelMessage;

    @FXML
    public void initialize() {
        com.example.gestionemployer.model.Entreprise e = DatabaseManager.getInstance().getEntreprise();
        champNom.setText(e.getNom());
        champAdresse.setText(e.getAdresse());
        champTelephone.setText(e.getTelephone());
        champEmail.setText(e.getEmail());
        champResponsable.setText(e.getResponsable());
        champNomBanque.setText(e.getNomBanque());
        champNumeroCompte.setText(e.getNumeroCompte());
        champNumeroCarteBancaire.setText(e.getNumeroCarteBancaire());
        champSolde.setText(String.valueOf(e.getSolde()));
        champNomUtilisateur.setText(e.getNomUtilisateur());
        champMotDePasse.setText(e.getMotDePasse());
    }

    @FXML
    protected void onEnregistrerClick() {
        double solde;
        try {
            solde = Double.parseDouble(champSolde.getText().trim().replace(",", "."));
        } catch (Exception ex) {
            labelMessage.setText("Le solde doit etre un nombre valide.");
            return;
        }

        com.example.gestionemployer.model.Entreprise e = new com.example.gestionemployer.model.Entreprise();
        e.setNom(champNom.getText().trim());
        e.setAdresse(champAdresse.getText().trim());
        e.setTelephone(champTelephone.getText().trim());
        e.setEmail(champEmail.getText().trim());
        e.setResponsable(champResponsable.getText().trim());
        e.setNomBanque(champNomBanque.getText().trim());
        e.setNumeroCompte(champNumeroCompte.getText().trim());
        e.setNumeroCarteBancaire(champNumeroCarteBancaire.getText().trim());
        e.setSolde(solde);
        e.setNomUtilisateur(champNomUtilisateur.getText().trim());
        e.setMotDePasse(champMotDePasse.getText());

        DatabaseManager.getInstance().saveEntreprise(e);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Parametres enregistres avec succes.", ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
        labelMessage.setText("");
    }
}