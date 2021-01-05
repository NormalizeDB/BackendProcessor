package com.normalizedb.security;

import com.normalizedb.security.services.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityEntityRunner implements ApplicationRunner {
    private RolesService rolesService;
    @Autowired
    public SecurityEntityRunner(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //When Spring's application context is loaded, but before application startup, pre-persist all the
        //applicable roles defined in the application layer
        rolesService.persistRoles();
    }
}
