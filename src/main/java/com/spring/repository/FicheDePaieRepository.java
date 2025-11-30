package com.spring.repository;

import com.spring.model.FicheDePaie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FicheDePaieRepository extends JpaRepository<FicheDePaie, Long> {

    List<FicheDePaie> findByEmploye_Id(Long employeId);

    List<FicheDePaie> findByEmploye_IdAndDateGenerationBetween(
            Long employeId, LocalDate debut, LocalDate fin
    );

    List<FicheDePaie> findByDateGenerationBetween(LocalDate debut, LocalDate fin);

    @Query("""
       SELECT f FROM FicheDePaie f
       LEFT JOIN FETCH f.employe e
       LEFT JOIN FETCH e.departement
       WHERE f.id = :id
       """)
    FicheDePaie findByIdWithDetails(Long id);

    @Query("""
       SELECT f FROM FicheDePaie f
       LEFT JOIN FETCH f.employe e
       LEFT JOIN FETCH e.departement
       """)
    List<FicheDePaie> findAllWithDetails();

}
