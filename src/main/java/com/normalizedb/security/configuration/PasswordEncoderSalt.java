package com.normalizedb.security.configuration;

import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderSalt implements SaltSource {

    @Override
    public Object getSalt(UserDetails user) {
        return user.getUsername().hashCode();
    }
}
