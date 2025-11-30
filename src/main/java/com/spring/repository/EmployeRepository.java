package com.spring.repository;

import com.spring.model.Employe;
import com.spring.model.Departement;
import com.spring.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {

    Employe findByEmail(String email);

    List<Employe> findAllByOrderByGradeAsc();

    List<Employe> findAllByOrderByPosteAsc();

    List<Employe> findByDepartement_Id(Long idDepartement);

    @Query("""
       SELECT e FROM Employe e
       WHERE LOWER(e.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(e.matricule) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(e.departement.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
       """)
    List<Employe> search(String keyword);

    List<Employe> findByDepartement_NomContainingIgnoreCase(String nomDept);

    @Query("SELECT e FROM Employe e LEFT JOIN FETCH e.departement")
    List<Employe> findAllWithDepartement();

}
