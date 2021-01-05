package com.normalizedb.configuration;

import com.normalizedb.controllers.NormalizeController;
import com.normalizedb.handlers.GenericExceptionHandler;
import com.normalizedb.security.controllers.AdminController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        registerClasses(NormalizeController.class,
                        AdminController.class,
                        GenericExceptionHandler.class);
    }

}
