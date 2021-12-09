package by.nik.controllers;

import by.nik.models.User;
import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import by.nik.dao.UserDAO;
import redis.clients.jedis.exceptions.JedisException;

import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class UsersController {

    private final UserDAO userDAO;
    public UsersController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody User user) {
        if ((user.getFirst_name() == "") || (user.getFirst_name() == null)) {
            return new ResponseEntity<>("field 'first_name' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
        if ((user.getLast_name() == "") || (user.getLast_name() == null)) {
            return new ResponseEntity<>("field 'last_name' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
        if ((user.getPassword() == null) || (user.getPassword().length() < 4)) {
            return new ResponseEntity<>("Password shouldn't be empty and must be >=4 characters", HttpStatus.BAD_REQUEST);
        }
        if ((user.getEmail() == "") || (user.getEmail() == null)) {
            return new ResponseEntity<>("field 'email' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
        String emailRegex = "^(.+)@(.+)$";
        //"^[A-Za-z0-9+_.-]+@[a-zA-Z0-9.-]+$"
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(user.getEmail()).matches()) {
            return new ResponseEntity<>("Not valid email", HttpStatus.BAD_REQUEST);
        }
        try {
            if (userDAO.isPresent(user.getEmail())) {
                return new ResponseEntity<>("Duplicate - this email already present", HttpStatus.BAD_REQUEST);
            }

            UUID uniqueKey = UUID.randomUUID();
            String confirmationCode = uniqueKey.toString();

            if (userDAO.create(user, confirmationCode)) {
                return new ResponseEntity<>("follow this link to confirm registration /auth/confirm/" + confirmationCode, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Redis database error", HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }


    @GetMapping("/confirm/{confirmationCode}")
    public ResponseEntity<?> confirmationLink(@PathVariable("confirmationCode") String confirmationCode, @ModelAttribute("user") User user) {
        try {
            if (userDAO.isConfirmedRegistrationLink(confirmationCode, user)) {
                userDAO.save(user);
                return new ResponseEntity<>("Congratulations on successful registration !", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Confirmation link УСТАЛЕЛ или НЕВЕРЕН", HttpStatus.I_AM_A_TEAPOT);
            }

        } catch (JedisException | HibernateException j) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /*
    стандартному сценарию./auth/forgot_password POST {email} отправлять письмо
    с кодом
/auth/reset POST {code, new_password} брать из редиса код и если он верен,
    то устанавливать новый пароль
/auth/check_code GET {code} проверять актуальность кода сброса и возвращать   в ответ
     */

    @PostMapping("/forgot_password")
    public ResponseEntity<?> createPasswordRecoveryLink(@RequestBody User user) {
        if ((user.getEmail() == "") || (user.getEmail() == null)) {
            return new ResponseEntity<>("field 'email' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (!pattern.matcher(user.getEmail()).matches()) {
            return new ResponseEntity<>("Not valid email", HttpStatus.BAD_REQUEST);
        }
        try {
            if (userDAO.readByEmail(user.getEmail()).size() > 0) {
                UUID uniqueKey = UUID.randomUUID();
                userDAO.setPasswordRecoveryLink(uniqueKey.toString(), user.getEmail());
                return new ResponseEntity<>("POST this code: " + uniqueKey.toString() + " and new_password to /auth/reset", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Email not found in database (no user with this email).", HttpStatus.BAD_REQUEST);
            }
        } catch (JedisException | HibernateException j) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }


    @PostMapping("/reset")
    public ResponseEntity<?> passwordReset(@RequestParam("new_password") String new_password,
                                           @RequestParam("code") String code) {
        if ((new_password == null) || (new_password.length() < 4)) {
            return new ResponseEntity<>("Password shouldn't be empty and must be >=4 characters", HttpStatus.BAD_REQUEST);
        }
        try {
            if (userDAO.isPasswordRecoveryLink(code)) {
                userDAO.isPasswordReset(code, new_password);
                return new ResponseEntity<>("reset password ok!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Confirmation link УСТАЛЕЛ или НЕВЕРЕН", HttpStatus.I_AM_A_TEAPOT);
            }
        } catch (JedisException | HibernateException j) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/check_code")
    public ResponseEntity<?> checkCode(@RequestParam("code") String code) {
        try {
            if (userDAO.isPasswordRecoveryLink(code)) {
                return new ResponseEntity<>("Confirmation link ok!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Confirmation link УСТАЛЕЛ или НЕВЕРЕН", HttpStatus.I_AM_A_TEAPOT);
            }
        } catch (JedisException j) {
            return new ResponseEntity<>("Redis database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
