package Models;

import java.time.LocalDate;

public class Reports {
    private int report_id,publication_id,uder_id;
    private String motif,description;
    private LocalDate report_date;
    private ReportStatus status;

    public Reports() {}

    public Reports(int publication_id, int uder_id, String motif, String description, ReportStatus status) {
        this.publication_id = publication_id;
        this.uder_id = uder_id;
        this.motif = motif;
        this.description = description;
        this.status = ReportStatus.EN_ATTENTE;
    }

    public Reports(int report_id, int publication_id, int uder_id, String motif, String description, ReportStatus status, LocalDate report_date) {
        this.report_id = report_id;
        this.publication_id = publication_id;
        this.uder_id = uder_id;
        this.motif = motif;
        this.description = description;
        this.status = status;
        this.report_date = report_date;
    }

    public int getReport_id() {
        return report_id;
    }

    public String getMotif() {
        return motif;
    }

    public int getPublication_id() {
        return publication_id;
    }

    public int getUder_id() {
        return uder_id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getReport_date() {
        return report_date;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }
}
