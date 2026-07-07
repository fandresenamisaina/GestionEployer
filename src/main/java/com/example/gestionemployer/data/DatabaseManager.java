package com.example.gestionemployer.data;

import com.example.gestionemployer.model.Employe;
import com.example.gestionemployer.model.Entreprise;
import com.example.gestionemployer.model.Paiement;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Gere la persistance de toutes les donnees de l'application (entreprise,
 * employes, paiements) dans une base de donnees SQLite, via JDBC.
 * <p>
 * Le fichier de base de donnees est stocke par defaut dans :
 * &lt;home_utilisateur&gt;/.gestionemployer/gestionemployer.db
 */
public class DatabaseManager {

    private static DatabaseManager instance;

    private final File dbFile;
    private final Connection connection;

    private DatabaseManager() {
        String dir = System.getProperty("user.home") + File.separator + ".gestionemployer";
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        this.dbFile = new File(dirFile, "gestionemployer.db");
        boolean nouvelleBase = !dbFile.exists();

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            creerSchema();
        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException("Erreur lors de l'initialisation de la base de donnees : " + ex.getMessage(), ex);
        }

        if (nouvelleBase) {
            creerDonneesParDefaut();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public File getDbFile() {
        return dbFile;
    }

    // ---------------------------------------------------------------
    // Schema
    // ---------------------------------------------------------------
    private void creerSchema() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS entreprise (
                    id INTEGER PRIMARY KEY CHECK (id = 1),
                    nom TEXT,
                    adresse TEXT,
                    telephone TEXT,
                    email TEXT,
                    nom_banque TEXT,
                    numero_compte TEXT,
                    numero_carte_bancaire TEXT,
                    solde REAL,
                    responsable TEXT,
                    nom_utilisateur TEXT,
                    mot_de_passe TEXT
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS employe (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT,
                    prenom TEXT,
                    adresse TEXT,
                    telephone TEXT,
                    email TEXT,
                    poste TEXT,
                    departement TEXT,
                    date_embauche TEXT,
                    salaire REAL,
                    mode_paiement TEXT,
                    numero_compte TEXT,
                    banque TEXT,
                    statut TEXT
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS paiement (
                    id TEXT PRIMARY KEY,
                    employe_id TEXT,
                    employe_nom TEXT,
                    date_paiement TEXT,
                    montant REAL,
                    statut TEXT,
                    reference TEXT,
                    FOREIGN KEY (employe_id) REFERENCES employe(id) ON DELETE SET NULL
                )
            """);
        }
    }

    // ---------------------------------------------------------------
    // Donnees par defaut (premier lancement)
    // ---------------------------------------------------------------
    private void creerDonneesParDefaut() {
        Entreprise e = new Entreprise();
        e.setNom("Mon Entreprise");
        e.setAdresse("Antananarivo, Madagascar");
        e.setTelephone("034 00 000 00");
        e.setEmail("contact@monentreprise.mg");
        e.setNomBanque("Banque Centrale");
        e.setNumeroCompte("0000000000");
        e.setNumeroCarteBancaire("0000 0000 0000 0000");
        e.setSolde(10000000.0);
        e.setResponsable("Administrateur");
        e.setNomUtilisateur("admin");
        e.setMotDePasse("admin123");
        saveEntreprise(e);
    }

    // ---------------------------------------------------------------
    // Acces / API metier
    // ---------------------------------------------------------------
    public Entreprise getEntreprise() {
        String sql = "SELECT * FROM entreprise WHERE id = 1";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                Entreprise e = new Entreprise();
                e.setNom(rs.getString("nom"));
                e.setAdresse(rs.getString("adresse"));
                e.setTelephone(rs.getString("telephone"));
                e.setEmail(rs.getString("email"));
                e.setNomBanque(rs.getString("nom_banque"));
                e.setNumeroCompte(rs.getString("numero_compte"));
                e.setNumeroCarteBancaire(rs.getString("numero_carte_bancaire"));
                e.setSolde(rs.getDouble("solde"));
                e.setResponsable(rs.getString("responsable"));
                e.setNomUtilisateur(rs.getString("nom_utilisateur"));
                e.setMotDePasse(rs.getString("mot_de_passe"));
                return e;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la lecture de l'entreprise : " + ex.getMessage(), ex);
        }
        return null;
    }

    public void saveEntreprise(Entreprise e) {
        String sql = """
            INSERT INTO entreprise (id, nom, adresse, telephone, email, nom_banque, numero_compte,
                                     numero_carte_bancaire, solde, responsable, nom_utilisateur, mot_de_passe)
            VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                nom = excluded.nom,
                adresse = excluded.adresse,
                telephone = excluded.telephone,
                email = excluded.email,
                nom_banque = excluded.nom_banque,
                numero_compte = excluded.numero_compte,
                numero_carte_bancaire = excluded.numero_carte_bancaire,
                solde = excluded.solde,
                responsable = excluded.responsable,
                nom_utilisateur = excluded.nom_utilisateur,
                mot_de_passe = excluded.mot_de_passe
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getAdresse());
            ps.setString(3, e.getTelephone());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getNomBanque());
            ps.setString(6, e.getNumeroCompte());
            ps.setString(7, e.getNumeroCarteBancaire());
            ps.setDouble(8, e.getSolde());
            ps.setString(9, e.getResponsable());
            ps.setString(10, e.getNomUtilisateur());
            ps.setString(11, e.getMotDePasse());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de l'enregistrement de l'entreprise : " + ex.getMessage(), ex);
        }
    }

    public List<Employe> getEmployes() {
        List<Employe> liste = new ArrayList<>();
        String sql = "SELECT * FROM employe ORDER BY id";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(mapEmploye(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la lecture des employes : " + ex.getMessage(), ex);
        }
        return liste;
    }

    public List<Paiement> getPaiements() {
        List<Paiement> liste = new ArrayList<>();
        String sql = "SELECT * FROM paiement ORDER BY rowid";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(mapPaiement(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la lecture des paiements : " + ex.getMessage(), ex);
        }
        return liste;
    }

    public void addEmploye(Employe emp) {
        String sql = """
            INSERT INTO employe (nom, prenom, adresse, telephone, email, poste, departement,
                                  date_embauche, salaire, mode_paiement, numero_compte, banque, statut)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            remplirParametresEmploye(ps, emp);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    emp.setId(String.valueOf(keys.getLong(1)));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de l'ajout de l'employe : " + ex.getMessage(), ex);
        }
    }

    public void updateEmploye(Employe emp) {
        String sql = """
            UPDATE employe SET nom = ?, prenom = ?, adresse = ?, telephone = ?, email = ?, poste = ?,
                                departement = ?, date_embauche = ?, salaire = ?, mode_paiement = ?,
                                numero_compte = ?, banque = ?, statut = ?
            WHERE id = ?
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            remplirParametresEmploye(ps, emp);
            ps.setLong(14, Long.parseLong(emp.getId()));
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la mise a jour de l'employe : " + ex.getMessage(), ex);
        }
    }

    public void deleteEmploye(String id) {
        String sql = "DELETE FROM employe WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, Long.parseLong(id));
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de la suppression de l'employe : " + ex.getMessage(), ex);
        }
    }

    public void addPaiement(Paiement p) {
        p.setId(nextPaiementId());
        String sql = """
            INSERT INTO paiement (id, employe_id, employe_nom, date_paiement, montant, statut, reference)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            remplirParametresPaiement(ps, p);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Erreur lors de l'ajout du paiement : " + ex.getMessage(), ex);
        }
    }

    // ---------------------------------------------------------------
    // Utilitaires
    // ---------------------------------------------------------------
    private Employe mapEmploye(ResultSet rs) throws SQLException {
        Employe emp = new Employe();
        emp.setId(String.valueOf(rs.getLong("id")));
        emp.setNom(rs.getString("nom"));
        emp.setPrenom(rs.getString("prenom"));
        emp.setAdresse(rs.getString("adresse"));
        emp.setTelephone(rs.getString("telephone"));
        emp.setEmail(rs.getString("email"));
        emp.setPoste(rs.getString("poste"));
        emp.setDepartement(rs.getString("departement"));
        emp.setDateEmbauche(rs.getString("date_embauche"));
        emp.setSalaire(rs.getDouble("salaire"));
        emp.setModePaiement(rs.getString("mode_paiement"));
        emp.setNumeroCompte(rs.getString("numero_compte"));
        emp.setBanque(rs.getString("banque"));
        emp.setStatut(rs.getString("statut"));
        return emp;
    }

    private Paiement mapPaiement(ResultSet rs) throws SQLException {
        Paiement p = new Paiement();
        p.setId(rs.getString("id"));
        p.setEmployeId(rs.getString("employe_id"));
        p.setEmployeNomComplet(rs.getString("employe_nom"));
        p.setDatePaiement(rs.getString("date_paiement"));
        p.setMontant(rs.getDouble("montant"));
        p.setStatut(rs.getString("statut"));
        p.setReference(rs.getString("reference"));
        return p;
    }

    private void remplirParametresEmploye(PreparedStatement ps, Employe emp) throws SQLException {
        ps.setString(1, emp.getNom());
        ps.setString(2, emp.getPrenom());
        ps.setString(3, emp.getAdresse());
        ps.setString(4, emp.getTelephone());
        ps.setString(5, emp.getEmail());
        ps.setString(6, emp.getPoste());
        ps.setString(7, emp.getDepartement());
        ps.setString(8, emp.getDateEmbauche());
        ps.setDouble(9, emp.getSalaire());
        ps.setString(10, emp.getModePaiement());
        ps.setString(11, emp.getNumeroCompte());
        ps.setString(12, emp.getBanque());
        ps.setString(13, emp.getStatut());
    }

    private void remplirParametresPaiement(PreparedStatement ps, Paiement p) throws SQLException {
        ps.setString(1, p.getId());
        ps.setString(2, p.getEmployeId());
        ps.setString(3, p.getEmployeNomComplet());
        ps.setString(4, p.getDatePaiement());
        ps.setDouble(5, p.getMontant());
        ps.setString(6, p.getStatut());
        ps.setString(7, p.getReference());
    }

    private String nextPaiementId() {
        int max = 0;
        for (Paiement p : getPaiements()) {
            try {
                max = Math.max(max, Integer.parseInt(p.getId().replaceAll("[^0-9]", "")));
            } catch (Exception ignored) {
            }
        }
        return String.format("P%03d", max + 1);
    }
}
