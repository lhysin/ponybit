package io.exchange.web.config;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import io.exchange.domain.util.DateUtils;

@Component
public class HealthCheckConfig implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up()
                .withDetail("serverTime", LocalDateTime.now().format(DateUtils.FORMATTER_YYYY_MM_DD_HH_MM_SS_Z))
                .withDetail("serverTimeZone", ZoneId.systemDefault())
                .build();
    }
}