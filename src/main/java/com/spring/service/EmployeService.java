package com.spring.service;

import com.spring.model.*;
import com.spring.repository.*;
import com.spring.utils.EmailService;
import com.spring.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmployeService {

    private final EmployeRepository employeRepository;
    private final ProjetRepository projetRepository;
    private final DepartementRepository departementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    // ----------- CRUD -----------
    public List<Employe> getAll() {
        List<Employe> list = employeRepository.findAllWithDepartement();
        list.forEach(e -> {
            Utilisateur u = utilisateurRepository.findByEmploye_Id(e.getId());
            if (u != null) {
                e.setRoleNom(u.getRole().getNomRole().name());
            }
        });
        return list;
    }

    public Employe getById(Long id) {
        return employeRepository.findById(id).orElse(null);
    }

    public Employe createEmploye(Employe employe) {
        if (employe.getMatricule() == null || employe.getMatricule().isEmpty()) {
            employe.setMatricule(generateMatricule());
        }

        Departement d = departementRepository.findByNomIgnoreCase("Administration");
        if (d != null) {
            if(employe.getRoleNom().equals(NomRole.ADMINISTRATEUR.name())) employe.setDepartement(d);
        }

        if (employe.getDateEmbauche() == null)
            employe.setDateEmbauche(LocalDate.now());

        return employeRepository.save(employe);
    }

    public Employe updateEmploye(Long id, Employe data) {
        Employe e = getById(id);
        if (e == null) return null;

        e.setNom(data.getNom());
        e.setPrenom(data.getPrenom());
        e.setPoste(data.getPoste());
        e.setAdresse(data.getAdresse());
        e.setTelephone(data.getTelephone());
        e.setEmail(data.getEmail());
        e.setGrade(data.getGrade());
        e.setSalaireBase(data.getSalaireBase());

        if (data.getDepartement() != null)
            e.setDepartement(data.getDepartement());

        return employeRepository.save(e);
    }

    public void deleteEmploye(Long id) {
        Utilisateur u = utilisateurRepository.findByEmploye_Id(id);
        if (u != null) {
            utilisateurRepository.delete(u);
        }
        employeRepository.deleteById(id);

    }

    // ----------- SEARCH -----------
    public List<Employe> search(String keyword) {
        List<Employe> list = employeRepository.search(keyword);
        list.forEach(e -> {
            Utilisateur u = utilisateurRepository.findByEmploye_Id(e.getId());
            if (u != null) e.setRoleNom(u.getRole().getNomRole().name());
        });
        return list;
    }


    public List<Employe> sortByGrade() {
        List<Employe> list = employeRepository.findAllByOrderByGradeAsc();
        list.forEach(e -> {
            Utilisateur u = utilisateurRepository.findByEmploye_Id(e.getId());
            if (u != null) e.setRoleNom(u.getRole().getNomRole().name());
        });
        return list;
    }

    public List<Employe> sortByPoste() {
        List<Employe> list = employeRepository.findAllByOrderByPosteAsc();
        list.forEach(e -> {
            Utilisateur u = utilisateurRepository.findByEmploye_Id(e.getId());
            if (u != null) e.setRoleNom(u.getRole().getNomRole().name());
        });
        return list;
    }

    public List<Employe> getByDepartement(Long deptId) {
        List<Employe> list = employeRepository.findByDepartement_Id(deptId);
        list.forEach(e -> {
            Utilisateur u = utilisateurRepository.findByEmploye_Id(e.getId());
            if (u != null) e.setRoleNom(u.getRole().getNomRole().name());
        });
        return list;
    }

    // ----------- PROJETS -----------
    public void assignProjects(Long employeId, List<Long> projectIds) {
        Employe emp = getById(employeId);
        if (emp == null) return;

        emp.getProjets().clear();

        for (Long pid : projectIds) {
            Projet p = projetRepository.findById(pid).orElse(null);
            if (p != null) {
                emp.addProjet(p);
            }
        }

        employeRepository.save(emp);
    }

    @Transactional(readOnly = true)
    public Employe getEmployeWithDetails(Long id) {
        Employe e = employeRepository.findById(id).orElse(null);
        if (e != null) {
            // ⚡ Forcer le chargement des associations lazy
            if (e.getDepartement() != null) {
                e.getDepartement().getNom(); // déclenche le chargement
            }
            if (e.getProjets() != null) {
                e.getProjets().size(); // déclenche le chargement
            }
        }
        return e;
    }

    // ----------- UTILISATEUR -----------
    public void createUserForEmploye(Employe employe, NomRole nomRole) {
        Role role = roleRepository.findByNomRole(nomRole);
        if (role == null) return;

        Utilisateur u = Utilisateur.builder()
                .login(employe.getNom().charAt(0) + "." + employe.getPrenom())
                .motDePasse(PasswordUtil.generateRandomPassword(12))
                .role(role)
                .employe(employe)
                .build();

        handleChefRole(employe, nomRole);

        if (employe.getEmail() != null && !employe.getEmail().isEmpty()) {
            String sujet = "Création de votre compte - Gestion RH";
            String contenu = """
                    Bonjour %s,

                    Votre compte utilisateur a été créé sur la plateforme RH.

                    🔑 Identifiants de connexion :
                    • Login : %s
                    • Mot de passe : %s

                    Pensez à modifier votre mot de passe lors de votre première connexion.

                    Cordialement,
                    L'équipe RH
                    """.formatted(employe.getPrenom(), u.getLogin(), u.getMotDePasse());

            emailService.envoyerMail(employe.getEmail(), sujet, contenu);
        }

        utilisateurRepository.save(u);
    }

    public void updateUserRole(Long employeId, NomRole newRole) {

        Utilisateur u = utilisateurRepository.findByEmploye_Id(employeId);
        if (u == null) return;

        Role role = roleRepository.findByNomRole(newRole);
        if (role == null) return;

        u.setRole(role);
        utilisateurRepository.save(u);

        // appliquer la logique spéciale chef (chef département)
        handleChefRole(u.getEmploye(), newRole);
    }


    public void handleChefRole(Employe employe, NomRole role) {

        if (role != NomRole.CHEF_DE_DEPARTEMENT) return;
        if (employe.getDepartement() == null) return;

        Departement d = employe.getDepartement();

        // Retirer l'ancien chef si besoin
        if (d.getChef() != null && !d.getChef().getId().equals(employe.getId())) {
            d.setChef(null);
        }

        // Mettre ce nouvel employé comme chef
        d.setChef(employe);
        departementRepository.save(d);
    }

    private String generateMatricule() {
        return "EMP-" + (10000 + new Random().nextInt(90000));
    }

    public List<Employe> getChefsByDepartement(Long deptId) {
        return getByDepartement(deptId).stream()
                .filter(e -> utilisateurRepository.findByEmploye_Id(e.getId()) != null &&
                        utilisateurRepository.findByEmploye_Id(e.getId()).getRole().getNomRole() == NomRole.CHEF_DE_PROJET)
                .toList();
    }

    public void updatePassword(Utilisateur user) {
        if (user == null || user.getId() == null) return;
        utilisateurRepository.save(user);
    }

}

