package by.nik.models;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.UUID;


@Component
public class Trader {
    public enum Status {CHECKED, UNCHECKED};

    private String id; //UID Integer
    @NotEmpty(message = "Field shouldn't be empty")
    private String title;
    @NotEmpty(message = "Field shouldn't be empty")
    private String text; // must be TEXT
    private Status status;
    private String author_id; //UID Integer
    private Date created_at;
    private Date updated_at;
    private String game_id; //UID Integer

    public Trader() {
        UUID uniqueKey = UUID.randomUUID();
        this.id = uniqueKey.toString();
        this.created_at = new Date();
        this.updated_at = new Date();
        this.status = Status.UNCHECKED;

        // only for example
        this.author_id = "1";
        this.game_id = "1";
    }

    public void setId(String id) {
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

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getId() {
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

    public String getAuthor_id() {
        return author_id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public String getGame_id() {
        return game_id;
    }
}
