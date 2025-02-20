package Models;

public enum ReportStatus {
    PENDING,  // ✅ En attente
    APPROVED, // ✅ Approuvé
    REJECTED, // ✅ Rejeté
    VALIDE;   // ✅ Validé

    // 🔹 Convertit une chaîne en `ReportStatus` (évite les erreurs de casse)
    public static ReportStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return PENDING; // ✅ Valeur par défaut
        }
        try {
            return ReportStatus.valueOf(status.trim().toUpperCase()); // ✅ Supprime espaces et met en majuscules
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Statut invalide : `" + status + "`. Utilisation de PENDING par défaut.");
            return PENDING;
        }
    }
}
