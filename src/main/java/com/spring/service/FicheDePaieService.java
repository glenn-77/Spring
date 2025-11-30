package com.spring.service;

import com.spring.repository.*;
import com.spring.model.*;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FicheDePaieService {

    private final FicheDePaieRepository ficheRepository;
    private final EmployeRepository employeRepository;

    public List<FicheDePaie> getAll() {
        return ficheRepository.findAllWithDetails();
    }

    public FicheDePaie getByIdWithDetails(Long id) {
        return ficheRepository.findByIdWithDetails(id);
    }

    public List<FicheDePaie> getAllWithDetails() {
        return ficheRepository.findAllWithDetails();
    }

    public FicheDePaie getById(Long id) {
        return ficheRepository.findById(id).orElse(null);
    }

    public FicheDePaie create(FicheDePaie fiche, Long employeId) {
        Employe e = employeRepository.findById(employeId).orElse(null);
        fiche.setEmploye(e);
        return ficheRepository.save(fiche);
    }

    public FicheDePaie update(Long id, FicheDePaie data) {
        FicheDePaie f = getById(id);
        if (f == null) return null;

        f.setPrime(data.getPrime());
        f.setDeduction(data.getDeduction());
        f.setMois(data.getMois());
        f.setAnnee(data.getAnnee());
        f.calculNetAPayer();

        return ficheRepository.save(f);
    }

    public void delete(Long id) {
        ficheRepository.deleteById(id);
    }

    // Recherche par employé + dates
    public List<FicheDePaie> search(Long employeId, LocalDate start, LocalDate end) {
        return ficheRepository.findByEmploye_IdAndDateGenerationBetween(employeId, start, end);
    }

    public boolean canAccessFiche(FicheDePaie f, Employe user, String role) {

        if (f == null || user == null || role == null) return false;

        switch (role) {
            case "ADMINISTRATEUR":
                return true;

            case "CHEF_DE_DEPARTEMENT":
            case "CHEF_DE_PROJET":
            case "EMPLOYE":
                return f.getEmploye().getId().equals(user.getId());

            default:
                return false;
        }
    }

}
