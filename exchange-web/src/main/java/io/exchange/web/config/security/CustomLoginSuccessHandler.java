package io.exchange.web.config.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import io.exchange.core.service.ActionLogService;
import io.exchange.domain.enums.LogTag;
import io.exchange.web.util.StaticWebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Login Success handling
 */
@Slf4j
@SuppressWarnings("unused")
@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ActionLogService actionLogService;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {

        Long userId = StaticWebUtils.getCurrentLoginUserId();
        actionLogService.log(userId, LogTag.LOGIN);

        HttpSession session = request.getSession(false);
        session.setMaxInactiveInterval(30 * 60);
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        StaticWebUtils.defaultHttpServletResponse(response, HttpStatus.OK);
    }
}