package io.exchange.core.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.annotation.Nullable;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import io.exchange.core.annotation.LoginUserId;
import io.exchange.domain.enums.Category;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.Status;
import lombok.Getter;
import lombok.Setter;

public class TransactionDto {

    @Getter
    @Setter
    public static class ReqTransactionAdd {
        @LoginUserId
        Long userId;
        @NotNull
        CoinName coinName;
        @NotNull
        String fromAddress;
        @NotNull
        String toAddress;
        @NotNull
        @Digits(integer=19, fraction=18)
        @DecimalMin("0.00000001")
        BigDecimal realAmount;
        @NotNull
        String txId;
        @NotNull
        Category category;
        @NotNull
        Status status;
    }
}
