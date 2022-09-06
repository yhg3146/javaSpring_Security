package com.sp.fc.web.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String main(Model model, HttpSession session){
        model.addAttribute("sessionId", session.getId());
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "loginForm";
    }

    @GetMapping("/login-required")
    public String loginRequired(){
        return "LoginRequired";
    }

<<<<<<< HEAD

=======
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
    @GetMapping("/login-error")
    public String loginError(Model model){
        model.addAttribute("loginError", true);
        return "loginForm";
    }

    @GetMapping("/access-denied")
    public String accessDenied(){

        return "AccessDenied";
    }

    @GetMapping("/access-denied2")
    public String accessDenied2(){
<<<<<<< HEAD
        return "AccessDenied2";
    }


    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/user-page")
    public String userPage() throws YouCannotAccessUserPage {
        if(true) throw new YouCannotAccessUserPage();
=======

        return "AccessDenied2";
    }
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/user-page")
    public String userPage(){
        if(true){
            throw new YouCannotAccessUserPage();
        }
>>>>>>> 78788ae22eccf611607554ca685cebd2e2e28fd5
        return "UserPage";
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/admin-page")
    public String adminPage(){
        return "AdminPage";
    }


    @ResponseBody
    @GetMapping("/auth")
    public Authentication auth(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
