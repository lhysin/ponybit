package io.exchange.core.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.querydsl.codegen.ClassPathUtils;

import io.exchange.core.hibernate.executor.CustomJpaFactoryBean;
import io.exchange.domain.enums.Phase;
import io.exchange.domain.util.EnumUtils;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableTransactionManagement
@ComponentScan({ "io.exchange.core", "io.exchange.domain" })
@EnableJpaRepositories(basePackages = "io.exchange.core.hibernate.repository",
                       repositoryFactoryBeanClass = CustomJpaFactoryBean.class)
@PropertySource("classpath:application.yml")
@DependsOn("coreConfig")
@Slf4j
public class JPAConfig {

//    @Bean
//    public PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
//        return new PropertySourcesPlaceholderConfigurer();
//    }

    public JPAConfig() {
        super();
        try {
            // http://www.querydsl.com/static/querydsl/3.2.2/reference/html/ch04s02.html
            ClassPathUtils.scanPackage(Thread.currentThread().getContextClassLoader(), "io.exchange.domain");
        } catch (IOException e) {
            log.error("error:{}", e);
        }
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { "io.exchange.domain.util", "io.exchange.domain.hibernate" });
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean
    public DataSource dataSource() {
        Environment env = CoreConfig.env;
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));

        // file DB available
        // vm arguments -Dh2.isFileDb=true
        if (EnumUtils.hasContain(CoreConfig.currentPhase, Phase.LOCAL) && CoreConfig.isH2File) {
            // exchange-root/exchange-web/temp
            dataSource.setUrl("jdbc:h2:file:./temp/exchange;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        } else if (EnumUtils.hasContain(CoreConfig.currentPhase, Phase.DEV) && CoreConfig.isH2File) {
            dataSource.setDriverClassName("org.h2.jdbcx.JdbcDataSource");
            // exchange-root/exchange-web/temp
            dataSource.setUrl("jdbc:h2:file:./temp/exchange;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            dataSource.setUsername("local");
            dataSource.setPassword("");
        } else if (EnumUtils.hasContain(CoreConfig.currentPhase, Phase.PRD) && CoreConfig.isH2File) {
            dataSource.setDriverClassName("org.h2.jdbcx.JdbcDataSource");
            // exchange-root/exchange-web/temp
            dataSource.setUrl("jdbc:h2:file:./temp/exchange;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            dataSource.setUsername("local");
            dataSource.setPassword("");
        }

        return dataSource;
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    protected Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();

        String hibernateDialect = null;
        String hibernateHbm2ddlAuto = null;
        String hibernateFormatSql = null;

        // profile local, test setting
        if (EnumUtils.hasContain(CoreConfig.currentPhase, Phase.LOCAL, Phase.TEST)) {
            hibernateDialect = "org.hibernate.dialect.H2Dialect";
            hibernateFormatSql = "true";
            hibernateHbm2ddlAuto = "create-drop";

        } else {
            hibernateDialect = "org.hibernate.dialect.MySQL5InnoDBDialect";
            hibernateFormatSql = "false";
            hibernateHbm2ddlAuto = "update";
            //hibernateHbm2ddlAuto = "validate";
        }

        // for file DB
        if (EnumUtils.hasContain(CoreConfig.currentPhase, Phase.LOCAL, Phase.DEV)) {
            if(CoreConfig.isH2File) {
                hibernateHbm2ddlAuto = "update";
            }
        }

//        hibernateFormatSql = "false";
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
        hibernateProperties.setProperty("hibernate.dialect", hibernateDialect);
        hibernateProperties.setProperty("hibernate.format_sql", hibernateFormatSql);
        hibernateProperties.setProperty("hibernate.jdbc.time_zone", CoreConfig.DEFAILT_TIME_ZONE.getID());
//        hibernateProperties.setProperty("hibernate.use_sql_comments", hibernateUseSqlComments);

        // Class .getName() -> io.exchange.ClassName
        hibernateProperties.setProperty("hibernate.physical_naming_strategy", PhysicalNamingConfig.class.getName());

        return hibernateProperties;
    }
}
