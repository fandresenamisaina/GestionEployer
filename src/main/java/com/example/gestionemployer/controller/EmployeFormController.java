package com.example.gestionemployer.controller;

import com.example.gestionemployer.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class EmployeFormController {

    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private TextField champAdresse;
    @FXML private TextField champTelephone;
    @FXML private TextField champEmail;
    @FXML private TextField champPoste;
    @FXML private TextField champDepartement;
    @FXML private DatePicker champDateEmbauche;
    @FXML private TextField champSalaire;
    @FXML private ComboBox<String> champModePaiement;
    @FXML private TextField champNumeroCompte;
    @FXML private TextField champBanque;
    @FXML private ComboBox<String> champStatut;
    @FXML private Label labelErreur;

    private com.example.gestionemployer.model.Employe employeResultat;
    private Stage stage;

    @FXML
    public void initialize() {
        champModePaiement.getItems().setAll("Carte bancaire", "Mobile Money", "Virement bancaire", "Cheque");
        champStatut.getItems().setAll("Actif", "Inactif");
        champStatut.getSelectionModel().select("Actif");
    }

    private void remplirDepuis(com.example.gestionemployer.model.Employe e) {
        champNom.setText(e.getNom());
        champPrenom.setText(e.getPrenom());
        champAdresse.setText(e.getAdresse());
        champTelephone.setText(e.getTelephone());
        champEmail.setText(e.getEmail());
        champPoste.setText(e.getPoste());
        champDepartement.setText(e.getDepartement());
        try {
            if (e.getDateEmbauche() != null && !e.getDateEmbauche().isEmpty()) {
                champDateEmbauche.setValue(LocalDate.parse(e.getDateEmbauche()));
            }
        } catch (Exception ignored) {
        }
        champSalaire.setText(String.valueOf(e.getSalaire()));
        champModePaiement.getSelectionModel().select(e.getModePaiement());
        champNumeroCompte.setText(e.getNumeroCompte());
        champBanque.setText(e.getBanque());
        champStatut.getSelectionModel().select(e.getStatut());
    }

    @FXML
    protected void onEnregistrerClick() {
        if (champNom.getText() == null || champNom.getText().trim().isEmpty()
                || champPrenom.getText() == null || champPrenom.getText().trim().isEmpty()) {
            labelErreur.setText("Le nom et le prenom sont obligatoires.");
            return;
        }

        double salaire;
        try {
            salaire = Double.parseDouble(champSalaire.getText().trim().replace(",", "."));
        } catch (Exception ex) {
            labelErreur.setText("Le salaire doit etre un nombre valide.");
            return;
        }

        com.example.gestionemployer.model.Employe e = new com.example.gestionemployer.model.Employe();
        e.setNom(champNom.getText().trim());
        e.setPrenom(champPrenom.getText().trim());
        e.setAdresse(nvl(champAdresse.getText()));
        e.setTelephone(nvl(champTelephone.getText()));
        e.setEmail(nvl(champEmail.getText()));
        e.setPoste(nvl(champPoste.getText()));
        e.setDepartement(nvl(champDepartement.getText()));
        e.setDateEmbauche(champDateEmbauche.getValue() != null ? champDateEmbauche.getValue().toString() : "");
        e.setSalaire(salaire);
        e.setModePaiement(champModePaiement.getValue() != null ? champModePaiement.getValue() : "Virement bancaire");
        e.setNumeroCompte(nvl(champNumeroCompte.getText()));
        e.setBanque(nvl(champBanque.getText()));
        e.setStatut(champStatut.getValue() != null ? champStatut.getValue() : "Actif");

        this.employeResultat = e;
        stage.close();
    }

    @FXML
    protected void onAnnulerClick() {
        this.employeResultat = null;
        stage.close();
    }

    private String nvl(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * Ouvre le formulaire en fenetre modale. Si employeExistant est fourni, ses
     * valeurs sont pre-remplies et l'identifiant est conserve (mode modification).
     */
    public static com.example.gestionemployer.model.Employe ouvrirFormulaire(com.example.gestionemployer.model.Employe employeExistant) {
        try {
            FXMLLoader loader = new FXMLLoader(EmployeFormController.class.getResource("/com/example/gestionemployer/employe.fxml"));
            Parent root = loader.load();
            EmployeFormController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(employeExistant == null ? "Ajouter un employe" : "Modifier l'employe");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            controller.stage = stage;

            if (employeExistant != null) {
                controller.remplirDepuis(employeExistant);
            }

            stage.showAndWait();

            if (controller.employeResultat != null && employeExistant != null) {
                controller.employeResultat.setId(employeExistant.getId());
            }
            return controller.employeResultat;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
