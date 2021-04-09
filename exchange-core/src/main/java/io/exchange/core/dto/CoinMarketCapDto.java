package io.exchange.core.dto;

import java.time.LocalDateTime;

import io.exchange.domain.enums.CoinName;
import lombok.Getter;
import lombok.Setter;

public class CoinMarketCapDto {

    @Getter
    @Setter
    public static class ResCoinMarketCap {
        CoinName coinName;
        LocalDateTime regDtm;
        String priceKrw;
    }
}
