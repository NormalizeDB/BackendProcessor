package com.normalizedb.security.services;

import com.normalizedb.security.entities.PrincipalUser;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.normalizedb.security.repositories.UserRepository;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        boolean shouldThrow = false;
        Throwable exception = null;
        Optional<PrincipalUser> user = Optional.empty();
        try {
            user = userRepository.findUserByUsername(username);
            if(!user.isPresent()) {
                shouldThrow = true;
            }
        } catch(Exception ex) {
            exception = ex;
            shouldThrow = true;
        }
        if(shouldThrow) {
            throw new UsernameNotFoundException(null, exception);
        }
        PrincipalUser principalUser = user.get();
        return User.withUsername(principalUser.getUsername())
                .password(principalUser.getPassword())
                .roles(principalUser.getRole())
                .build();
    }
}
