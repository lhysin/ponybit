package io.exchange.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class ViewConfig {

    /* Thymeleaf template resolver 
     * default setting : org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties
     * */
    @Bean
    public ITemplateResolver templateResolver() {

        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();

        // templateResolver.setPrefix("/WEB-INF/thymeleaf/");

        // /src/resources/thymeleaf/");
        templateResolver.setPrefix("classpath:thymeleaf/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        templateResolver.setOrder(0);
        templateResolver.setCharacterEncoding(CoreConfig.DEFAULT_CHARSET);

        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(){

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addDialect(new Java8TimeDialect());
        templateEngine.setTemplateResolver(templateResolver());

        return templateEngine;
    }

    @Bean
    public ViewResolver thymeleafViewResolver(){

        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver() ;

        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding(CoreConfig.DEFAULT_CHARSET);

        return viewResolver;
    }
}