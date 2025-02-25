package Models;
import java.time.LocalDate;

public class Reunion {
    private int id;
    private String objectif;
    private LocalDate dateReunion;
    private String description;
    private String fichierPv;

    // Constructeurs
    public Reunion() {}

    public Reunion(int id, String objectif, LocalDate dateReunion, String description, String fichierPv) {
        this.id = id;
        this.objectif = objectif;
        this.dateReunion = dateReunion;
        this.description = description;
        this.fichierPv = fichierPv;
    }

    public Reunion(String objectif, LocalDate dateReunion, String description, String fichierPv) {
        this.objectif = objectif;
        this.dateReunion = dateReunion;
        this.description = description;
        this.fichierPv = fichierPv;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    public LocalDate getDateReunion() { return dateReunion; }
    public void setDateReunion(LocalDate dateReunion) { this.dateReunion = dateReunion; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFichierPv() { return fichierPv; }
    public void setFichierPv(String fichierPv) { this.fichierPv = fichierPv; }

    @Override
    public String toString() {
        return "Reunion{" +
                "id=" + id +
                ", objectif='" + objectif + '\'' +
                ", dateReunion=" + dateReunion +
                ", description='" + description + '\'' +
                ", fichierPv='" + fichierPv + '\'' +
                '}';
    }
}
