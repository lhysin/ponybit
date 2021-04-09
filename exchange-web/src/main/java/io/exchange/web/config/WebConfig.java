package io.exchange.web.config;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import io.exchange.core.config.CoreConfig;
import io.exchange.core.service.ActionLogService;
import io.exchange.domain.util.DateUtils;
import io.exchange.domain.util.LocalDateTimeConverter;
import io.exchange.web.config.factory.StringToEnumConverterFactory;
import io.exchange.web.config.resolver.CustomHandlerExceptionResolver;
import io.exchange.web.config.resolver.LoginUserArgumentResolver;
import io.exchange.web.config.resolver.PathObjectArgumentResolver;
import io.exchange.web.config.resolver.RequestQueryArgumentResolver;
import lombok.RequiredArgsConstructor;

@Configuration
@ComponentScan(basePackages = { "io.exchange.web" })
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ActionLogService actionLogService;
    private final MessageSource messageSource;
    private final StringToEnumConverterFactory stringToEnumConverterFactory;

    private final LoginUserArgumentResolver loginUserArgumentResolver;
    private final RequestQueryArgumentResolver requestQueryArgumentResolver;
    private final CustomHandlerExceptionResolver customHandlerExceptionResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserArgumentResolver);
        argumentResolvers.add(requestQueryArgumentResolver);
        argumentResolvers.add(new PathObjectArgumentResolver(conversionService()));
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(customHandlerExceptionResolver);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        final GsonHttpMessageConverter msgConverter = new GsonHttpMessageConverter();
        msgConverter.setGson(gson());

        converters.add(msgConverter);
        converters.add(new StringHttpMessageConverter(Charset.forName(CoreConfig.DEFAULT_CHARSET)));
        converters.add(new FormHttpMessageConverter());
    }

    /*
     * https://stackoverflow.com/questions/4617099/spring-3-0-mvc-binding-enums-case-sensitive(non-Javadoc)
     * 
     *  as-is : Enum Type @PathVariable init bind is sensitive
     *  to-be : insesitive
     *  
     *  e.g. CoinEnum.BITCOIN
     *  @PathVariable("coinName") CoinEnum coinEnum
     *  as-is : only .../BITCOIN
     *  to-be : .../BITCOIN , .../bitcoin
     */
//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverterFactory(stringToEnumConverterFactory);
//        ApplicationConversionService.configure(registry);
//    }

    @Bean
    public ConversionService conversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverterFactory(stringToEnumConverterFactory);
        return conversionService;
    }

    @Bean
    public Gson gson() {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
                .setDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS)
                .setPrettyPrinting()
                .registerTypeAdapter(BigDecimal.class,  new JsonSerializer<BigDecimal>() {
                    @Override
                    public JsonElement serialize(final BigDecimal src, final Type typeOfSrc, final JsonSerializationContext context) {
                        return new JsonPrimitive(src.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                    }
                })
                .create();
        return gson;
    }

    @Bean
    public LocaleResolver localeResolver() {
        final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return cookieLocaleResolver;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        return processor;
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        return dispatcherServlet;
    }
}
