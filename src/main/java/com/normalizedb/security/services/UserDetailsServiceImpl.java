package com.normalizedb.security.services;

import com.normalizedb.security.entities.PrincipalUser;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<PrincipalUser> user = userRepository.findUserByUsername(username);
        if(!user.isPresent()) {
            throw new UsernameNotFoundException(String.format("The following username: {%s} cannot be found", username));
        }
        PrincipalUser principalUser = user.get();
        return new User(principalUser.getUsername(),
                principalUser.getPassword(),
                Collections.emptyList());
    }
}
