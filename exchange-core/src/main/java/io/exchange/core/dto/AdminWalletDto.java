package io.exchange.core.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.WalletType;
import lombok.Getter;
import lombok.Setter;

public class AdminWalletDto {

    @Getter
    @Setter
    public static class ResAdminWallet {
        CoinName coinName;
        WalletType type;
        String address;
        BigDecimal availableBalance;
        String tag;
        LocalDateTime regDtm;
    }
}
