package com.biblioteca.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserLoginController {

    @GetMapping("/login")
    public String login() {
        return "user/login";
        // Esto apunta a:
        // src/main/resources/templates/user/login.html
    }
}

