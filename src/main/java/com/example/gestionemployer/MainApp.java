package com.example.gestionemployer;

import com.example.gestionemployer.data.DatabaseManager;
import com.example.gestionemployer.service.PaiementService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static Timer minuteurPaiementAutomatique;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Initialise / charge la base de donnees des le demarrage
        DatabaseManager.getInstance();

        stage.setResizable(true);
        switchScene("login.fxml", "Connexion - Gestion des employes", 480, 380);
        stage.show();

        demarrerVerificationPaiementAutomatique();
    }

    /**
     * Lance une verification en arriere-plan : au demarrage de l'appli, puis
     * toutes les heures tant qu'elle reste ouverte, on regarde si on est le
     * dernier jour du mois. Si oui, PaiementService vire automatiquement le
     * salaire de chaque employe actif, sans aucune action du proprietaire.
     */
    private void demarrerVerificationPaiementAutomatique() {
        minuteurPaiementAutomatique = new Timer("verification-paiement-automatique", true);
        long uneHeure = 60L * 60L * 1000L;
        minuteurPaiementAutomatique.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                PaiementService.ResultatPaiement resultat = PaiementService.verifierEtPayerSiFinDeMois();
                if (resultat != null && resultat.payes > 0) {
                    Platform.runLater(() ->
                            System.out.println("Paiement automatique de fin de mois effectue : "
                                    + resultat.payes + " employe(s) paye(s)."));
                }
            }
        }, 0, uneHeure);
    }

    @Override
    public void stop() {
        if (minuteurPaiementAutomatique != null) {
            minuteurPaiementAutomatique.cancel();
        }
    }

    /**
     * Remplace la scene affichee par le contenu du fichier FXML donne.
     */
    public static void switchScene(String fxml, String title, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);
        String css = MainApp.class.getResource("style.css") != null
                ? Objects.requireNonNull(MainApp.class.getResource("style.css")).toExternalForm() : null;
        if (css != null) {
            scene.getStylesheets().add(css);
        }
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}