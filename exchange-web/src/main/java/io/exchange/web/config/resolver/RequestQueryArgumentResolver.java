
package io.exchange.web.config.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import io.exchange.core.annotation.RequestQuery;

@Component
public class RequestQueryArgumentResolver extends ServletModelAttributeMethodProcessor {

    public RequestQueryArgumentResolver() {
        super(false);
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(RequestQuery.class);
    }
}