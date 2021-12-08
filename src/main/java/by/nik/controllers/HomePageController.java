package by.nik.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {
//    @GetMapping()
//    public String homePage() {
//        return "home/index";
//    }
    @GetMapping()
    public String homePage(@AuthenticationPrincipal User user) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(user);
//org.springframework.security.core.userdetails.User [Username=admin, Password=[PROTECTED], Enabled=true, AccountNonExpired=true, credentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ROLE_USER]]
        System.out.println(user.getUsername()); //admin
        System.out.println(user.getAuthorities()); //[ROLE_USER]
        System.out.println(user.getPassword()); // null
        return "home/index";
    }

}
