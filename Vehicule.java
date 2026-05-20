import java.util.ArrayList;
import java.util.List;

/**
 * Représente un véhicule de la flotte.
 * Classe centrale : reliée au TypeEnergie,
 * aux Entretiens et aux UtilisationsAnnuelles.
 */
public class Vehicule {

    private String idVehicule;
    private String marque;
    private String modele;
    private String idTypeEnergie;
    private double prixAchat;
    private double consommation;       // L/100km ou kWh/100km
    private int autonomieKm;
    private int anneeAchat;
    private int dureeAmortissement;    // en années

    // Relations
    private TypeEnergie typeEnergie;                       // résolu après chargement
    private List<Entretien> entretiens;
    private List<UtilisationAnnuelle> utilisationsAnnuelles;

    /**
     * Constructeur classique.
     * @param idVehicule identifiant unique
     * @param marque marque du véhicule
     * @param modele modèle du véhicule
     * @param idTypeEnergie identifiant du type d'énergie utilisé
     * @param prixAchat prix d'achat en euros
     * @param consommation consommation en L/100km ou kWh/100km
     * @param autonomieKm autonomie en kilomètres
     * @param anneeAchat année d'achat
     * @param dureeAmortissement durée d'amortissement en années
     */
    public Vehicule(String idVehicule, String marque, String modele, String idTypeEnergie,
                    double prixAchat, double consommation, int autonomieKm,
                    int anneeAchat, int dureeAmortissement) {
        this.entretiens = new ArrayList<>();
        this.utilisationsAnnuelles = new ArrayList<>();

        setIdVehicule(idVehicule);
        setMarque(marque);
        setModele(modele);
        setIdTypeEnergie(idTypeEnergie);
        setPrixAchat(prixAchat);
        setConsommation(consommation);
        setAutonomieKm(autonomieKm);
        setAnneeAchat(anneeAchat);
        setDureeAmortissement(dureeAmortissement);
    }

    /**
     * Constructeur de chargement depuis une ligne CSV.
     * @param champs tableau des 9 colonnes du CSV vehicule
     */
    public Vehicule(String[] champs) {
        this(
            champs[0],
            champs[1],
            champs[2],
            champs[3],
            Double.parseDouble(champs[4].trim()),
            Double.parseDouble(champs[5].trim()),
            Integer.parseInt(champs[6].trim()),
            Integer.parseInt(champs[7].trim()),
            Integer.parseInt(champs[8].trim())
        );
    }

    // ----- Getters -----
    public String getIdVehicule()      { return idVehicule; }
    public String getMarque()          { return marque; }
    public String getModele()          { return modele; }
    public String getIdTypeEnergie()   { return idTypeEnergie; }
    public double getPrixAchat()       { return prixAchat; }
    public double getConsommation()    { return consommation; }
    public int getAutonomieKm()        { return autonomieKm; }
    public int getAnneeAchat()         { return anneeAchat; }
    public int getDureeAmortissement() { return dureeAmortissement; }
    public TypeEnergie getTypeEnergie() { return typeEnergie; }
    public List<Entretien> getEntretiens() { return entretiens; }
    public List<UtilisationAnnuelle> getUtilisationsAnnuelles() { return utilisationsAnnuelles; }

    // ----- Setters avec validation -----

    public void setIdVehicule(String idVehicule) {
        if (idVehicule == null || idVehicule.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant du véhicule est obligatoire.");
        }
        this.idVehicule = idVehicule;
    }

    public void setMarque(String marque) {
        if (marque == null || marque.trim().isEmpty()) {
            throw new IllegalArgumentException("La marque est obligatoire.");
        }
        this.marque = marque;
    }

    public void setModele(String modele) {
        if (modele == null || modele.trim().isEmpty()) {
            throw new IllegalArgumentException("Le modèle est obligatoire.");
        }
        this.modele = modele;
    }

    public void setIdTypeEnergie(String idTypeEnergie) {
        if (idTypeEnergie == null || idTypeEnergie.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant du type d'énergie est obligatoire.");
        }
        this.idTypeEnergie = idTypeEnergie;
    }

    public void setPrixAchat(double prixAchat) {
        if (prixAchat < 0) {
            throw new IllegalArgumentException("Le prix d'achat ne peut pas être négatif.");
        }
        this.prixAchat = prixAchat;
    }

    public void setConsommation(double consommation) {
        if (consommation < 0) {
            throw new IllegalArgumentException("La consommation ne peut pas être négative.");
        }
        this.consommation = consommation;
    }

    public void setAutonomieKm(int autonomieKm) {
        if (autonomieKm < 0) {
            throw new IllegalArgumentException("L'autonomie ne peut pas être négative.");
        }
        this.autonomieKm = autonomieKm;
    }

    public void setAnneeAchat(int anneeAchat) {
        if (anneeAchat < 1900) {
            throw new IllegalArgumentException("L'année d'achat doit être réaliste (>= 1900).");
        }
        this.anneeAchat = anneeAchat;
    }

    public void setDureeAmortissement(int dureeAmortissement) {
        if (dureeAmortissement <= 0) {
            throw new IllegalArgumentException("La durée d'amortissement doit être strictement positive.");
        }
        this.dureeAmortissement = dureeAmortissement;
    }

    // ----- Gestion des relations -----

    public void setTypeEnergie(TypeEnergie typeEnergie) {
        this.typeEnergie = typeEnergie;
    }

    public void addEntretien(Entretien entretien) {
        if (entretien != null) {
            entretiens.add(entretien);
        }
    }

    public void addUtilisationAnnuelle(UtilisationAnnuelle utilisation) {
        if (utilisation != null) {
            utilisationsAnnuelles.add(utilisation);
        }
    }

    // ============================================================
    // CALCULS LIGNE PAR LIGNE
    // ============================================================

    /**
     * Calcul ligne : amortissement annuel linéaire du véhicule.
     * Exemple : 42990 € sur 5 ans → 8598 € par an.
     * @return amortissement annuel en euros
     */
    public double getAmortissementAnnuel() {
        return prixAchat / dureeAmortissement;
    }

    /**
     * Calcul ligne : coût de l'énergie pour 100 km.
     * Utilise le type d'énergie lié (consommation × prix par unité).
     * Exemple : Tesla 14.5 kWh/100km × 0.25 €/kWh = 3.625 €/100km.
     * @return coût énergie pour 100 km en euros, 0 si type énergie non résolu
     */
    public double getCoutEnergiePour100km() {
        if (typeEnergie == null) {
            return 0;
        }
        return consommation * typeEnergie.getPrixParUnite();
    }
}
