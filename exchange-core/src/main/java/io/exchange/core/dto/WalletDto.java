package io.exchange.core.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.lang.Nullable;

import io.exchange.core.annotation.LoginUserId;
import io.exchange.domain.enums.CoinName;
import lombok.Getter;
import lombok.Setter;

public class WalletDto {

    @Getter
    @Setter
    public static class ResWallet {
        transient Long userId;
        CoinName coinName;
        String address;
        BigDecimal availableBalance;
        String tag;
        LocalDateTime regDtm;
    }

    @Getter
    @Setter
    public static class ReqWallets extends CommonDto.ReqPage {
        @Nullable
        CoinName coinName;
        @Nullable
        String email;
    }

    @Getter
    @Setter
    public static class ReqWalletByAdmin {
        @NotNull
        Long userId;
        @NotNull
        CoinName coinName;
    }

    @Getter
    @Setter
    public static class ReqWalletByUser {
        @LoginUserId
        Long userId;
        @NotNull
        CoinName coinName;
    }

    @Getter
    @Setter
    public static class ReqAdd {
        @LoginUserId
        Long userId;
        @NotNull
        CoinName coinName;
    }
}
