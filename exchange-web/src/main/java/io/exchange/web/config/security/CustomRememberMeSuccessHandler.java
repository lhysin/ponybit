package io.exchange.web.config.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
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
public class CustomRememberMeSuccessHandler implements AuthenticationSuccessHandler {

    private final ActionLogService actionLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if(!response.isCommitted()) {
            Long userId = StaticWebUtils.getCurrentLoginUserId();
            this.actionLogService.log(userId, LogTag.LOGIN_BY_REMEMBER_ME);
        }
    }
}