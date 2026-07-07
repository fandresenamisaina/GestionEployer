package com.example.gestionemployer.service;

import com.example.gestionemployer.model.Employe;
import com.example.gestionemployer.model.Entreprise;
import com.example.gestionemployer.model.Paiement;
import com.example.gestionemployer.data.DatabaseManager;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

/**
 * Service centralise pour le paiement des salaires (virement de l'entreprise
 * vers le compte / la carte / le mobile money de chaque employe actif).
 * <p>
 * Utilise a la fois par le bouton manuel "Paiement mensuel" et par le
 * declenchement automatique de fin de mois (voir verifierEtPayerSiFinDeMois).
 */
public class PaiementService {

    /**
     * Resultat d'une campagne de paiement, pour affichage a l'utilisateur.
     */
    public static class ResultatPaiement {
        public final int payes;
        public final int ignores;
        public final int echecs;

        public ResultatPaiement(int payes, int ignores, int echecs) {
            this.payes = payes;
            this.ignores = ignores;
            this.echecs = echecs;
        }
    }

    /**
     * Effectue le virement du salaire de chaque employe actif qui n'a pas
     * deja ete paye ce mois-ci. Le montant est preleve sur le solde de
     * l'entreprise (associe a son compte / carte bancaire).
     */
    public static synchronized ResultatPaiement payerSalairesDuMois() {
        DatabaseManager gestionnaire = DatabaseManager.getInstance();
        Entreprise entreprise = gestionnaire.getEntreprise();
        List<Employe> employes = gestionnaire.getEmployes();
        String moisCourant = YearMonth.now().toString();

        int payes = 0, ignores = 0, echecs = 0;

        for (Employe e : employes) {
            if (!e.isActif()) continue;

            boolean dejaPaye = gestionnaire.getPaiements().stream()
                    .anyMatch(p -> p.getEmployeId().equals(e.getId())
                            && p.getDatePaiement() != null && p.getDatePaiement().startsWith(moisCourant)
                            && "Effectue".equals(p.getStatut()));
            if (dejaPaye) {
                ignores++;
                continue;
            }

            Paiement p = new Paiement();
            p.setEmployeId(e.getId());
            p.setEmployeNomComplet(e.getPrenom() + " " + e.getNom());
            p.setDatePaiement(LocalDate.now().toString());
            p.setMontant(e.getSalaire());
            p.setReference("VIR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            if (entreprise.getSolde() >= e.getSalaire()) {
                entreprise.setSolde(entreprise.getSolde() - e.getSalaire());
                p.setStatut("Effectue");
                payes++;
            } else {
                p.setStatut("Echoue - solde insuffisant");
                echecs++;
            }
            gestionnaire.addPaiement(p);
        }
        gestionnaire.saveEntreprise(entreprise);

        return new ResultatPaiement(payes, ignores, echecs);
    }

    /**
     * A appeler regulierement (par ex. au demarrage de l'appli et une fois
     * par heure tant qu'elle tourne). Si la date du jour est le dernier jour
     * du mois, declenche automatiquement le virement des salaires - sans
     * aucune action de l'utilisateur.
     * <p>
     * Comme payerSalairesDuMois() ignore les employes deja payes ce mois-ci,
     * cette methode peut etre appelee plusieurs fois le meme jour sans risque
     * de payer deux fois.
     */
    public static ResultatPaiement verifierEtPayerSiFinDeMois() {
        LocalDate aujourdhui = LocalDate.now();
        boolean dernierJourDuMois = aujourdhui.getDayOfMonth() == aujourdhui.lengthOfMonth();
        if (!dernierJourDuMois) {
            return null;
        }
        return payerSalairesDuMois();
    }
}