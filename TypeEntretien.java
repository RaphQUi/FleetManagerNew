import java.util.ArrayList;
import java.util.List;

/**
 * Représente un type d'entretien (pneus, freins, révision, vidange).
 */
public class TypeEntretien {

    private String idTypeEntretien;
    private String libelle;

    // Relation : entretiens de ce type
    private List<Entretien> entretiens;

    /**
     * Constructeur classique.
     * @param idTypeEntretien identifiant unique du type d'entretien
     * @param libelle libellé lisible (ex : "pneus")
     */
    public TypeEntretien(String idTypeEntretien, String libelle) {
        this.entretiens = new ArrayList<>();
        setIdTypeEntretien(idTypeEntretien);
        setLibelle(libelle);
    }

    /**
     * Constructeur de chargement depuis une ligne CSV.
     * @param champs tableau [idTypeEntretien, libelle]
     */
    public TypeEntretien(String[] champs) {
        this(champs[0], champs[1]);
    }

    // ----- Getters -----
    public String getIdTypeEntretien() { return idTypeEntretien; }
    public String getLibelle()         { return libelle; }
    public List<Entretien> getEntretiens() { return entretiens; }

    // ----- Setters avec validation -----

    public void setIdTypeEntretien(String idTypeEntretien) {
        if (idTypeEntretien == null || idTypeEntretien.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant du type d'entretien est obligatoire.");
        }
        this.idTypeEntretien = idTypeEntretien;
    }

    public void setLibelle(String libelle) {
        if (libelle == null || libelle.trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé est obligatoire.");
        }
        this.libelle = libelle;
    }

    /**
     * Ajoute un entretien à la liste de ce type.
     * @param entretien entretien à ajouter
     */
    public void addEntretien(Entretien entretien) {
        if (entretien != null) {
            entretiens.add(entretien);
        }
    }
}
