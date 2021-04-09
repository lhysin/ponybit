package io.exchange.core.config;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.exchange.core.service.AdminWalletService;
import io.exchange.core.service.CoinService;
import io.exchange.core.service.DefaultDataService;
import io.exchange.core.service.TestService;
import io.exchange.core.service.UserService;
import io.exchange.domain.enums.Phase;
import io.exchange.domain.util.EnumUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DependsOn("coreConfig")
@Transactional
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final CoinService coinService;
    private final AdminWalletService adminWalletService;
    private final UserService userService;

    private final DefaultDataService defaultDataService;
    private final TestService testService;

    private boolean alreadySetup = false;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {

        if(!alreadySetup) {

            // core data setting
            this.coinService.createDefaultCoinsIfNotFound();
            this.adminWalletService.createDefaultAdminWalletIfNotFound();
            this.userService.createAdminIfNotFound("admin@ponybit.io", "UNCENCORED");

            // test data setting
            if (EnumUtils.hasContain(CoreConfig.currentPhase, Phase.LOCAL, Phase.DEV)) {

                this.userService.createAdminIfNotFound("test@admin.com", "test");
                this.userService.createAdminIfNotFound("admin@admin.com", "admin");

                this.defaultDataService.createTestData(LocalDateTime.now());
//                this.testService.enumCompareUtilsTest();
//                this.testService.numberCompareUtilsTest();
            }
            alreadySetup = true;
        }
    }
}