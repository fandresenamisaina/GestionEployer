package com.example.gestionemployer.model;

public class Paiement {
    private String id;
    private String employeId;
    private String employeNomComplet;
    private String datePaiement; // yyyy-MM-dd
    private double montant;
    private String statut;       // Effectue / Echoue
    private String reference;

    public Paiement() {
    }

    public Paiement(String id, String employeId, String employeNomComplet, String datePaiement,
                    double montant, String statut, String reference) {
        this.id = id;
        this.employeId = employeId;
        this.employeNomComplet = employeNomComplet;
        this.datePaiement = datePaiement;
        this.montant = montant;
        this.statut = statut;
        this.reference = reference;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmployeId() { return employeId; }
    public void setEmployeId(String employeId) { this.employeId = employeId; }

    public String getEmployeNomComplet() { return employeNomComplet; }
    public void setEmployeNomComplet(String employeNomComplet) { this.employeNomComplet = employeNomComplet; }

    public String getDatePaiement() { return datePaiement; }
    public void setDatePaiement(String datePaiement) { this.datePaiement = datePaiement; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
