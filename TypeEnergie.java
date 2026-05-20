import java.util.ArrayList;
import java.util.List;

/**
 * Représente un type d'énergie (Électrique, Essence, Diesel).
 */
public class TypeEnergie {

    private String idTypeEnergie;
    private String libelle;
    private String unite;          // kWh ou L
    private double prixParUnite;   // prix en € de l'unité

    // Relation : véhicules qui utilisent cette énergie
    private List<Vehicule> vehicules;

    /**
     * Constructeur classique.
     * @param idTypeEnergie identifiant unique
     * @param libelle libellé lisible (ex : "Électrique")
     * @param unite unité de mesure (ex : "kWh", "L")
     * @param prixParUnite prix unitaire en euros
     */
    public TypeEnergie(String idTypeEnergie, String libelle, String unite, double prixParUnite) {
        this.vehicules = new ArrayList<>();
        setIdTypeEnergie(idTypeEnergie);
        setLibelle(libelle);
        setUnite(unite);
        setPrixParUnite(prixParUnite);
    }

    /**
     * Constructeur de chargement depuis une ligne CSV.
     * @param champs tableau [idTypeEnergie, libelle, unite, prixParUnite]
     */
    public TypeEnergie(String[] champs) {
        this(
            champs[0],
            champs[1],
            champs[2],
            Double.parseDouble(champs[3].trim())
        );
    }

    // ----- Getters -----
    public String getIdTypeEnergie() { return idTypeEnergie; }
    public String getLibelle()       { return libelle; }
    public String getUnite()         { return unite; }
    public double getPrixParUnite()  { return prixParUnite; }
    public List<Vehicule> getVehicules() { return vehicules; }

    // ----- Setters avec validation -----

    public void setIdTypeEnergie(String idTypeEnergie) {
        if (idTypeEnergie == null || idTypeEnergie.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant du type d'énergie est obligatoire.");
        }
        this.idTypeEnergie = idTypeEnergie;
    }

    public void setLibelle(String libelle) {
        if (libelle == null || libelle.trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé est obligatoire.");
        }
        this.libelle = libelle;
    }

    public void setUnite(String unite) {
        if (unite == null || unite.trim().isEmpty()) {
            throw new IllegalArgumentException("L'unité est obligatoire.");
        }
        this.unite = unite;
    }

    public void setPrixParUnite(double prixParUnite) {
        if (prixParUnite < 0) {
            throw new IllegalArgumentException("Le prix par unité ne peut pas être négatif.");
        }
        this.prixParUnite = prixParUnite;
    }

    /**
     * Ajoute un véhicule à la liste des véhicules utilisant cette énergie.
     * @param vehicule véhicule à ajouter
     */
    public void addVehicule(Vehicule vehicule) {
        if (vehicule != null) {
            vehicules.add(vehicule);
        }
    }
}
