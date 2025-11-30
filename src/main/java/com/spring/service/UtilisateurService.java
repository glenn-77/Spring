package com.spring.service;

import com.spring.repository.*;
import com.spring.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;

    public Utilisateur login(String login, String motDePasse) {
        return utilisateurRepository.login(login, motDePasse);
    }

    public Utilisateur createUser(String login, String password, NomRole role) {
        Role r = roleRepository.findByNomRole(role);

        Utilisateur u = Utilisateur.builder()
                .login(login)
                .motDePasse(password)
                .role(r)
                .build();

        return utilisateurRepository.save(u);
    }
}

