package io.exchange.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import feign.Feign;
import feign.Feign.Builder;
import feign.Logger;
import feign.Response;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;
import io.exchange.core.util.ModelUtils;
import io.exchange.domain.enums.Code;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.util.DateUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FeignConfig {

    public class FeignErrorDecoder implements ErrorDecoder {

        private final Decoder decoder = new Decoder.Default();
        private final ErrorDecoder defaultErrorDecoder = new Default();

        @Override
        public Exception decode(String methodKey, Response response) {
            if (response.status() >= 400 && response.status() <= 499) {
                Object decodedObject = null;
                try {
                    decodedObject = decoder.decode(response, String.class);
                    return new BusinessException(Code.FEIGN_API_ERROR, ModelUtils.toJsonMap(decodedObject.toString()));
                } catch (Exception e) {
                    log.error("decoder.decode() error : {}", e); 
                    return new BusinessException(Code.FEIGN_API_ERROR, null);
                } 
            }
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }

    @Bean
    public Builder getFeignBuilder() {
        return Feign.builder()
                .logger(getSlf4jLogger())
                .logLevel(getFeignLoggerLevel())
                .encoder(getGsonEncoder())
                .decoder(getGsonDecoder())
                .errorDecoder(getFeignErrorDecoder());
    }

    @Bean
    public Slf4jLogger getSlf4jLogger() {
        return new Slf4jLogger();
    }

    @Bean
    public feign.Logger.Level getFeignLoggerLevel() {
//        return Logger.Level.BASIC;
        return Logger.Level.FULL;
    }

    @Bean
    public Decoder getGsonDecoder() {
        return new GsonDecoder(createGson());
    }

    @Bean
    public Encoder getGsonEncoder() {
        return new GsonEncoder(createGson());
    }
    @Bean
    public ErrorDecoder getFeignErrorDecoder() {
        return new FeignErrorDecoder();
    }

    private Gson createGson() {
        return new GsonBuilder()
                .setDateFormat(DateUtils.DATE_FORMAT_SERVER_DEFAULT)
                .create();
    }
}
