package by.nik.models;

import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Component
public class User {

    public enum Role {ADMIN, TRADER, ANONIM};

    private Integer id; //UID


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
    private Date created_at;
    private Role role;

    public User() {
        UUID uniqueKey = UUID.randomUUID();
        this.id = uniqueKey.hashCode();

        this.created_at = new Date();
        this.role = Role.TRADER;
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

    public void setId(Integer id) {
        this.id = id;
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

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
