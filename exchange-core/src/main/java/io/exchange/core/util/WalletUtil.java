package io.exchange.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WalletUtil {

    public static BigDecimal scale(BigDecimal target) {
        return target.setScale(8, RoundingMode.DOWN);
    }
}
