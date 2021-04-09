package io.exchange.web.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.transaction.annotation.Transactional;

import io.exchange.core.service.ActionLogService;
import io.exchange.domain.enums.LogTag;
import io.exchange.web.util.LoginUser;
import io.exchange.web.util.StaticWebUtils;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
public class CustomRememberMeServices extends PersistentTokenBasedRememberMeServices {

    private final ActionLogService actionLogService;

    private final GrantedAuthoritiesMapper authoritiesMapper;
    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;
    private final PersistentTokenRepository tokenRepository;
    private final String key;

    public CustomRememberMeServices(UserDetailsService userDetailsService, ActionLogService actionLogService, String key) {
        super(key, userDetailsService, persistentTokenRepository());
        this.actionLogService = actionLogService;
        this.key = key;
        this.tokenRepository = persistentTokenRepository();
        this.authenticationDetailsSource  = new WebAuthenticationDetailsSource();
        this.authoritiesMapper = new NullAuthoritiesMapper();
    }

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {

        LoginUser loginUser = StaticWebUtils.getCurrentLoginUser();
        String email = loginUser.getEmail();

        log.debug("Creating new persistent login for user's email : {}", email);
        PersistentRememberMeToken persistentToken = new PersistentRememberMeToken(email, generateSeriesData(), generateTokenData(), new Date());

        try {
            tokenRepository.createNewToken(persistentToken);
            addCookie(persistentToken, request, response);
            actionLogService.log(loginUser.getId(), LogTag.LOGIN_AND_REMEMBER_ME);
        } catch (Exception e) {
            log.error("Failed to save persistent token ", e);
        }
    }

    @Override
    protected Authentication createSuccessfulAuthentication(HttpServletRequest request, UserDetails user) {

        LoginUser loginUser = StaticWebUtils.getCurrentLoginUser();

        RememberMeAuthenticationToken auth = new RememberMeAuthenticationToken(key, loginUser, authoritiesMapper.mapAuthorities(user.getAuthorities()));
        auth.setDetails(authenticationDetailsSource.buildDetails(request));

        return auth;
    }

    private void addCookie(PersistentRememberMeToken token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(new String[] { token.getSeries(), token.getTokenValue() }, getTokenValiditySeconds(), request, response);
    }

    private static PersistentTokenRepository persistentTokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }
}
