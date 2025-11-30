package com.spring.repository;

import com.spring.model.Role;
import com.spring.model.NomRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByNomRole(NomRole nomRole);
}
