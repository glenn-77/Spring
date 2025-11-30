package com.spring.controller;

import com.spring.model.Utilisateur;
import com.spring.service.UtilisateurService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurService utilisateurService;

    // >>> PAGE PAR DÉFAUT : /
    @GetMapping("/")
    public String root(HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login"; // non connecté -> page de login
        }

        return "redirect:/index";     // connecté -> page d'accueil
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String login,
                        @RequestParam String motDePasse,
                        HttpSession session,
                        Model model) {

        Utilisateur user = utilisateurService.login(login, motDePasse);

        if (user == null) {
            model.addAttribute("error", "Identifiants invalides");
            return "login";
        }
        session.setAttribute("user", user);
        session.setAttribute("role", user.getRole().getNomRole().name());
        return "redirect:/index";    //important : on renvoie vers /index
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
