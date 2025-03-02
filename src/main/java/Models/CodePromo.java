package Models;

import java.util.Date;

public class CodePromo {
    private int id;
    private String code;
    private double pourcentage;
    private Date dateCreation;
    private Date dateExpiration;

    // ✅ Constructeur complet
    public CodePromo(int id, String code, double pourcentage, Date dateCreation, Date dateExpiration) {
        this.id = id;
        this.code = code;
        this.pourcentage = pourcentage;
        this.dateCreation = dateCreation;
        this.dateExpiration = dateExpiration;
    }
    public CodePromo(int id, String code, double pourcentage, Date dateExpiration) {
        this.id = id;
        this.code = code;
        this.pourcentage = pourcentage;
        this.dateExpiration = dateExpiration;
    }

    // ✅ Constructeur sans ID (utilisé lors de l'ajout d'un nouveau code promo)
    public CodePromo(String code, double pourcentage, Date dateCreation, Date dateExpiration) {
        this.code = code;
        this.pourcentage = pourcentage;
        this.dateCreation = dateCreation;
        this.dateExpiration = dateExpiration;
    }

    // ✅ Constructeur par défaut
    public CodePromo() {}

    // ✅ Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public double getPourcentage() { return pourcentage; }
    public void setPourcentage(double pourcentage) { this.pourcentage = pourcentage; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Date getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(Date dateExpiration) { this.dateExpiration = dateExpiration; }

    @Override
    public String toString() {
        return "CodePromo{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", pourcentage=" + pourcentage +
                ", dateCreation=" + dateCreation +
                ", dateExpiration=" + dateExpiration +
                '}';
    }
}
