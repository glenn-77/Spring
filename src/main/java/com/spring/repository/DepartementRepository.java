package com.spring.repository;

import com.spring.model.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {

    Departement findByChef_Id(Long chefId);

    Departement findByEmployes_Id(Long employeId);

    Departement findByNomIgnoreCase(String nom);

    @Query("SELECT d FROM Departement d LEFT JOIN FETCH d.employes LEFT JOIN FETCH d.chef")
    List<Departement> findAllWithDetails();

    @Query("SELECT d FROM Departement d LEFT JOIN FETCH d.employes LEFT JOIN FETCH d.chef WHERE d.id = :id")
    Departement findByIdWithDetails(Long id);

}
