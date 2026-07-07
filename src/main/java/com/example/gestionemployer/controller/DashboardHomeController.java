package com.example.gestionemployer.controller;

import com.example.gestionemployer.data.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import com.example.gestionemployer.model.Employe;
import com.example.gestionemployer.model.Paiement;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DashboardHomeController {

    @FXML private Label labelTotalEmployes;
    @FXML private Label labelEmployesActifs;
    @FXML private Label labelMasseSalariale;
    @FXML private Label labelSolde;
    @FXML private Label labelPaiementsEnAttente;
    @FXML private ListView<String> listeDerniersPaiements;

    @FXML
    public void initialize() {
        DatabaseManager gestionnaire = DatabaseManager.getInstance();
        List<Employe> employes = gestionnaire.getEmployes();
        List<Paiement> paiements = gestionnaire.getPaiements();

        long totalEmployes = employes.size();
        long employesActifs = employes.stream().filter(Employe::isActif).count();
        double masseSalariale = employes.stream().filter(Employe::isActif).mapToDouble(Employe::getSalaire).sum();

        labelTotalEmployes.setText(String.valueOf(totalEmployes));
        labelEmployesActifs.setText(String.valueOf(employesActifs));
        labelMasseSalariale.setText(formatMontant(masseSalariale));
        labelSolde.setText(formatMontant(gestionnaire.getEntreprise().getSolde()));

        String moisCourant = YearMonth.now().toString();
        long dejaPayes = paiements.stream()
                .filter(p -> p.getDatePaiement() != null && p.getDatePaiement().startsWith(moisCourant))
                .map(Paiement::getEmployeId)
                .distinct()
                .count();
        long enAttente = Math.max(0, employesActifs - dejaPayes);
        labelPaiementsEnAttente.setText(String.valueOf(enAttente));

        List<String> derniers = paiements.stream()
                .sorted(Comparator.comparing(Paiement::getDatePaiement, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(6)
                .map(p -> p.getDatePaiement() + "  -  " + p.getEmployeNomComplet() + "  -  " + formatMontant(p.getMontant()) + "  (" + p.getStatut() + ")")
                .collect(Collectors.toList());
        listeDerniersPaiements.getItems().setAll(derniers);
    }

    private String formatMontant(double montant) {
        return String.format(Locale.FRANCE, "%,.2f Ar", montant);
    }
}