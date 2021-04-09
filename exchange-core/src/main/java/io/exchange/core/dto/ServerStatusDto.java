package io.exchange.core.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

public class ServerStatusDto {

    @Getter
    @Setter
    public static class ResServerStatus {
        BigDecimal angelFundPer;
    }

    @Getter
    @Setter
    public static class ReqAdd {
        BigDecimal angelFundPer;
    }
}
