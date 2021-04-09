package io.exchange.web.service;

import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.stereotype.Component;

import io.exchange.core.service.ActionLogService;
import io.exchange.domain.enums.LogTag;
import io.exchange.web.util.StaticWebUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomSessionRegistryService extends SessionRegistryImpl {

    private final ActionLogService actionLogService;

    public void onApplicationEvent(SessionDestroyedEvent event) {

        Long userId = StaticWebUtils.getCurrentLoginUserId();
        if(userId != null) {
            // action logging
            this.actionLogService.log(userId, LogTag.LOGOUT_BY_SESSION);
        }

        String sessionId = event.getId();
        super.removeSessionInformation(sessionId);
    }

}
