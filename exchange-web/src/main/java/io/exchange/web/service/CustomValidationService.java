package io.exchange.web.service;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.util.UriUtils;

import com.google.common.base.Splitter;

import io.exchange.core.annotation.LoginUserId;
import io.exchange.core.config.CoreConfig;
import io.exchange.core.dto.CommonDto;
import io.exchange.domain.enums.Code;
import io.exchange.domain.exception.BadParameterException;
import io.exchange.domain.exception.BadRequestException;
import io.exchange.web.util.StaticWebUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomValidationService {

    private final static String QUERY = "q";
    private final static String DEFAULT_MASSAGE = "invalid parameter";

    private Validator validator;
    private final ConversionService conversionService;

    public CustomValidationService(ConversionService conversionService) {
        this.conversionService = conversionService;

        // Create ValidatorFactory which returns validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

        // It validates bean instances
        this.validator = factory.getValidator();
    }

    public void validationObject(Object target) {

        Class<?> clazz =  target.getClass();

        // check Primitive or Wrapper
        if(ClassUtils.isPrimitiveOrWrapper(clazz)) {
            return;
        }

        // bindingResult initialize
        BindingResult bindingResult = new BeanPropertyBindingResult(target, clazz.getName());

        // ?page=1&size=10&  q=coinName%3Dbitcoin%26category%3Dwithdrawal
        if(this.hasSuperClassReqPage(clazz)) {

            try {

                Field queryField = ReflectionUtils.findField(clazz, QUERY);

                if (queryField == null) {
                    throw new BadRequestException(QUERY + " is " + DEFAULT_MASSAGE);
                }

                ReflectionUtils.makeAccessible(queryField);

                // get q
                String q = ReflectionUtils.getField(queryField, target).toString();
                q = UriUtils.decode(q, CoreConfig.DEFAULT_CHARSET);

                // remove q
                ReflectionUtils.setField(queryField, target, null);

                if(StringUtils.isNotEmpty(q)) {
                    // q to map
                    Map<String, String> map = Splitter.on(CoreConfig.AMPERSAND).trimResults()
                            .withKeyValueSeparator(CoreConfig.EQUALS).split(q);
                    
                    // map to target
                    map.forEach((key, value) -> {
                        try {
                            this.invokeSetField(target, key, value);
                        } catch (Exception e) {
                            String errorMsg = "'" + value + "' is " + DEFAULT_MASSAGE;
                            FieldError fieldError = new FieldError(clazz.getName(), key, errorMsg);
                            if(!bindingResult.hasFieldErrors(fieldError.getField())) {
                                bindingResult.addError(fieldError);
                            }
                        }
                    });
                }

            } catch (Exception e) {
                throw new BadRequestException(QUERY + " is " + DEFAULT_MASSAGE);
            }
        }

        // specific column user_id from LoginUser
        Field loginUserIdField = this.findLoginUserIdField(clazz);
        if(loginUserIdField != null) {
            Long loginUserId = StaticWebUtils.getCurrentLoginUserId();
            if(loginUserId != null) {
                this.invokeSetField(target, loginUserIdField, loginUserId);
            } else {
                throw new BadRequestException(Code.LOGIN_REQUIED.getMessage());
            }
        }

        // Validate bean
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(target);

        // Add Errors
        for (ConstraintViolation<Object> violation : constraintViolations) {

            Object annotation = violation.getConstraintDescriptor().getAnnotation();

            String fieldName = violation.getPropertyPath().toString();

            if(annotation instanceof NotNull) {
                log.debug(fieldName + " is NotNull.");
            }
            if(annotation instanceof Positive) {
                log.debug(fieldName + " is Positive.");
            }
            if(annotation instanceof Size) {
                log.debug(fieldName + " is invalid Size.");
            }

            FieldError fieldError = new FieldError(clazz.getName(), fieldName, violation.getMessage());
            if(!bindingResult.hasFieldErrors(fieldError.getField())) {
                bindingResult.addError(fieldError);
            }
        }

        // BindingResult to BadParameterException
        if(!bindingResult.getAllErrors().isEmpty()) {
            throw new BadParameterException(bindingResult);
        }
    }

    private void invokeSetField(Object target,  String fieldName, Object value) {
        Field field = ReflectionUtils.findField(target.getClass(), fieldName);
        this.invokeSetField(target, field, value);
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

    private boolean hasSuperClassReqPage(Class<?> clazz) {
        boolean has = false;

        Class<CommonDto.ReqPage> reqPageClazz = CommonDto.ReqPage.class;
        if(ClassUtils.getAllSuperclasses(clazz).contains(reqPageClazz) ||
                clazz == reqPageClazz) {
            has = true;
        }

        return has;
    }

    private Field findLoginUserIdField(Class<?> clazz) {
        Field loginUserId = null;
        for(Field field : clazz.getDeclaredFields()) {
            if(field.isAnnotationPresent(LoginUserId.class)) {
                loginUserId = field;
                break;
            }
        }
        return loginUserId;
    }
}