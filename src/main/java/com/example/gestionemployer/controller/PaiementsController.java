package com.example.gestionemployer.controller;

import com.example.gestionemployer.data.DatabaseManager;
import com.example.gestionemployer.service.PaiementService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Locale;

public class PaiementsController {

    @FXML private TableView<com.example.gestionemployer.model.Paiement> tablePaiements;
    @FXML private TableColumn<com.example.gestionemployer.model.Paiement, String> colId;
    @FXML private TableColumn<com.example.gestionemployer.model.Paiement, String> colEmploye;
    @FXML private TableColumn<com.example.gestionemployer.model.Paiement, String> colDate;
    @FXML private TableColumn<com.example.gestionemployer.model.Paiement, Double> colMontant;
    @FXML private TableColumn<com.example.gestionemployer.model.Paiement, String> colStatut;
    @FXML private TableColumn<com.example.gestionemployer.model.Paiement, String> colReference;
    @FXML private Label labelSolde;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEmploye.setCellValueFactory(new PropertyValueFactory<>("employeNomComplet"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("datePaiement"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        rafraichir();
    }

    private void rafraichir() {
        tablePaiements.setItems(FXCollections.observableArrayList(DatabaseManager.getInstance().getPaiements()));
        labelSolde.setText(formatMontant(DatabaseManager.getInstance().getEntreprise().getSolde()));
    }

    @FXML
    protected void onPaiementMensuelClick() {
        PaiementService.ResultatPaiement resultat = PaiementService.payerSalairesDuMois();
        rafraichir();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Paiement des salaires");
        alert.setContentText("Paiements effectues : " + resultat.payes
                + "\nDeja payes ce mois-ci : " + resultat.ignores
                + "\nEchecs (solde insuffisant) : " + resultat.echecs);
        alert.showAndWait();
    }

    @FXML
    protected void onGenererRecuClick() {
        com.example.gestionemployer.model.Paiement selection = tablePaiements.getSelectionModel().getSelectedItem();
        if (selection == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Veuillez selectionner un paiement.", ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }

        String recu = "===== RECU DE PAIEMENT =====\n"
                + "Reference    : " + selection.getReference() + "\n"
                + "Employe      : " + selection.getEmployeNomComplet() + "\n"
                + "Date         : " + selection.getDatePaiement() + "\n"
                + "Montant      : " + formatMontant(selection.getMontant()) + "\n"
                + "Statut       : " + selection.getStatut() + "\n"
                + "=============================";

        TextArea zone = new TextArea(recu);
        zone.setEditable(false);
        zone.setWrapText(true);
        zone.setPrefRowCount(10);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Recu de paiement");
        alert.getDialogPane().setContent(zone);
        alert.showAndWait();
    }

    private String formatMontant(double montant) {
        return String.format(Locale.FRANCE, "%,.2f Ar", montant);
    }
}