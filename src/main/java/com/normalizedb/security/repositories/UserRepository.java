package com.normalizedb.security.repositories;

import com.normalizedb.security.entities.PrincipalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<PrincipalUser, Long> {
    Optional<PrincipalUser> findUserByUsername(String username);
}
