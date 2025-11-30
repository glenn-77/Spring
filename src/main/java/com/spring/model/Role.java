package com.spring.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private NomRole nomRole;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<Utilisateur> users = new ArrayList<>();

    @Override
    public String toString() {
        return nomRole.name();
    }
}
