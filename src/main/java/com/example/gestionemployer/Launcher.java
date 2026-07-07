package com.example.gestionemployer;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
/* Fandresena L1A N°290/LA/25-26

PROJET DE FIN D'ANNÉE – JAVA
Nom du projet : Gestion des Employés

OBJECTIF :
Ce projet a pour objectif de faciliter la gestion des employés au sein d'une
entreprise et d'automatiser le processus de paiement des salaires.

TECHNOLOGIES UTILISÉES :
  - Java 21 + JavaFX 21 (interface graphique, via FXML)
  - Maven (gestion des dépendances et du build)
  - SQLite (base de données embarquée, via le driver JDBC sqlite-jdbc)
    Toutes les données (entreprise, employés, paiements) sont persistées
    dans un fichier local : <dossier utilisateur>/.gestionemployer/gestionemployer.db
    Ce fichier est créé automatiquement au premier lancement, avec son schéma
    de tables (entreprise, employe, paiement) et des données par défaut.

FONCTIONNALITÉS ET FONCTIONNEMENT :

- Authentification :
  Le PDG se connecte à l'aide d'un nom d'utilisateur et d'un mot de passe.
  Identifiants par défaut :
    - Nom d'utilisateur : admin
    - Mot de passe : admin123
  NB : Ces informations peuvent être modifiées dans les paramètres de l'entreprise.

- Tableau de bord :
  Après la connexion, l'utilisateur accède à un tableau de bord permettant de consulter :
    - Le nombre total d'employés
    - Le solde de l'entreprise
    - Les paiements en attente du mois en cours
    - Les employés actifs
    - Les derniers paiements effectués

- Gestion des employés :
  Cette section permet d'ajouter, de supprimer et de modifier les informations des employés.
  Lors de l'ajout d'un employé, les informations suivantes sont enregistrées :
    - Nom
    - Poste
    - Salaire
    - Moyen de paiement
    - Adresse
    - Département
    - Statut (actif ou inactif)
    - Date d'embauche
    - Email
    - Nom de la banque
    - Numéro de carte bancaire

- Paiement des salaires :
  Cette section affiche le solde de l'entreprise et permet :
    - D'effectuer un paiement manuel (paiement mensuel groupé pour tous
      les employés actifs non encore payés ce mois-ci)
    - De générer un reçu pour un paiement donné
  Un mécanisme de paiement automatique tourne également en arrière-plan
  tant que l'application est ouverte : le dernier jour de chaque mois, les
  salaires des employés actifs sont virés automatiquement, sans action de
  l'utilisateur.

- Paramètres de l'entreprise :
  Cette section permet de configurer les informations de l'entreprise :
    - Nom de l'entreprise
    - Coordonnées bancaires
    - Adresse
    - Email
  Elle permet également de modifier le nom d'utilisateur et le mot de passe.
*/