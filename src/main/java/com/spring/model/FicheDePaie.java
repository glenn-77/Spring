package com.spring.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "fiche_de_paie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FicheDePaie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int mois;
    private int annee;

    @Column(nullable = false)
    private Double salaireBase;

    private Double prime = 0.0;
    private Double deduction = 0.0;

    @Transient
    private Double netAPayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employe_id")
    private Employe employe;

    @Column(name = "date_generation")
    private LocalDate dateGeneration;

    @PrePersist
    @PreUpdate
    public void calculNetAPayer() {
        if (salaireBase == null) salaireBase = 0.0;
        if (prime == null) prime = 0.0;
        if (deduction == null) deduction = 0.0;

        this.netAPayer = salaireBase + prime - deduction;

        if (dateGeneration == null)
            dateGeneration = LocalDate.now();
    }

    public Double getNetAPayer() {
        if (netAPayer == null) calculNetAPayer();
        return netAPayer;
    }

    @Override
    public String toString() {
        return "FicheDePaie " + mois + "/" + annee + " - " + employe.getNom();
    }
}
