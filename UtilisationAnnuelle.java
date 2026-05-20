/**
 * Représente l'utilisation d'un véhicule sur une année donnée.
 * C'est ici qu'on calcule le TCO annuel (Total Cost of Ownership)
 * et le coût par km, en s'appuyant sur le véhicule lié.
 */
public class UtilisationAnnuelle {

    private String idVehicule;
    private int annee;
    private int kmParcourus;

    // Relation : véhicule lié (résolu après chargement)
    private Vehicule vehicule;

    /**
     * Constructeur classique.
     * @param idVehicule identifiant du véhicule
     * @param annee année d'utilisation
     * @param kmParcourus kilomètres parcourus dans l'année
     */
    public UtilisationAnnuelle(String idVehicule, int annee, int kmParcourus) {
        setIdVehicule(idVehicule);
        setAnnee(annee);
        setKmParcourus(kmParcourus);
    }

    /**
     * Constructeur de chargement depuis une ligne CSV.
     * On ne lit que les 3 premières colonnes : idVehicule, annee, kmParcourus.
     * Les colonnes calculées du CSV (coutEnergie, tcoAnnuel, etc.)
     * sont ignorées car recalculées par les méthodes de cette classe.
     * @param champs tableau des colonnes du CSV utilisationAnnuelle
     */
    public UtilisationAnnuelle(String[] champs) {
        this(
            champs[0],
            Integer.parseInt(champs[1].trim()),
            Integer.parseInt(champs[2].trim())
        );
    }

    // ----- Getters -----
    public String getIdVehicule() { return idVehicule; }
    public int getAnnee()         { return annee; }
    public int getKmParcourus()   { return kmParcourus; }
    public Vehicule getVehicule() { return vehicule; }

    // ----- Setters avec validation -----

    public void setIdVehicule(String idVehicule) {
        if (idVehicule == null || idVehicule.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant du véhicule est obligatoire.");
        }
        this.idVehicule = idVehicule;
    }

    public void setAnnee(int annee) {
        if (annee < 1900) {
            throw new IllegalArgumentException("L'année doit être réaliste (>= 1900).");
        }
        this.annee = annee;
    }

    public void setKmParcourus(int kmParcourus) {
        if (kmParcourus < 0) {
            throw new IllegalArgumentException("Les kilomètres parcourus ne peuvent pas être négatifs.");
        }
        this.kmParcourus = kmParcourus;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    // ============================================================
    // CALCULS LIGNE PAR LIGNE
    // ============================================================

    /**
     * Calcul ligne : coût de l'énergie consommée sur l'année.
     * Utilise le coût pour 100 km du véhicule, ramené au kilométrage.
     * @return coût énergie en euros, 0 si véhicule non résolu
     */
    public double getCoutEnergie() {
        if (vehicule == null) {
            return 0;
        }
        return vehicule.getCoutEnergiePour100km() * kmParcourus / 100.0;
    }

    /**
     * Calcul ligne : coût d'entretien annuel.
     * Somme des coûts annuels de tous les entretiens du véhicule.
     * @return coût entretien en euros, 0 si véhicule non résolu
     */
    public double getCoutEntretienAnnuel() {
        if (vehicule == null) {
            return 0;
        }
        double total = 0;
        for (Entretien entretien : vehicule.getEntretiens()) {
            total += entretien.getCoutAnnuel(kmParcourus);
        }
        return total;
    }

    /**
     * Calcul ligne : amortissement annuel du véhicule.
     * @return amortissement en euros, 0 si véhicule non résolu
     */
    public double getAmortissement() {
        if (vehicule == null) {
            return 0;
        }
        return vehicule.getAmortissementAnnuel();
    }

    /**
     * Calcul ligne : TCO annuel = énergie + entretien + amortissement.
     * C'est l'indicateur clé du projet.
     * @return TCO annuel en euros
     */
    public double getTcoAnnuel() {
        return getCoutEnergie() + getCoutEntretienAnnuel() + getAmortissement();
    }

    /**
     * Calcul ligne : coût total de possession ramené au kilomètre.
     * @return coût en euros par km, 0 si aucun km parcouru
     */
    public double getCoutParKm() {
        if (kmParcourus == 0) {
            return 0;
        }
        return getTcoAnnuel() / kmParcourus;
    }
}
