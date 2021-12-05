package by.nik.models;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;

@Component
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotEmpty(message = "Field shouldn't be empty")
    private String message;
    private Integer post_id; // UID
    private Integer author_id; // UID
    private Timestamp created_at;
    private Boolean approved;

    public Comment() {
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.approved = false;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public void setAuthor_id(Integer author_id) {
        this.author_id = author_id;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Integer getPost_id() {
        return post_id;
    }

    public Integer getAuthor_id() {
        return author_id;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public Boolean getApproved() {
        return approved;
    }
}
