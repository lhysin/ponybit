package io.exchange.core.service.api;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import feign.Feign.Builder;
import io.exchange.core.hibernate.repository.CoinMarketCapRepository;
import io.exchange.core.provider.feign.CoinMarketCapProvider;
import io.exchange.core.provider.feign.CoinMarketCapProvider.Ticker;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.coin.CoinMarketCap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class CoinMarketCapApiService {

    private final Builder feignBuilder;
    private final CoinMarketCapRepository coinMarketCapRepository;

    public void coinMarketCapSearchAndMerge(boolean isTop10) {

        CoinMarketCapProvider coinMarketCapProvider = feignBuilder.target(CoinMarketCapProvider.class, "https://api.coinmarketcap.com");
        List<Ticker> tickers = null;
        try {
            if(isTop10) {
                tickers = coinMarketCapProvider.getTop10Tiker();
            } else {
                tickers = coinMarketCapProvider.getEthereumTiker();
            }
        } catch (Exception e) {
            log.error("coinMarketCapProvider.getTiker() error : {}", e);
            throw new BusinessException(Code.FEIGN_API_ERROR);
        }

        for(Ticker ticker : tickers) {

            CoinName coinName = null;
            try {
                coinName = CoinName.valueOf(StringUtils.upperCase(ticker.getName()));
            } catch (Exception e) {
                log.trace("not exists coin name");
                continue;
            }

            CoinMarketCap coinMarketCap = CoinMarketCap.builder()
                    .coinName(coinName)
                    .regIp(StaticUtils.getCurrentIp())
                    .regDtm(LocalDateTime.now())
                    .symbol(ticker.getSymbol())
                    .rank(ticker.getRank())
                    .percentChange24h(ticker.getPercent_change_24h())
                    .priceKrw(ticker.getPrice_krw())
                    .priceUsd(ticker.getPrice_usd())
                    .priceBtc(ticker.getPrice_btc())
                    .marketCapUsd(ticker.getMarket_cap_usd())
                    .availableSupply(ticker.getAvailable_supply())
                    .totalSupply(ticker.getTotal_supply())
                    .maxSupply(ticker.getMax_supply())
                    .percentChange1h(ticker.getPercent_change_1h())
                    .percentChange7d(ticker.getPercent_change_7d())
                    .lastUpdated(ticker.getLast_updated())
                    .marketCapKrw(ticker.getMarket_cap_krw())
                    .build();

            coinMarketCapRepository.save(coinMarketCap);
        }
    }
}

