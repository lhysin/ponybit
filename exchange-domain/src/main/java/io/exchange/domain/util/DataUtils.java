package io.exchange.domain.util;

import java.math.BigDecimal;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataUtils {

    public static String rtrim(String str){
        int len = str.length();
        while ((0 < len) && (str.charAt(len-1) <= '0'))
        {
            len--;
        }
        return str.substring(0, len);
    }

    public static String decimal(BigDecimal val) {
        String[] _val = val.toPlainString().split("\\.");
        if (_val.length == 2) {
            return _val[0] + "." + ("".equals(DataUtils.rtrim(_val[1])) ? "0" : DataUtils.rtrim(_val[1]));
        }
        return val.toPlainString();
    }
}
