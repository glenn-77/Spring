package com.spring.controller;

import com.spring.model.*;
import com.spring.service.*;
import com.spring.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/employes")
public class EmployeController {

    private final EmployeService employeService;
    private final DepartementService departementService;
    private final ProjetService projetService;
    private final UtilisateurRepository utilisateurRepository;

    // Liste des employés + recherche
    @GetMapping
    public String listEmployes(@RequestParam(value = "q", required = false) String q,
                               Model model) {
        List<Employe> employes = (q == null || q.isEmpty())
                ? employeService.getAll()
                : employeService.search(q);

        model.addAttribute("employes", employes);
        model.addAttribute("query", q);
        return "employes"; // /WEB-INF/jsp/employes.html
    }

    // Formulaire de création
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employe", new Employe());
        model.addAttribute("departements", departementService.getAll());
        return "employes-form"; // /WEB-INF/jsp/employes-form.html
    }

    // Formulaire d'édition
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employe e = employeService.getById(id);
        if (e == null) {
            return "redirect:/employes";
        }
        model.addAttribute("employe", e);
        model.addAttribute("departements", departementService.getAll());
        return "employes-form";
    }

    // Création / mise à jour
    @PostMapping("/save")
    public String saveEmploye(@ModelAttribute Employe employe,
                              @RequestParam(value = "departementId", required = false) Long departementId,
                              @RequestParam(value = "role", required = false) NomRole nomRole) {

        if (departementId != null) {
            Departement d = departementService.getById(departementId);
            employe.setDepartement(d);
        }

        boolean isNew = (employe.getId() == null);
        Employe saved = isNew
                ? employeService.createEmploye(employe)
                : employeService.updateEmploye(employe.getId(), employe);

        if (!isNew && nomRole != null) {
            employeService.updateUserRole(saved.getId(), nomRole);
        }


        if (isNew && nomRole != null) {
            employeService.createUserForEmploye(saved, nomRole);
        }


        return "redirect:/employes";
    }

    // Suppression
    @GetMapping("/delete/{id}")
    public String deleteEmploye(@PathVariable Long id) {
        employeService.deleteEmploye(id);
        return "redirect:/employes";
    }

    // Page d'affectation projets (équivalent affectations.html)
    @GetMapping("/{id}/projets")
    public String showAffectationForm(@PathVariable Long id, Model model) {
        Employe e = employeService.getById(id);
        if (e == null) return "redirect:/employes";

        model.addAttribute("employe", e);
        model.addAttribute("projets", projetService.getAll());
        return "affectations"; // /WEB-INF/jsp/affectations.html
    }

    // Enregistrement des affectations projets
    @PostMapping("/{id}/projets")
    public String saveAffectations(@PathVariable Long id,
                                   @RequestParam(value = "projetsIds", required = false) List<Long> projetsIds) {
        if (projetsIds == null) projetsIds = List.of();
        employeService.assignProjects(id, projetsIds);
        return "redirect:/employes";
    }

    // TRI PAR GRADE
    @GetMapping("/sort/grade")
    public String sortByGrade(Model model) {
        model.addAttribute("employes", employeService.sortByGrade());
        return "employes";
    }

    // TRI PAR POSTE
    @GetMapping("/sort/poste")
    public String sortByPoste(Model model) {
        model.addAttribute("employes", employeService.sortByPoste());
        return "employes";
    }

    // EMPLOYÉS PAR DÉPARTEMENT
    @GetMapping("/departement/{id}")
    public String listByDepartement(@PathVariable Long id, Model model) {
        model.addAttribute("employes", employeService.getByDepartement(id));
        return "employes";
    }

    // CHEFS PAR DÉPARTEMENT (JSON)
    @GetMapping("/chefs/{deptId}")
    @ResponseBody
    public List<Employe> getChefsProjet(@PathVariable Long deptId) {
        return employeService.getByDepartement(deptId).stream()
                .filter(e -> {
                    Utilisateur u = utilisateurRepository.findByEmploye_Id(e.getId());
                    return (u != null && u.getRole().getNomRole() == NomRole.CHEF_DE_PROJET);
                })
                .toList();
    }


    @GetMapping("/{id}/salaire")
    @ResponseBody
    public Double getSalaireBase(@PathVariable Long id) {
        Employe e = employeService.getById(id);
        return (e != null) ? e.getSalaireBase() : null;
    }

    @GetMapping("/departement/{id}/non-chefs")
    @ResponseBody
    public List<EmployeDTO> getEmployesNonChefs(@PathVariable Long id) {
        return employeService.getByDepartement(id).stream()
                .filter(e -> {
                    Utilisateur u = utilisateurRepository.findByEmploye_Id(e.getId());
                    return (u == null || u.getRole().getNomRole() != NomRole.CHEF_DE_PROJET);
                })
                .map(e -> new EmployeDTO(e.getId(), e.getNom(), e.getPrenom()))
                .toList();
    }



}