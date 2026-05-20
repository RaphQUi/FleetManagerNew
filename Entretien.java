/**
 * Représente une opération d'entretien effectuée sur un véhicule
 * (ex : pneus à changer tous les 40 000 km pour 600 €).
 */
public class Entretien {

    private String idEntretien;
    private String idVehicule;
    private String idTypeEntretien;
    private double coutUnitaire;   // coût en € d'une opération
    private int frequenceKm;       // tous les combien de km

    /**
     * Constructeur classique.
     * @param idEntretien identifiant unique de l'entretien
     * @param idVehicule identifiant du véhicule concerné
     * @param idTypeEntretien identifiant du type d'entretien
     * @param coutUnitaire coût en euros d'une opération
     * @param frequenceKm fréquence de l'entretien en kilomètres
     */
    public Entretien(String idEntretien, String idVehicule, String idTypeEntretien,
                     double coutUnitaire, int frequenceKm) {
        setIdEntretien(idEntretien);
        setIdVehicule(idVehicule);
        setIdTypeEntretien(idTypeEntretien);
        setCoutUnitaire(coutUnitaire);
        setFrequenceKm(frequenceKm);
    }

    /**
     * Constructeur de chargement depuis une ligne CSV.
     * @param champs tableau [idEntretien, idVehicule, idTypeEntretien, coutUnitaire, frequenceKm]
     */
    public Entretien(String[] champs) {
        this(
            champs[0],
            champs[1],
            champs[2],
            Double.parseDouble(champs[3].trim()),
            Integer.parseInt(champs[4].trim())
        );
    }

    // ----- Getters -----
    public String getIdEntretien()     { return idEntretien; }
    public String getIdVehicule()      { return idVehicule; }
    public String getIdTypeEntretien() { return idTypeEntretien; }
    public double getCoutUnitaire()    { return coutUnitaire; }
    public int getFrequenceKm()        { return frequenceKm; }

    // ----- Setters avec validation -----

    public void setIdEntretien(String idEntretien) {
        if (idEntretien == null || idEntretien.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant de l'entretien est obligatoire.");
        }
        this.idEntretien = idEntretien;
    }

    public void setIdVehicule(String idVehicule) {
        if (idVehicule == null || idVehicule.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant du véhicule est obligatoire.");
        }
        this.idVehicule = idVehicule;
    }

    public void setIdTypeEntretien(String idTypeEntretien) {
        if (idTypeEntretien == null || idTypeEntretien.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant du type d'entretien est obligatoire.");
        }
        this.idTypeEntretien = idTypeEntretien;
    }

    public void setCoutUnitaire(double coutUnitaire) {
        if (coutUnitaire < 0) {
            throw new IllegalArgumentException("Le coût unitaire ne peut pas être négatif.");
        }
        this.coutUnitaire = coutUnitaire;
    }

    public void setFrequenceKm(int frequenceKm) {
        if (frequenceKm <= 0) {
            throw new IllegalArgumentException("La fréquence en km doit être strictement positive.");
        }
        this.frequenceKm = frequenceKm;
    }

    /**
     * Calcul ligne : coût annuel théorique de cet entretien
     * en fonction du kilométrage parcouru dans l'année.
     *
     * Exemple : pneus à 600 € tous les 40 000 km, véhicule qui roule 20 000 km/an
     *          → 600 × (20000 / 40000) = 300 € / an
     *
     * @param kmAnnuels kilomètres parcourus dans l'année
     * @return coût annuel en euros
     */
    public double getCoutAnnuel(int kmAnnuels) {
        return coutUnitaire * ((double) kmAnnuels / frequenceKm);
    }
}
