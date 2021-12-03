package by.nik.models;

import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
//               только для Традер пока нет секурити!!!
// Это нужно ТОЛЬКО ПОКА НЕТ СЕКУРИТИ

@Component
public class Login {
    @NotEmpty(message = "Field shouldn't be empty")
    @Size(min = 8, message = "Password must be >=8 characters")
    private String password;
    @NotEmpty(message = "Field shouldn't be empty")
    @Email(message = "Not valid email")
    private String email;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
