package com.spring.service;

import com.spring.repository.*;
import com.spring.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartementService {

    private final DepartementRepository departementRepository;
    private final EmployeRepository employeRepository;

    // CRUD
    public List<Departement> getAll() {
        return departementRepository.findAllWithDetails();
    }

    public Departement getByIdWithDetails(Long id) {
        return departementRepository.findByIdWithDetails(id);
    }

    public Departement getById(Long id) {
        return departementRepository.findById(id).orElse(null);
    }

    public Departement create(Departement d) {
        return departementRepository.save(d);
    }

    public Departement update(Long id, Departement data) {
        Departement d = getById(id);
        if (d == null) return null;

        d.setNom(data.getNom());
        return departementRepository.save(d);
    }

    public void delete(Long id) {
        departementRepository.deleteById(id);
    }

    // Modifier chef : logique de tes servlets
    public void setChef(Long departementId, Long chefId) {
        Departement d = getById(departementId);
        Employe chef = employeRepository.findById(chefId).orElse(null);

        if (d == null || chef == null) return;

        // Déchéance de l’ancien chef s’il existait
        Departement oldDept = departementRepository.findByChef_Id(chefId);
        if (oldDept != null && !oldDept.getId().equals(departementId)) {
            oldDept.setChef(null);
            departementRepository.save(oldDept);
        }

        d.setChef(chef);
        departementRepository.save(d);
    }

    // Affecter employés au département
    public void assignEmployes(Long deptId, List<Long> empIds) {
        Departement dept = getById(deptId);
        if (dept == null) return;

        // vider
        dept.getEmployes().forEach(e -> e.setDepartement(null));

        for (Long id : empIds) {
            Employe e = employeRepository.findById(id).orElse(null);
            if (e != null) {
                e.setDepartement(dept);
                employeRepository.save(e);
            }
        }
    }

    public boolean canAccessDepartement(Departement d, Employe user, String role) {

        if (d == null || user == null || role == null) return false;

        switch (role) {

            case "ADMINISTRATEUR":
                return true;

            case "CHEF_DE_DEPARTEMENT":
                return user.getDepartement() != null &&
                        d.getId().equals(user.getDepartement().getId());

            case "CHEF_DE_PROJET":
            case "EMPLOYE":
                // Ils peuvent seulement voir leur propre département
                return user.getDepartement() != null &&
                        d.getId().equals(user.getDepartement().getId());

            default:
                return false;
        }
    }

}

