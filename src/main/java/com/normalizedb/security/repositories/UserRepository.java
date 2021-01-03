package com.normalizedb.security.repositories;

import com.normalizedb.security.entities.PrincipalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<PrincipalUser, String> {
    Optional<PrincipalUser> findUserByUsername(String username);
    @Modifying()
    @Query(value = "UPDATE PrincipalUser X SET X.role = :newRole WHERE X.role = :oldRole")
    void updateRole(@Param("oldRole") String oldRole, @Param("newRole") String newRole);
}
