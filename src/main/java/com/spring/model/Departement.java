package com.spring.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "departement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    // Chef du département (employé unique)
    @OneToOne
    @JoinColumn(name = "chef_id")
    private Employe chef;

    // Employés du département
    @OneToMany(mappedBy = "departement", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Employe> employes = new HashSet<>();

    public void addEmploye(Employe e) {
        employes.add(e);
        e.setDepartement(this);
    }

    public void removeEmploye(Employe e) {
        employes.remove(e);
        e.setDepartement(null);
    }

    @Override
    public String toString() {
        return "Departement{" + "id=" + id + ", nom='" + nom + '\'' + '}';
    }
}
