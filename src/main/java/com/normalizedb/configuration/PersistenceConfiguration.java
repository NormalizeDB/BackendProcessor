package com.normalizedb.configuration;

import com.normalizedb.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
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

    private final SecurityConstants constants;
    @Autowired
    public PersistenceConfiguration(SecurityConstants constants) {
        this.constants = constants;
    }

    @Primary
    @Bean(name = "test-normalizedb")
    public DataSource fetchDataSource() {
        DataSourceBuilder sourceBuilder = DataSourceBuilder.create();
        sourceBuilder.url(constants.getDataSourceUrl());
        sourceBuilder.username(constants.getDataSourceUsername());
        sourceBuilder.password(constants.getDataSourcePassword());
        sourceBuilder.driverClassName("org.postgresql.Driver");
        return sourceBuilder.build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName("normalizeDB");
        factory.setDataSource(fetchDataSource());
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
