package io.exchange.domain.util;

import java.time.format.DateTimeFormatter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

    public static final String DATE_FORMAT_SERVER_DEFAULT = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_HH = DateTimeFormatter.ofPattern("HH");
    public static final DateTimeFormatter FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter FORMATTER_YYYY_MM_DD_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter FORMATTER_YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:SS");
    public static final DateTimeFormatter FORMATTER_HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_HHMMSSSSS = DateTimeFormatter.ofPattern("HHmmssSSS");
    public static final DateTimeFormatter FORMATTER_HHMMSS = DateTimeFormatter.ofPattern("HHmmss");
    public static final DateTimeFormatter FORMATTER_YYYYMMDDHHMMSSSS = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS");
    public static final DateTimeFormatter FORMATTER_YYYY_MM_DD_HH_MM_SS_Z = DateTimeFormatter.ofPattern(DATE_FORMAT_SERVER_DEFAULT);
}
