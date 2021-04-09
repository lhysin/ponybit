package io.exchange.web.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import io.exchange.domain.enums.Code;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.util.EnumUtils;
import io.exchange.web.util.ResBody;
import io.exchange.web.util.StaticWebUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Login Failure handling
 */
@Slf4j
@SuppressWarnings("unused")
@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {

        Throwable throwable = exception.getCause();
        Code codeEnum = Code.USER_LOGIN_FAIL;

        if(throwable instanceof BusinessException) {
            Code excepCodeEnum = BusinessException.class.cast(throwable).getCodeEnum();
            if(EnumUtils.isEqual(excepCodeEnum, Code.USER_NOT_EMAIL_VERIFICATION)) {
                codeEnum = excepCodeEnum;
            }
        } else if (throwable instanceof BadCredentialsException) {
            codeEnum = Code.USER_LOGIN_FAIL;
        } else {
        }

        ResBody<Object> resBody = ResBody.builder()
                .msg(codeEnum.getMessage())
                .code(codeEnum.getCode())
                .build();
        StaticWebUtils.defaultExceptionHttpServletResponse(response, HttpStatus.BAD_REQUEST, resBody);
    }
}