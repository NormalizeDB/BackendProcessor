package com.normalizedb.configuration;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.normalizedb.functions.RelationSchema;
import com.normalizedb.functions.RelationSchemaDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.ZoneOffset;
import java.util.TimeZone;

@Configuration
public class MapperConfig {

    @Primary
    @Bean("customMapper")
    public ObjectMapper getObjectMapper() {
        SimpleModule customModule = new SimpleModule();
        customModule.addDeserializer(RelationSchema.class, new RelationSchemaDeserializer());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(customModule);
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        return new ObjectMapper();
    }
}
