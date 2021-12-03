package by.nik.controllers;

import by.nik.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import by.nik.dao.UserDAO;
import redis.clients.jedis.exceptions.JedisDataException;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class UserRegistrationController {
    private final UserDAO userDAO;

    @Autowired
    public UserRegistrationController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/registration")
    public String newUserRegistration(@ModelAttribute("user") User user){
            return "users/registration";
        }

    @PostMapping()
    public String createNewUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "users/registration";
        }

        String confirmationCode = userDAO.createConfirmationCode();
        model.addAttribute("confirmationCode", confirmationCode);

        if (userDAO.create(user, confirmationCode)){
            return "users/link_confirmation_registration";
        } else {
            return "errors/error_redis_database";
        }
    }

    @GetMapping("/confirm/:{confirmationCode}")
    public String confirmationLink(@PathVariable("confirmationCode") String confirmationCode, @ModelAttribute("user") User user) {
        if (userDAO.isConfirmedRegistrationLink(confirmationCode, user)) {

            if (userDAO.save(user)) {
                return "users/congratulations_success_registration";
            } else {
                return "errors/error_database";
            }
        } else {
            return "errors/error_confirmation_link";
        }
    }


    @GetMapping("/forgot_password")
    public String userForgotPassword(){
            return "users/forgot_password";
        }

    @PostMapping("/forgot_password")
    public String confirmationCodeForgotPassword(@RequestParam("email") String email, Model model){
        if (userDAO.isExistEmailInUserTable(email)){
            UUID uniqueKey = UUID.randomUUID();
            model.addAttribute("confirmationCode", uniqueKey.toString());
            if (!userDAO.setPasswordRecoveryLink(uniqueKey.toString(), email)) {
                return "errors/error_redis_database";
            };
            return "users/link_confirmation_password_ recovery";
        } else {
            return "users/error_email_absent";}
    }

    @GetMapping("/check_code/:{confirmationCode}")
    public String passwordResetCheckCode(@PathVariable("confirmationCode") String confirmationCode, Model model){
        model.addAttribute("confirmationKey", confirmationCode);
        if (userDAO.isPasswordRecoveryLink(confirmationCode)) {
            return "users/password_reset";
        } else {
            return "errors/error_confirmation_link";
        }
    }

    @PostMapping("/reset")
    public String passwordReset(@RequestParam("new_password") String new_password,
                                @RequestParam("confirmationCode") String confirmationCode) {
        if (userDAO.isPasswordRecoveryLink(confirmationCode)) {

            try {
                userDAO.isPasswordReset(confirmationCode, new_password);
                // добавить if по try из DAO (all Exception)
            } catch (Exception e) {
// Добавить действие
            }
            return "users/congratulations_success_password_reset";
        } else {
            return "errors/error_confirmation_link";
        }
    }

}
