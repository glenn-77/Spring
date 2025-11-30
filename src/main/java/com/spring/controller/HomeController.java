package com.spring.controller;

import com.spring.model.Utilisateur;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/index")
    public String index(HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        model.addAttribute("user", user);
        System.out.println("USER = " + user);
        if (user != null) System.out.println("EMPLOYE = " + user.getEmploye());
        return "index"; // index.html (Thymeleaf)
    }
}
