package com.normalizedb.security.repositories;

import com.normalizedb.security.entities.AuthorizedRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<AuthorizedRole, String> {
}
