package Models;
import java.time.LocalDate;
import java.util.List;


public class Publications {
    private int publication_id,id_user;
    private String title,description,image_url,statut;
    private LocalDate publication_date;
    private List <Comments> comments;
    public Publications(String hhh, String bc, String ggggg, String number) {}

    public Publications(int publication_id, int id_user, String title, String description, String image_url, LocalDate publication_date) {
        this.publication_id = publication_id;
        this.id_user = id_user;
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.publication_date = publication_date;
    }

    public Publications(int publication_id, int id_user, String title, String description, String image_url) {
        this.publication_id = publication_id;
        this.id_user = id_user;
        this.title = title;
        this.description = description;
        this.image_url = image_url;
    }

    public Publications(int publication_id, String title, String description, String image_url) {
        this.publication_id = publication_id;
        this.title = title;
        this.description = description;
        this.image_url = image_url;

    }

    public Publications(String title, String description, String image_url) {
        this.title = title;
        this.description = description;
        this.image_url = image_url;
    }

    public Publications(String title, String description, String image_url, int id_user) {
        this.title = title;
        this.description = description;
        this.image_url = image_url;
        this.id_user = id_user;
    }

    public int getPublication_id() {
        return publication_id;
    }

    public int getId_user() {
        return id_user;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage_url() {
        return image_url;
    }

    public LocalDate getPublication_date() {
        return publication_date;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setPublication_date(LocalDate publication_date) {
        this.publication_date = publication_date;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Publications{" +
                "publication_id=" + publication_id +
                ", id_user=" + id_user +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image_url='" + image_url + '\'' +
                ", publication_date=" + publication_date +
                ", comments=" + comments +
                '}';
    }

    public void setStatut(String statut) {
        this.statut =statut;
    }

    public String getStatut() {
        return statut;
    }
}
