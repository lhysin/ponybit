package io.exchange.web.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import io.exchange.core.config.CoreConfig;
import io.exchange.core.util.StaticUtils;
import io.exchange.web.util.StaticWebUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Unauthorized handling
 * (not login)
 * 
 * HttpStatus.UNAUTHORIZED
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        // after web server separated
//        String method = request.getMethod();
//        String uri = request.getRequestURL().toString();
//        log.trace("unauthorized for:{} and method:{}", uri, method);
//
//        StaticWebUtils.defaultHttpServletResponse(response, HttpStatus.UNAUTHORIZED);

        String method = request.getMethod();
        String uri = request.getRequestURL().toString();
        log.trace("access denied for:{} and method:{}", uri, method);
        if(StaticWebUtils.isAjax(request)) {
            StaticWebUtils.defaultHttpServletResponse(response, HttpStatus.UNAUTHORIZED);
        } else {
            if(!response.isCommitted()) {
                String loginPage = "/login";
                if(StringUtils.isNoneEmpty(uri)) {
                    uri = UriUtils.encode(uri, CoreConfig.DEFAULT_CHARSET);
                    loginPage += "?returnUrl=" + uri;
                }
                response.sendRedirect(loginPage);
            }
        }
    }
}
