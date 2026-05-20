import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Point d'entrée FleetManager.
 * Charge les 5 fichiers CSV, résout les relations entre entités,
 * puis affiche quelques résultats de test.
 */
public class Main {

    // Séparateur CSV (à adapter selon l'export du tableur : "," ou ";")
    private static final String SEPARATEUR = ",";

    public static void main(String[] args) {
        try {
            // ----- 1. Chargement des CSV -----
            Map<String, TypeEnergie>   typesEnergie   = chargerTypesEnergie("data/typeEnergie.csv");
            Map<String, TypeEntretien> typesEntretien = chargerTypesEntretien("data/typeEntretien.csv");
            Map<String, Vehicule>      vehicules      = chargerVehicules("data/vehicule.csv");
            Map<String, Entretien>     entretiens     = chargerEntretiens("data/entretien.csv");
            List<UtilisationAnnuelle>  utilisations   = chargerUtilisations("data/utilisationAnnuelle.csv");

            // ----- 2. Résolution des relations entre objets -----
            lierRelations(vehicules, typesEnergie, typesEntretien, entretiens, utilisations);

            // ----- 3. Affichage de contrôle -----
            System.out.println("=== CHARGEMENT OK ===");
            System.out.println("Vehicules         : " + vehicules.size());
            System.out.println("Types energie     : " + typesEnergie.size());
            System.out.println("Types entretien   : " + typesEntretien.size());
            System.out.println("Entretiens        : " + entretiens.size());
            System.out.println("Utilisations      : " + utilisations.size());

            // ----- 4. Vérification d'une ligne contre le tableur -----
            System.out.println();
            System.out.println("=== VERIFICATION TCO (VH001 en 2022) ===");
            for (UtilisationAnnuelle u : utilisations) {
                if (u.getIdVehicule().equals("VH001") && u.getAnnee() == 2022) {
                    System.out.printf("Cout energie      : %.2f €%n", u.getCoutEnergie());
                    System.out.printf("Cout entretien    : %.2f €%n", u.getCoutEntretienAnnuel());
                    System.out.printf("Amortissement     : %.2f €%n", u.getAmortissement());
                    System.out.printf("TCO annuel        : %.2f €%n", u.getTcoAnnuel());
                    System.out.printf("Cout par km       : %.4f €/km%n", u.getCoutParKm());
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur lecture CSV : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Donnee invalide dans un CSV : " + e.getMessage());
        }
    }

    // ============================================================
    // CHARGEMENT
    // ============================================================

    /**
     * Lit un fichier CSV et renvoie ses lignes de données (sans l'en-tête)
     * découpées en tableaux de chaînes.
     */
    private static List<String[]> lireCsv(String chemin) throws IOException {
        List<String[]> resultat = new ArrayList<>();
        List<String> lignes = Files.readAllLines(Path.of(chemin), StandardCharsets.UTF_8);
        for (int i = 1; i < lignes.size(); i++) {
            String ligne = lignes.get(i).trim();
            if (!ligne.isEmpty()) {
                resultat.add(ligne.split(SEPARATEUR));
            }
        }
        return resultat;
    }

    public static Map<String, TypeEnergie> chargerTypesEnergie(String chemin) throws IOException {
        Map<String, TypeEnergie> map = new HashMap<>();
        for (String[] champs : lireCsv(chemin)) {
            TypeEnergie t = new TypeEnergie(champs);
            map.put(t.getIdTypeEnergie(), t);
        }
        return map;
    }

    public static Map<String, TypeEntretien> chargerTypesEntretien(String chemin) throws IOException {
        Map<String, TypeEntretien> map = new HashMap<>();
        for (String[] champs : lireCsv(chemin)) {
            TypeEntretien t = new TypeEntretien(champs);
            map.put(t.getIdTypeEntretien(), t);
        }
        return map;
    }

    public static Map<String, Vehicule> chargerVehicules(String chemin) throws IOException {
        Map<String, Vehicule> map = new HashMap<>();
        for (String[] champs : lireCsv(chemin)) {
            Vehicule v = new Vehicule(champs);
            map.put(v.getIdVehicule(), v);
        }
        return map;
    }

    public static Map<String, Entretien> chargerEntretiens(String chemin) throws IOException {
        Map<String, Entretien> map = new HashMap<>();
        for (String[] champs : lireCsv(chemin)) {
            Entretien e = new Entretien(champs);
            map.put(e.getIdEntretien(), e);
        }
        return map;
    }

    public static List<UtilisationAnnuelle> chargerUtilisations(String chemin) throws IOException {
        List<UtilisationAnnuelle> list = new ArrayList<>();
        for (String[] champs : lireCsv(chemin)) {
            list.add(new UtilisationAnnuelle(champs));
        }
        return list;
    }

    // ============================================================
    // RESOLUTION DES RELATIONS
    // ============================================================

    /**
     * Relie les entités entre elles après chargement :
     * Vehicule <-> TypeEnergie, Entretien <-> Vehicule + TypeEntretien,
     * UtilisationAnnuelle <-> Vehicule.
     */
    public static void lierRelations(Map<String, Vehicule> vehicules,
                                     Map<String, TypeEnergie> typesEnergie,
                                     Map<String, TypeEntretien> typesEntretien,
                                     Map<String, Entretien> entretiens,
                                     List<UtilisationAnnuelle> utilisations) {

        // Vehicule <-> TypeEnergie
        for (Vehicule v : vehicules.values()) {
            TypeEnergie te = typesEnergie.get(v.getIdTypeEnergie());
            if (te != null) {
                v.setTypeEnergie(te);
                te.addVehicule(v);
            }
        }

        // Entretien <-> Vehicule + TypeEntretien
        for (Entretien e : entretiens.values()) {
            Vehicule v = vehicules.get(e.getIdVehicule());
            if (v != null) {
                v.addEntretien(e);
            }
            TypeEntretien t = typesEntretien.get(e.getIdTypeEntretien());
            if (t != null) {
                t.addEntretien(e);
            }
        }

        // UtilisationAnnuelle <-> Vehicule
        for (UtilisationAnnuelle u : utilisations) {
            Vehicule v = vehicules.get(u.getIdVehicule());
            if (v != null) {
                u.setVehicule(v);
                v.addUtilisationAnnuelle(u);
            }
        }
    }
}
