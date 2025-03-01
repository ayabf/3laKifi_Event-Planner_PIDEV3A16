package Models;


public class SessionCommande {
    public static int id_commande;

    // Définir l'ID de la commande en cours
    public static void setCommande(int id) {
        id_commande = id;
    }

    // Réinitialiser la session commande
    public static void clearSession() {
        id_commande = 0;
    }

    // Vérifier si une commande est active
    public static boolean isCommandeActive() {
        return id_commande > 0;
    }
}
