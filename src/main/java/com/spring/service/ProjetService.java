package com.spring.service;

import java.util.List;
import com.spring.model.*;
import com.spring.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjetService {

    private final ProjetRepository projetRepository;
    private final EmployeRepository employeRepository;
    private final DepartementRepository departementRepository;

    public List<Projet> getAll() {
        return projetRepository.findAllWithDetails();
    }

    public Projet getByIdWithDetails(Long id) {
        return projetRepository.findByIdWithDetails(id);
    }

    public Projet getById(Long id) {
        return projetRepository.findById(id).orElse(null);
    }

    public Projet create(Projet p) {
        return projetRepository.save(p);
    }

    public Projet update(Long id, Projet data) {
        Projet p = getById(id);
        if (p == null) return null;

        p.setNom(data.getNom());
        p.setDescription(data.getDescription());
        p.setBudget(data.getBudget());
        p.setEtat(data.getEtat());
        p.setDateDebut(data.getDateDebut());
        p.setDateFin(data.getDateFin());
        return projetRepository.save(p);
    }

    public void delete(Long id) {
        projetRepository.deleteById(id);
    }

    // Gérer chef de projet
    public void setChefProjet(Long projetId, Long chefId) {
        Projet p = getById(projetId);
        Employe chef = employeRepository.findById(chefId).orElse(null);

        if (p == null || chef == null) return;

        p.setChefProjet(chef);
        projetRepository.save(p);
    }

    // Affectation employés
    public void assignEmployes(Long projetId, List<Long> empIds) {
        Projet p = getById(projetId);
        if (p == null) return;

        p.getEmployes().clear();

        for (Long id : empIds) {
            Employe e = employeRepository.findById(id).orElse(null);
            if (e != null) {
                p.addEmploye(e);
            }
        }

        projetRepository.save(p);
    }

    public List<Projet> getProjetsForUser(Employe user, String role) {

        switch (role) {

            case "ADMINISTRATEUR":
                return projetRepository.findAll();

            case "CHEF_DE_DEPARTEMENT":
                if (user.getDepartement() == null) return List.of();
                return projetRepository.findByDepartement(user.getDepartement());

            case "CHEF_DE_PROJET":
                return projetRepository.findByChefProjet(user);

            case "EMPLOYE":
                return projetRepository.findByEmployes_Id(user.getId());

            default:
                return List.of();
        }
    }

    public boolean canAccessProject(Projet p, Employe user, String role) {

        if (p == null || user == null || role == null) return false;

        switch (role) {

            case "ADMINISTRATEUR":
                return true;

            case "CHEF_DE_DEPARTEMENT":
                return p.getDepartement() != null &&
                        user.getDepartement() != null &&
                        p.getDepartement().getId().equals(user.getDepartement().getId());

            case "CHEF_DE_PROJET":
                return p.getChefProjet() != null &&
                        p.getChefProjet().getId().equals(user.getId());

            case "EMPLOYE":
                return p.getEmployes()
                        .stream()
                        .anyMatch(e -> e.getId().equals(user.getId()));

            default:
                return false;
        }
    }


}

