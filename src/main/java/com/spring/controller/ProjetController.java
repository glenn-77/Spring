package com.spring.controller;

import com.spring.model.*;
import com.spring.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/projets")
public class ProjetController {

    private final ProjetService projetService;
    private final EmployeService employeService;
    private final DepartementService departementService;

    @GetMapping
    public String listProjets(Model model, HttpSession session) {

        Utilisateur u = (Utilisateur) session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (u == null || role == null) {
            return "redirect:/login";
        }

        Employe employe = u.getEmploye();

        List<Projet> projets = projetService.getProjetsForUser(employe, role);
        model.addAttribute("projets", projets);

        return "projets";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Projet p = new Projet();
        p.setEtat(EtatProjet.EN_COURS);

        model.addAttribute("projet", p);
        model.addAttribute("employes", employeService.getAll());
        model.addAttribute("departements", departementService.getAll());
        return "projets-form"; // projets-form.html
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {

        Utilisateur u = (Utilisateur) session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (u == null || role == null) {
            return "redirect:/login";
        }

        Projet p = projetService.getByIdWithDetails(id);
        if (p == null) return "redirect:/projets";

        // Vérification d'accès
        if (!projetService.canAccessProject(p, u.getEmploye(), role)) {
            return "redirect:/projets?denied=true";
        }

        model.addAttribute("projet", p);
        model.addAttribute("employes", employeService.getAll());
        model.addAttribute("departements", departementService.getAll());

        return "projets-form";
    }


    @PostMapping("/save")
    public String saveProjet(@RequestParam(value = "id", required = false) Long id,
                             @RequestParam String nom,
                             @RequestParam String description,
                             @RequestParam double budget,
                             @RequestParam("etat") EtatProjet etat,
                             @RequestParam(value = "dateDebut", required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                             @RequestParam(value = "dateFin", required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                             @RequestParam(value = "departementId", required = false) Long departementId,
                             @RequestParam(value = "chefProjetId", required = false) Long chefProjetId,
                             @RequestParam(value = "employesIds", required = false) List<Long> employesIds, HttpSession session) {

        Utilisateur u = (Utilisateur) session.getAttribute("user");
        String role = (String) session.getAttribute("role");
        Employe employe = u.getEmploye();

        Projet existing;
        if (id != null) {
            existing = projetService.getById(id);

            //Vérification
            if (!projetService.canAccessProject(existing, employe, role)) {
                return "redirect:/projets?denied=true";
            }
        }

        Projet data = new Projet();
        data.setNom(nom);
        data.setDescription(description);
        data.setBudget(budget);
        data.setEtat(etat);
        data.setDateDebut(dateDebut);
        data.setDateFin(dateFin);

        Projet saved;
        if (id == null) {
            saved = projetService.create(data);
        } else {
            saved = projetService.update(id, data);
        }

        if (departementId != null) {
            saved.setDepartement(departementService.getById(departementId));
        }

        if (chefProjetId != null) {
            projetService.setChefProjet(saved.getId(), chefProjetId);
        }

        if (employesIds != null) {
            projetService.assignEmployes(saved.getId(), employesIds);
        }

        return "redirect:/projets";
    }

    @GetMapping("/delete/{id}")
    public String deleteProjet(@PathVariable Long id, HttpSession session) {

        Utilisateur u = (Utilisateur) session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        Projet p = projetService.getById(id);
        if (p == null) return "redirect:/projets";

        //Vérification
        if (!projetService.canAccessProject(p, u.getEmploye(), role)) {
            return "redirect:/projets?denied=true";
        }

        projetService.delete(id);
        return "redirect:/projets";
    }

}