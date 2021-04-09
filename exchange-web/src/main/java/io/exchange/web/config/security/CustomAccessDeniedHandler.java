package io.exchange.web.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.NoHandlerFoundException;

import io.exchange.core.util.StaticUtils;
import io.exchange.web.util.ResBody;
import io.exchange.web.util.StaticWebUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * AccessDenied handling
 * login after hasRole('ROLE_ADMIN')
 * 
 * HttpStatus.FORBIDDEN
 */
@Slf4j
@SuppressWarnings("unused")
@Component
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        String method = request.getMethod();
        String uri = request.getRequestURL().toString();
        log.trace("access denied for:{} and method:{}", uri, method);

        StaticWebUtils.defaultHttpServletResponse(response, HttpStatus.FORBIDDEN);
    }
}
