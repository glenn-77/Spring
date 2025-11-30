package com.spring.controller;

import com.spring.model.*;
import com.spring.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/departements")
public class DepartementController {

    private final DepartementService departementService;
    private final EmployeService employeService;

    @GetMapping
    public String listDepartements(Model model, HttpSession session) {

        Utilisateur u = (Utilisateur) session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (u == null || role == null)
            return "redirect:/login";

        Employe emp = u.getEmploye();

        List<Departement> result;

        switch (role) {
            case "ADMINISTRATEUR":
                result = departementService.getAll();
                break;

            case "CHEF_DE_DEPARTEMENT":
            case "CHEF_DE_PROJET":
            case "EMPLOYE":
                Departement d = emp.getDepartement();
                if (d != null) {
                    result = List.of(departementService.getByIdWithDetails(d.getId()));
                } else {
                    result = List.of();
                }
                break;

            default:
                result = List.of();
        }

        model.addAttribute("departements", result);
        return "departements";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("departement", new Departement());
        model.addAttribute("employes", employeService.getAll());
        return "departements-form"; // departements-form.html
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {

        Utilisateur u = (Utilisateur) session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (u == null || role == null)
            return "redirect:/login";

        Departement d = departementService.getByIdWithDetails(id);
        if (d == null)
            return "redirect:/departements";

        //Sécurité
        if (!departementService.canAccessDepartement(d, u.getEmploye(), role)) {
            return "redirect:/departements?denied=true";
        }

        model.addAttribute("departement", d);
        model.addAttribute("employes", employeService.getAll());
        return "departements-form";
    }

    @PostMapping("/save")
    public String saveDepartement(@ModelAttribute Departement departement,
                                  @RequestParam(value = "chefId", required = false) Long chefId,
                                  @RequestParam(value = "employesIds", required = false) List<Long> employesIds,
                                  HttpSession session) {

        Utilisateur u = (Utilisateur) session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (u == null)
            return "redirect:/login";

        // Si update -> vérifier accès
        if (departement.getId() != null) {
            Departement existing = departementService.getById(departement.getId());
            if (!departementService.canAccessDepartement(existing, u.getEmploye(), role)) {
                return "redirect:/departements?denied=true";
            }
        } else {
            // Création de département réservée à l'admin
            if (!role.equals("ADMINISTRATEUR")) {
                return "redirect:/departements?denied=true";
            }
        }

        boolean isNew = (departement.getId() == null);
        Departement saved = isNew
                ? departementService.create(departement)
                : departementService.update(departement.getId(), departement);

        // Chef
        if (chefId != null) {
            departementService.setChef(saved.getId(), chefId);
        }

        // Employés du département
        if (employesIds != null) {
            departementService.assignEmployes(saved.getId(), employesIds);
        }

        return "redirect:/departements";
    }

    @GetMapping("/delete/{id}")
    public String deleteDepartement(@PathVariable Long id,  HttpSession session) {
        Utilisateur u = (Utilisateur) session.getAttribute("user");
        String role = (String) session.getAttribute("role");

        if (u == null) return "redirect:/login";

        Departement d = departementService.getById(id);
        if (d == null) return "redirect:/departements";

        //Delete -> uniquement admin
        if (!role.equals("ADMINISTRATEUR")) {
            return "redirect:/departements?denied=true";
        }

        departementService.delete(id);
        return "redirect:/departements";
    }
}