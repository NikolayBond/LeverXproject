package by.nik.controllers;

import by.nik.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import by.nik.dao.UserDAO;

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
    public String createNewUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "users/registration";
        }

        if (userDAO.create(user)){
            return "users/link_confirmation_registration";
        } else {
            return "users/error_database";
        }
    }

    @GetMapping("/confirm/:{confirmationCode}")
    public String confirmationLink(@PathVariable("confirmationCode") Integer confirmationCode) {
        if (userDAO.isConfirmedRegistration(confirmationCode)) {
            return "users/congratulations_success_registration";
        } else {
            return "users/error_confirmation_link";
        }
    }


    @GetMapping("/forgot_password")
    public String userForgotPassword(){
            return "users/forgot_password";
        }

    @PostMapping("/forgot_password")
    public String confirmationCodeForgotPassword(@RequestParam("email") String email, Model model){
        if (userDAO.isExistEmailInActiveUserSet(email)){
            UUID uniqueKey = UUID.randomUUID();
            model.addAttribute("confirmationCode", uniqueKey.hashCode());
            userDAO.setPasswordRecoveryLink(uniqueKey.hashCode(), email);

            return "users/link_confirmation_password_ recovery";

        } else {
            return "users/error_email_absent";}
    }

    @GetMapping("/check_code/:{confirmationCode}")
    public String passwordResetCheckCode(@PathVariable("confirmationCode") int confirmationCode, Model model){
        model.addAttribute("confirmationKey", confirmationCode);
        if (userDAO.isPasswordRecoveryLink(confirmationCode)) {
            return "users/password_reset";
        } else {
            return "users/error_confirmation_link";
        }
    }

    @PostMapping("/reset")
    public String passwordReset(@RequestParam("new_password") String new_password,
                                @RequestParam("confirmationCode") int confirmationCode) {
        if (userDAO.isPasswordRecoveryLink(confirmationCode)) {

            userDAO.isPasswordReset(confirmationCode, new_password);
            // добавить if по try из DAO
            return "users/congratulations_success_password_reset";
        } else {
            return "users/error_confirmation_link";
        }
    }

}
