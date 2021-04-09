package io.exchange.core.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.annotation.Nullable;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.exchange.core.annotation.LoginUserId;
import io.exchange.domain.enums.Category;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.Status;
import lombok.Getter;
import lombok.Setter;

public class ManualTransactionDto {

    @Getter
    @Setter
    public static class ResManualTransaction {
        Long id;
        Category category;
        Status status;
        CoinName coinName;
        String txId;
        BigDecimal reqAmount;
        LocalDateTime regDtm;
        LocalDateTime completeDtm;
        String reason;
    }

    @Getter
    @Setter
    public static class ReqManualTransactionAdd {
        @LoginUserId
        Long userId;
        @NotNull
        CoinName coinName;
        @NotNull
        String address;
        @NotNull
        @Digits(integer=19, fraction=18)
        @DecimalMin("0.00000001")
        BigDecimal amount;
        @Nullable
        String txId;
    }

    @Getter
    @Setter
    public static class ReqManualTransactions extends CommonDto.ReqPage {
        @LoginUserId
        Long userId;
        @Nullable
        Category category;
    }

    @Getter
    @Setter
    public static class ReqManualTransactionEdit {
        @NotNull
        Long id;
        @NotNull
        Long userId;
        @NotNull
        CoinName coinName;
        @NotNull
        Category category;
        @NotNull
        String realTxId;
    }
}
