package io.exchange.web.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Logout Success handling
 */
@Slf4j
@SuppressWarnings("unused")
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if(!response.isCommitted()) {
            String refererUrl = request.getHeader("Referer");
            if(StringUtils.isNotEmpty(refererUrl)) {
                response.sendRedirect(refererUrl);
            } else {
                response.sendRedirect("/");
            }
        }
    }
 }