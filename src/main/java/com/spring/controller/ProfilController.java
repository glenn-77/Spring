package com.spring.controller;


import com.spring.model.*;
import com.spring.service.EmployeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;      // ✔ IMPORT IMPORTANT
import org.springframework.web.bind.annotation.RequestParam; // ✔ IMPORT IMPORTANT


@Controller
@RequiredArgsConstructor
public class ProfilController {

    private final EmployeService employeService;

    @GetMapping("/profil")
    public String profil(HttpSession session, Model model) {
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Employe employe = employeService.getEmployeWithDetails(user.getEmploye().getId());

        model.addAttribute("user", user);
        model.addAttribute("employe", employe);

        return "profil";
    }

    @PostMapping("/profil/updatePassword")
    public String updatePassword(HttpSession session,
                                 @RequestParam String newPassword,
                                 Model model) {

        Utilisateur user = (Utilisateur) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // Mise à jour du mot de passe
        user.setMotDePasse(newPassword);
        employeService.updatePassword(user); // méthode à créer si tu ne l’as pas

        model.addAttribute("message", "Mot de passe mis à jour avec succès !");
        model.addAttribute("user", user);
        model.addAttribute("employe", employeService.getEmployeWithDetails(user.getEmploye().getId()));

        return "profil"; // retourne vers la page profil
    }


}
