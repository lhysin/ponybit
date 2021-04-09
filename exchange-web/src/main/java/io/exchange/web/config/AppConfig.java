package io.exchange.web.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.ConditionalConverter.MatchResult;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.context.request.RequestContextListener;

import io.exchange.core.config.CoreConfig;

@Configuration
@DependsOn("coreConfig")
public class AppConfig {

    /* messageSource Configuration */
    @Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        /* messageSource.setBasenames("classpath:/messages/error","classpath:/messages/static"); */
        messageSource.setBasename("classpath:/messages");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding(CoreConfig.DEFAULT_CHARSET);
        messageSource.setCacheSeconds(0);
        return messageSource;
    }

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();

        ConditionalConverter<Object, Enum<?>> enumInsensitiveConvertor = new ConditionalConverter<Object, Enum<?>>(){

            @Override
            @SuppressWarnings({ "unchecked", "rawtypes" })
            public Enum<?> convert(MappingContext<Object, Enum<?>> context) {
                Object source = context.getSource();
                if (source == null)
                  return null;

                String name = source.getClass() == String.class ? String.valueOf(source).toUpperCase() : ((Enum<?>) source).name();

                if (name != null)
                  try {
                    return Enum.valueOf((Class) context.getDestinationType(), name);
                  } catch (IllegalArgumentException ignore) {
                  }

                return null;
              }
            @Override
            public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
                return destinationType.isEnum() && (sourceType.isEnum() || sourceType == String.class) ? MatchResult.FULL
                        : MatchResult.NONE;
            }
        };

        /**
         * default EnumConverter faster than callback.
         */
        modelMapper.getConfiguration().getConverters().add(0, enumInsensitiveConvertor);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        return modelMapper;
    }

    // HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
}
