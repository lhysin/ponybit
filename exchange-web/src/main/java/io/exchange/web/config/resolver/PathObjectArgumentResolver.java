package io.exchange.web.config.resolver;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import io.exchange.core.annotation.PathObject;
import io.exchange.domain.exception.BadParameterException;

@Component
public class PathObjectArgumentResolver implements HandlerMethodArgumentResolver {

    private final static String DEFAULT_MASSAGE = "invalid parameter";

    private final ConversionService conversionService;

    public PathObjectArgumentResolver(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(PathObject.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
            NativeWebRequest nativeWebRequest, WebDataBinderFactory binderFactory) throws Exception {

        Class<?> clazz =  methodParameter.getParameterType();
        Object target = clazz.newInstance();

        // bindingResult initialize
        BindingResult bindingResult = new BeanPropertyBindingResult(target, clazz.getName());

        // get pathvariable map
        Map<String, String> uriTemplateVars = (Map<String, String>) nativeWebRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);

        // with superclass fields
        ReflectionUtils.doWithFields(clazz, new FieldCallback() {
            public void doWith(Field field) {
                String fieldName = field.getName();
                if(uriTemplateVars.containsKey(fieldName)) {
                    Object value = uriTemplateVars.get(fieldName);
                    try {
                        invokeSetField(target, field, value);
                    } catch (Exception e) {
                        String errorMsg = "'" + value + "' is " + DEFAULT_MASSAGE;
                        FieldError fieldError = new FieldError(clazz.getName(), fieldName, errorMsg);
                        if(!bindingResult.hasFieldErrors(fieldError.getField())) {
                            bindingResult.addError(fieldError);
                        }
                    }
                }
            }
        });

        // BindingResult to BadParameterException
        if(!bindingResult.getAllErrors().isEmpty()) {
            throw new BadParameterException(bindingResult);
        }

        return target;
    }

    private void invokeSetField(Object target, Field field, Object value) {
        if(value != null) {
            if(this.conversionService.canConvert(field.getType(), value.getClass())) {
                value = this.conversionService.convert(value, field.getType());
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, target, value);
            }
        }
    }
}
