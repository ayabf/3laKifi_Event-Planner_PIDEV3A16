package Models;

import java.time.LocalDate;

public class Reports {
    private int report_id, publication_id, user_id;
    private String publicationTitle; // 🔹 Ajout du titre de la publication
    private String username; // 🔹 Ajout du username de l'utilisateur qui a signalé
    private String description;
    private LocalDate report_date;
    private ReportStatus status;

    public Reports() {}

    public Reports(int report_id, int publication_id, String publicationTitle, int user_id,
                   String username, String description, ReportStatus status, LocalDate report_date) {
        this.report_id = report_id;
        this.publication_id = publication_id;
        this.publicationTitle = publicationTitle;
        this.user_id = user_id;
        this.username = username;
        this.description = description;
        this.status = status;
        this.report_date = report_date;
    }

    // 🔹 Getters et Setters
    public int getReport_id() { return report_id; }
    public int getPublication_id() { return publication_id; }
    public String getPublicationTitle() { return publicationTitle; }
    public int getUser_id() { return user_id; }
    public String getUsername() { return username; }
    public String getDescription() { return description; }
    public LocalDate getReport_date() { return report_date; }
    public ReportStatus getStatus() { return status; }

    public void setStatus(ReportStatus status) { this.status = status; }
}
