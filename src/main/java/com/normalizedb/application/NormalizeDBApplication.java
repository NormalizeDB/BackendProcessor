package com.normalizedb.application;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.normalizedb"
}, exclude = { ErrorMvcAutoConfiguration.class })
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(basePackages = {
        "com.normalizedb.security.repositories"
})
public class NormalizeDBApplication {
    public static void main(String[] args){
        try {
            ApplicationContext context = SpringApplication.run(NormalizeDBApplication.class, args);
            System.out.println();
        } catch(BeanCreationException creationException) {
            System.out.println(creationException.getMostSpecificCause().getLocalizedMessage());
        }
    }
}
