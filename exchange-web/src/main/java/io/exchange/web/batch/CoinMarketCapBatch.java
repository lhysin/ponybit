package io.exchange.web.batch;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.exchange.core.service.ActionLogService;
import io.exchange.core.service.api.CoinMarketCapApiService;
import io.exchange.domain.enums.LogTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinMarketCapBatch {

    private final ActionLogService actionLogService;
    private final CoinMarketCapApiService coinMarketCapApiService;

    // https://www.baeldung.com/spring-scheduled-tasks
    // default sync
    //@Scheduled(cron = "1 * * * * *")
    @Scheduled(fixedDelay = 600 * 1000, initialDelay = 10000)
    public void syncCoinMarketCapToDB() {

        try {

            LocalDateTime startTime = LocalDateTime.now();

            boolean isTop10 = true;
            coinMarketCapApiService.coinMarketCapSearchAndMerge(isTop10);

            log.debug("coinMarketCapApiService.coinMarketCapSearchAndMerge() start time : {}", startTime);
            Double elpasedTime = Double.valueOf(startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS))/1000;
            log.debug("coinMarketCapApiService.coinMarketCapSearchAndMerge() elpased time Sec : {}", elpasedTime);
        } catch (Exception e) {
            log.error("coinMarketCapApiService.coinMarketCapSearchAndMerge() error : {}", e);
            this.actionLogService.log(LogTag.BATCH_EXCEPTION, e.getMessage());
        }

    }
}
