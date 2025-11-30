package com.spring.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String matricule;

    @Column(nullable = false)
    private String poste;

    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.JUNIOR;

    @Column(nullable = false)
    private String adresse;
    private String telephone;

    @Column(nullable = false)
    private double salaireBase;

    private LocalDate dateEmbauche;

    @Column(unique = true)
    private String email;

    // Relation ManyToOne : un employé appartient à un département
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id")
    @JsonBackReference
    private Departement departement;

    // Relation ManyToMany avec les projets
    @ManyToMany(mappedBy = "employes", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Projet> projets = new HashSet<>();

    // Relation OneToMany avec les fiches de paie
    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FicheDePaie> fichesPaie = new HashSet<>();

    @Transient
    private String roleNom;

    //Méthodes utilitaires
    public void addProjet(Projet p) {
        projets.add(p);
        p.getEmployes().add(this);
    }

    public void removeProjet(Projet p) {
        projets.remove(p);
        p.getEmployes().remove(this);
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + poste + ")";
    }
}