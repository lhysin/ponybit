package io.exchange.core.dto;

import java.time.LocalDateTime;

import io.exchange.core.dto.AdminWalletDto.ResAdminWallet;
import io.exchange.core.dto.WalletDto.ResWallet;
import io.exchange.domain.enums.CoinName;
import lombok.Getter;
import lombok.Setter;

public class CoinDto {

    @Getter
    @Setter
    public static class ResCoin {
        CoinName name;
        String hanName;
        String mark;
        String unit;
        Long displayPriority;
        String logoUrl;
        LocalDateTime regDtm;
    }

    @Getter
    @Setter
    public static class ResCoinWalletAdminWallet {
        ResCoin coin;
        ResWallet wallet;
        ResAdminWallet adminWallet;
    }
}
