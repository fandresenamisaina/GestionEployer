package com.example.gestionemployer.controller;

import com.example.gestionemployer.data.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class EmployeListController {

    @FXML private TextField champRecherche;
    @FXML private TableView<com.example.gestionemployer.model.Employe> tableEmployes;
    @FXML private TableColumn<com.example.gestionemployer.model.Employe, String> colId;
    @FXML private TableColumn<com.example.gestionemployer.model.Employe, String> colNom;
    @FXML private TableColumn<com.example.gestionemployer.model.Employe, String> colPrenom;
    @FXML private TableColumn<com.example.gestionemployer.model.Employe, String> colPoste;
    @FXML private TableColumn<com.example.gestionemployer.model.Employe, String> colDepartement;
    @FXML private TableColumn<com.example.gestionemployer.model.Employe, Double> colSalaire;
    @FXML private TableColumn<com.example.gestionemployer.model.Employe, String> colStatut;

    private final ObservableList<com.example.gestionemployer.model.Employe> donnees = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colPoste.setCellValueFactory(new PropertyValueFactory<>("poste"));
        colDepartement.setCellValueFactory(new PropertyValueFactory<>("departement"));
        colSalaire.setCellValueFactory(new PropertyValueFactory<>("salaire"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        donnees.setAll(DatabaseManager.getInstance().getEmployes());

        FilteredList<com.example.gestionemployer.model.Employe> filtres = new FilteredList<>(donnees, e -> true);
        champRecherche.textProperty().addListener((obs, ancien, nouveau) -> {
            String texte = nouveau == null ? "" : nouveau.toLowerCase();
            filtres.setPredicate(e -> texte.isEmpty()
                    || e.getNom().toLowerCase().contains(texte)
                    || e.getPrenom().toLowerCase().contains(texte)
                    || e.getPoste().toLowerCase().contains(texte)
                    || e.getDepartement().toLowerCase().contains(texte));
        });

        tableEmployes.setItems(filtres);
    }

    private void rafraichir() {
        donnees.setAll(DatabaseManager.getInstance().getEmployes());
    }

    @FXML
    protected void onAjouterClick() {
        com.example.gestionemployer.model.Employe resultat = EmployeFormController.ouvrirFormulaire(null);
        if (resultat != null) {
            DatabaseManager.getInstance().addEmploye(resultat);
            rafraichir();
        }
    }

    @FXML
    protected void onModifierClick() {
        com.example.gestionemployer.model.Employe selection = tableEmployes.getSelectionModel().getSelectedItem();
        if (selection == null) {
            alerte("Veuillez selectionner un employe a modifier.");
            return;
        }
        com.example.gestionemployer.model.Employe resultat = EmployeFormController.ouvrirFormulaire(selection);
        if (resultat != null) {
            DatabaseManager.getInstance().updateEmploye(resultat);
            rafraichir();
        }
    }

    @FXML
    protected void onSupprimerClick() {
        com.example.gestionemployer.model.Employe selection = tableEmployes.getSelectionModel().getSelectedItem();
        if (selection == null) {
            alerte("Veuillez selectionner un employe a supprimer.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer l'employe " + selection.getPrenom() + " " + selection.getNom() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirmation.setHeaderText(null);
        confirmation.showAndWait().ifPresent(bouton -> {
            if (bouton == ButtonType.YES) {
                DatabaseManager.getInstance().deleteEmploye(selection.getId());
                rafraichir();
            }
        });
    }

    private void alerte(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}