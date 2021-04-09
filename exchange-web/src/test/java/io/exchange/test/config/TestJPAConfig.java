package io.exchange.test.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import io.exchange.core.config.JPAConfig;

@Configuration
public class TestJPAConfig { 
//extends JPAConfig {
//
//    @Override
//    public DataSource dataSource() {
//        EmbeddedDatabase datasource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
//        return datasource;
//    }
//
//    @Override
//    protected Properties additionalProperties() {
//        Properties properties = super.additionalProperties();
//        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//        properties.setProperty("hibernate.format_sql", "true");
//        return properties;
//    }
}
