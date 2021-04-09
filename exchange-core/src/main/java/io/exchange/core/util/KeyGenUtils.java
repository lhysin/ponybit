package io.exchange.core.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import io.exchange.core.config.CoreConfig;
import lombok.experimental.UtilityClass;

@UtilityClass
public class KeyGenUtils {

    public static String generateDocFileName() {
        return RandomStringUtils.randomAlphanumeric(64);
    }

    public static String generateApiKey() {
        return RandomStringUtils.randomAlphanumeric(32);
    }
    
    public static String generateAddressIdByEthereum() {
        return "0x" + StringUtils.lowerCase(RandomStringUtils.randomAlphanumeric(32));
    }

    public static String generateTxIdByEthereum() {
        return "0x" + StringUtils.lowerCase(RandomStringUtils.randomAlphanumeric(62));
    }

    public static String generateKey(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String generateNumericKey(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    public static String generateNumericKey(int precision, int scale) {
        return RandomStringUtils.randomNumeric(precision) + CoreConfig.DOT + RandomStringUtils.randomNumeric(scale);
    }

    public static String generateEmailConfirmNumericKey() {
        return RandomStringUtils.randomAlphanumeric(20);
    }

    public static String generateHashEmail(Long userId, String pwd, String email) {
        return DigestUtils.md5Hex(RandomStringUtils.randomAlphanumeric(32) + "_" + userId + "_" + pwd + "_" + email + "_" + RandomStringUtils.randomAlphanumeric(32));
    }
}
