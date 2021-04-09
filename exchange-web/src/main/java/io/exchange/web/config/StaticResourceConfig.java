package io.exchange.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    // from: src/main/resource/static/js/3.3.1.jquery.min.js
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/"
            , "classpath:/resources/"
            , "classpath:/static/"
            , "classpath:/resources/static/"
            , "classpath:/META-INF/resources/static/"
            , "classpath:/public/"
            , "/resources/**"};

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // to: http://localhost:8080/resources/js/3.3.1.jquery.min.js
        registry.addResourceHandler("/resources/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);

        //  http://localhost:8080/favicon.ico
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/favicon.ico");
    }
}
