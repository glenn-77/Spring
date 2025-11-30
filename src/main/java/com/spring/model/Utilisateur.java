package com.spring.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    // ICI LE POINT IMPORTANT
    @Column(name = "motDePasse", nullable = false)
    private String motDePasse;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne
    @JoinColumn(name = "employe_id")
    private Employe employe;

    @Override
    public String toString() {
        return login + " (" + role.getNomRole() + ")";
    }
}
