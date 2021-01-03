package com.normalizedb.application;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.normalizedb"
})
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(basePackages = {
        "com.normalizedb.security.repositories"
})
public class NormalizeDBApplication {
    public static void main(String[] args){
        try {
            SpringApplication.run(NormalizeDBApplication.class, args);
        } catch(BeanCreationException creationException) {
            System.out.println(creationException.getMostSpecificCause().getLocalizedMessage());
        }
    }
}
