package by.nik.models;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;

@Component
@Entity
@Table(name = "users")
public class User {

    public enum Role {ADMIN, TRADER, USER};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Field shouldn't be empty")
    private String first_name;
    @NotEmpty(message = "Field shouldn't be empty")
    private String last_name;
    @NotEmpty(message = "Field shouldn't be empty")
    @Size(min = 8, message = "Password must be >=8 characters")
    private String password;
    @NotEmpty(message = "Field shouldn't be empty")
    @Email(message = "Not valid email")
    private String email;
    private Timestamp created_at;
    @Enumerated(EnumType.STRING)
    private Role role;

// проверить используется ли этот блок, если нет - убрать
//    @OneToMany(mappedBy = "author_id", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<Trader> traders;

    public User() {
// must be USER ?
        this.role = Role.TRADER;
        this.created_at = new Timestamp(System.currentTimeMillis());
    }

    public Integer getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public Role getRole() {
        return role;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
