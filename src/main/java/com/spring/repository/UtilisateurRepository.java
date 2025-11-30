package com.spring.repository;

import com.spring.model.Utilisateur;
import com.spring.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Utilisateur findByLoginAndMotDePasse(String login, String motDePasse);

    Utilisateur findByLogin(String login);

    Utilisateur findByEmploye_Id(Long employeId);

    List<Utilisateur> findByRole(Role role);

    @Query("""
       SELECT u FROM Utilisateur u
       LEFT JOIN FETCH u.employe
       LEFT JOIN FETCH u.role
       WHERE u.login = :login AND u.motDePasse = :motDePasse
       """)
    Utilisateur login(@Param("login") String login,
                      @Param("motDePasse") String motDePasse);
}
