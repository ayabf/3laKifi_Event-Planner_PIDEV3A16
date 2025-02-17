package Models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reports {
    private int report_id,publication_id,user_id;
    private String description;
    private LocalDate report_date;
    private ReportStatus status;

    public Reports() {}

    public Reports(int publication_id, int user_id, String description, ReportStatus status) {
        this.publication_id = publication_id;
        this.user_id = user_id;
        this.description = description;
        this.status = ReportStatus.PENDING;
    }

    public Reports(int report_id, int publication_id, int user_id,
                   String description, ReportStatus status, LocalDate report_date) {
        this.report_id = report_id;
        this.publication_id = publication_id;
        this.user_id = user_id;
        this.description = description;
        this.status = status;
        this.report_date = report_date;
    }

    public int getReport_id() {
        return report_id;
    }

    public int getPublication_id() {
        return publication_id;
    }

    public int getUser_id() {
        return user_id;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reports{" +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
