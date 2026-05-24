import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MainAnalyse {

    private static final String SEPARATEUR = ",";

    public static void main(String[] args) throws IOException {
        // ========== CHARGEMENT (identique à Main) ==========
        Map<String, TypeEnergie> typesEnergie = new HashMap<>();
        for (String[] c : lireCsv("data/typeEnergie.csv")) {
            TypeEnergie t = new TypeEnergie(c);
            typesEnergie.put(t.getIdTypeEnergie(), t);
        }
        Map<String, TypeEntretien> typesEntretien = new HashMap<>();
        for (String[] c : lireCsv("data/typeEntretien.csv")) {
            TypeEntretien t = new TypeEntretien(c);
            typesEntretien.put(t.getIdTypeEntretien(), t);
        }
        Map<String, Vehicule> vehicules = new HashMap<>();
        for (String[] c : lireCsv("data/vehicule.csv")) {
            Vehicule v = new Vehicule(c);
            vehicules.put(v.getIdVehicule(), v);
        }
        Map<String, Entretien> entretiens = new HashMap<>();
        for (String[] c : lireCsv("data/entretien.csv")) {
            Entretien e = new Entretien(c);
            entretiens.put(e.getIdEntretien(), e);
        }
        List<UtilisationAnnuelle> utilisations = new ArrayList<>();
        for (String[] c : lireCsv("data/utilisationAnnuelle.csv")) {
            utilisations.add(new UtilisationAnnuelle(c));
        }

        // Relations
        for (Vehicule v : vehicules.values()) {
            TypeEnergie te = typesEnergie.get(v.getIdTypeEnergie());
            if (te != null) { v.setTypeEnergie(te); te.addVehicule(v); }
        }
        for (Entretien e : entretiens.values()) {
            Vehicule v = vehicules.get(e.getIdVehicule());
            if (v != null) v.addEntretien(e);
            TypeEntretien t = typesEntretien.get(e.getIdTypeEntretien());
            if (t != null) t.addEntretien(e);
        }
        for (UtilisationAnnuelle u : utilisations) {
            Vehicule v = vehicules.get(u.getIdVehicule());
            if (v != null) { u.setVehicule(v); v.addUtilisationAnnuelle(u); }
        }

        System.out.println("============================================");
        System.out.println("3. SOMME — TCO total + km totaux");
        System.out.println("============================================");
        double tcoTotal = 0; int kmTotal = 0;
        for (UtilisationAnnuelle u : utilisations) {
            tcoTotal += u.getTcoAnnuel();
            kmTotal += u.getKmParcourus();
        }
        System.out.printf("TCO total flotte (3 ans) : %,.2f €%n", tcoTotal);
        System.out.printf("Km total flotte (3 ans)  : %,d km%n", kmTotal);
        System.out.printf("Cout par km global       : %.4f €/km%n%n", tcoTotal / kmTotal);

        System.out.println("============================================");
        System.out.println("4. MOYENNE — coût/km arithmétique vs pondéré");
        System.out.println("============================================");
        double sommeCoutKm = 0;
        for (UtilisationAnnuelle u : utilisations) sommeCoutKm += u.getCoutParKm();
        double moyArith = sommeCoutKm / utilisations.size();
        double moyPond = tcoTotal / kmTotal;
        System.out.printf("Moyenne arithmétique : %.4f €/km%n", moyArith);
        System.out.printf("Moyenne pondérée     : %.4f €/km%n", moyPond);
        System.out.printf("Écart                : %.4f €/km%n%n", moyArith - moyPond);

        System.out.println("============================================");
        System.out.println("5. EXTRÊMES — coût/km min et max");
        System.out.println("============================================");
        UtilisationAnnuelle min = null, max = null;
        for (UtilisationAnnuelle u : utilisations) {
            if (min == null || u.getCoutParKm() < min.getCoutParKm()) min = u;
            if (max == null || u.getCoutParKm() > max.getCoutParKm()) max = u;
        }
        System.out.printf("MIN : %s %s en %d : %.4f €/km sur %d km%n",
            min.getVehicule().getMarque(), min.getVehicule().getModele(),
            min.getAnnee(), min.getCoutParKm(), min.getKmParcourus());
        System.out.printf("MAX : %s %s en %d : %.4f €/km sur %d km%n%n",
            max.getVehicule().getMarque(), max.getVehicule().getModele(),
            max.getAnnee(), max.getCoutParKm(), max.getKmParcourus());

        System.out.println("============================================");
        System.out.println("6. COMPTAGE — véhicules par énergie");
        System.out.println("============================================");
        Map<String, Integer> nbParEnergie = new HashMap<>();
        for (Vehicule v : vehicules.values()) {
            String k = v.getTypeEnergie().getLibelle();
            nbParEnergie.merge(k, 1, Integer::sum);
        }
        for (Map.Entry<String, Integer> e : nbParEnergie.entrySet())
            System.out.printf("  %-15s : %d%n", e.getKey(), e.getValue());
        System.out.println();

        System.out.println("============================================");
        System.out.println("7. REGROUPEMENT — TCO par énergie");
        System.out.println("============================================");
        Map<String, Double> tcoParEnergie = new HashMap<>();
        Map<String, Integer> kmParEnergie = new HashMap<>();
        for (UtilisationAnnuelle u : utilisations) {
            String k = u.getVehicule().getTypeEnergie().getLibelle();
            tcoParEnergie.merge(k, u.getTcoAnnuel(), Double::sum);
            kmParEnergie.merge(k, u.getKmParcourus(), Integer::sum);
        }
        for (String k : tcoParEnergie.keySet()) {
            double tco = tcoParEnergie.get(k);
            int km = kmParEnergie.get(k);
            System.out.printf("  %-15s : TCO %,12.2f € | %,8d km | %.4f €/km%n",
                k, tco, km, tco / km);
        }
        System.out.println();

        System.out.println("============================================");
        System.out.println("8. GRAPHIQUE — TCO cumulé par véhicule");
        System.out.println("============================================");
        Map<String, Double> tcoParVeh = new LinkedHashMap<>();
        for (Vehicule v : vehicules.values()) {
            double tco = 0;
            for (UtilisationAnnuelle u : v.getUtilisationsAnnuelles()) tco += u.getTcoAnnuel();
            tcoParVeh.put(v.getIdVehicule() + " " + v.getMarque() + " " + v.getModele(), tco);
        }
        double maxTco = 0;
        for (double t : tcoParVeh.values()) if (t > maxTco) maxTco = t;
        for (Map.Entry<String, Double> e : tcoParVeh.entrySet()) {
            int n = (int) Math.round((e.getValue() / maxTco) * 40);
            StringBuilder bar = new StringBuilder();
            for (int i = 0; i < n; i++) bar.append("#");
            System.out.printf("%-35s %-40s %,10.0f €%n", e.getKey(), bar, e.getValue());
        }
    }

    private static List<String[]> lireCsv(String chemin) throws IOException {
        List<String[]> res = new ArrayList<>();
        List<String> lignes = Files.readAllLines(Path.of(chemin), StandardCharsets.UTF_8);
        for (int i = 1; i < lignes.size(); i++) {
            String l = lignes.get(i).trim();
            if (!l.isEmpty()) res.add(l.split(SEPARATEUR));
        }
        return res;
    }
}
