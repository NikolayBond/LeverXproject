package by.nik.models;

import org.springframework.stereotype.Component;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.Date;

@Component
@Entity
@Table(name = "gameObjects")
public class Post {
    public enum Status {CHECKED, UNCHECKED};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //UID Integer

    @NotEmpty(message = "Field shouldn't be empty")
    private String title;
    @NotEmpty(message = "Field shouldn't be empty")
    private String text; // must be TEXT
    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer author_id; //UID Integer
    private Date created_at;
    private Date updated_at;
    private Integer game_id; //UID Integer

    public Post() {
        this.created_at =  new Timestamp(System.currentTimeMillis());
        this.updated_at =  new Timestamp(System.currentTimeMillis());
        this.status = Status.UNCHECKED;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAuthor_id(Integer author_id) {
        this.author_id = author_id;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public void setGame_id(Integer game_id) {
        this.game_id = game_id;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getAuthor_id() {
        return author_id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public Integer getGame_id() {
        return game_id;
    }
}
