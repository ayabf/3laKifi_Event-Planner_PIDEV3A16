/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author mahmo
 */
public class Inscription {
    
    private int id_inscrit;
    private int id_user;
    private String nom;
    private String prenom;
    private String email;
    private LocalDateTime dateNow=LocalDateTime.now();
    
    
    
    



    public Inscription() {
    }

    public Inscription(int id_inscrit, int id_user, String nom, String prenom, String email   ) {
        this.id_inscrit = id_inscrit;
        this.id_user = id_user;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;

    }

    public int getId_inscrit() {
        return id_inscrit;
    }

    public void setId_inscrit(int id_inscrit) {
        this.id_inscrit = id_inscrit;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDateNow() {
        return dateNow;
    }

    public void setDateNow(LocalDateTime dateNow) {
        this.dateNow = dateNow;
    }


    public Inscription(int id_user, String nom, String prenom, String email) {
        this.id_user = id_user;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;

    }

    @Override
    public String toString() {
        return "Inscription{" + "id_inscrit=" + id_inscrit + ", id_user=" + id_user + ", nom=" + nom + ", prenom=" + prenom + ", email=" + email + ", dateNow=" + dateNow + ", formation_id="  + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.id_inscrit;
        hash = 17 * hash + this.id_user;
        hash = 17 * hash + Objects.hashCode(this.nom);
        hash = 17 * hash + Objects.hashCode(this.prenom);
        hash = 17 * hash + Objects.hashCode(this.email);
        hash = 17 * hash + Objects.hashCode(this.dateNow);

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Inscription other = (Inscription) obj;
        if (this.id_inscrit != other.id_inscrit) {
            return false;
        }
        if (this.id_user != other.id_user) {
            return false;
        }

        if (!Objects.equals(this.nom, other.nom)) {
            return false;
        }
        if (!Objects.equals(this.prenom, other.prenom)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.dateNow, other.dateNow)) {
            return false;
        }

        return true;
    }



    
}
