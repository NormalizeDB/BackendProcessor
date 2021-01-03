package com.normalizedb.security.services;

import com.normalizedb.security.entities.PrincipalUser;
import com.normalizedb.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void saveUser(PrincipalUser user) {
        repository.save(user);
    }

    public void updateUserRoles(String oldRole, String newRole) {
        repository.updateRole(oldRole, newRole);
    }
}
