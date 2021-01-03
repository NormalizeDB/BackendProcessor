package com.normalizedb.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class PersistenceConfiguration {

    @Autowired
    private DataSource dataSource;

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName("normalizeDB");
        factory.setDataSource(dataSource);
        factory.setPackagesToScan("com.normalizedb");
        HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
        //Update DB from configured application layer tables
        vendor.setShowSql(true);
        vendor.setGenerateDdl(true);
        vendor.setDatabase(Database.POSTGRESQL);
        factory.setJpaVendorAdapter(vendor);
        return factory;
    }

}
