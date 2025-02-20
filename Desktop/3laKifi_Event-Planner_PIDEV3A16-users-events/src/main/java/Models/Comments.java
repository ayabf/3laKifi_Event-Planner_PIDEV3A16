package Models;
import java.time.LocalDateTime;

public class Comments {
    private int comment_id,publication_id,id_user;
    private String content;
    private LocalDateTime comment_date;
    private String publicationTitle;
    private String username;

    public Comments() {
    }

    public Comments(String content, int id_user, LocalDateTime comment_date) {
        this.content = content;
        this.id_user = id_user;
        this.comment_date = comment_date;
    }

    public Comments(String publicationTitle, String username, String content) {
        this.publicationTitle = publicationTitle;
        this.username = username;
        this.content = content;
    }


    public Comments(int comment_id, int publication_id, int id_user, String content, LocalDateTime comment_date) {
        this.comment_id = comment_id;
        this.publication_id = publication_id;
        this.id_user = id_user;
        this.content = content;
        this.comment_date = comment_date;
    }

    public int getComment_id() {
        return comment_id;
    }

    public int getPublication_id() {
        return publication_id;
    }

    public int getId_user() {
        return id_user;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getComment_date() {
        return comment_date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return " Publication: " + publicationTitle +
                "\n Utilisateur: " + username +
                "\n Commentaire: \"" + content + "\"" +
                "\n--------------------------------------";
    }


}
