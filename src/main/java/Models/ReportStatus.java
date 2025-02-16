package Models;

public enum ReportStatus {
    EN_ATTENTE("En attente"),
    VERIFIE("Vérifié"),
    REJETE("Rejeté");

    private final String value;

    ReportStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    public static ReportStatus fromString(String text) {
        for (ReportStatus s : ReportStatus.values()) {
            if (s.value.equalsIgnoreCase(text)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Statut inconnu : " + text);
    }
}
