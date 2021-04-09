package io.exchange.web;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import io.exchange.core.config.CoreConfig;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    @PostConstruct
    void started() {
        TimeZone.setDefault(CoreConfig.DEFAILT_TIME_ZONE);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}