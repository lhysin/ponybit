package io.exchange.web.batch;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.exchange.core.service.ActionLogService;
import io.exchange.core.service.AdminWalletService;
import io.exchange.core.service.api.EtherScanApiService;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.LogTag;
import io.exchange.domain.enums.WalletType;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.coin.AdminWallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EtherScanBatch {

    private final ActionLogService actionLogService;
    private final EtherScanApiService etherScanService;
    private final AdminWalletService adminWalletService;

    // https://www.baeldung.com/spring-scheduled-tasks
    // default sync
    //@Scheduled(cron = "1 * * * * *")
    @Scheduled(fixedDelay = 10 * 1000, initialDelay = 10000)
    public void syncEtherScanToDB() {

        try {

            LocalDateTime startTime = LocalDateTime.now();

            AdminWallet adminWallet = this.adminWalletService.get(CoinName.ETHEREUM, WalletType.HOT)
                    .orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

            this.etherScanService.etherScanSearchAndMerge(adminWallet);

            log.debug("etherScanService.etherScanSearchAndMerge() start time : {}", startTime);

            Double elpasedTime = Double.valueOf(startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS))/1000;
            log.debug("etherScanService.etherScanSearchAndMerge() elpased time Sec : {}", elpasedTime);

        } catch (Exception e) {
            log.error("etherScanService.etherScanSearchAndMerge() error : {}", e);
            this.actionLogService.log(LogTag.BATCH_EXCEPTION, e.getMessage());
        }

    }
}
