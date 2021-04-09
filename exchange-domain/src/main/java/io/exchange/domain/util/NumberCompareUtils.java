package io.exchange.domain.util;

import java.math.BigDecimal;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberCompareUtils {

    /**
      * +---------+----------+
      * |  a gt b |  a > b   |
      * +---------+----------+
      * |  a lt b |  a < b   |
      * +---------+----------+
      * |  a ge b |  a >= b  |
      * +---------+----------+
      * |  a le b |  a <= b  |
      * +---------+----------+
      * |  a eq b |  a == b  |
      * +---------+----------+
      * |  a ne b |  a !== b |
      * +---------+----------+
     */

    public static boolean isEqual(BigDecimal target1, BigDecimal target2) {
        return target1.compareTo(target2) == 0;
    }

    public static boolean isNotEqual(BigDecimal target1, BigDecimal target2) {
        return !NumberCompareUtils.isEqual(target1, target2);
    }

    public static boolean isLT(BigDecimal target1, BigDecimal target2) {
        return target1.compareTo(target2) == -1;
    }

    public static boolean isGT(BigDecimal target1, BigDecimal target2) {
        return target1.compareTo(target2) == 1;
    }

    public static boolean isPositive(BigDecimal target) {
        return target.compareTo(BigDecimal.ZERO) == 1;
    }

    public static boolean isPositiveOrZero(BigDecimal target) {
        return target.compareTo(BigDecimal.ZERO) == 0 ||
                target.compareTo(BigDecimal.ZERO) == 1;
    }

    public static boolean isNotPositiveOrZero(BigDecimal target) {
        return !NumberCompareUtils.isPositiveOrZero(target);
    }

    public static boolean isPositive(Long target) {
        return target.compareTo(0L) == 1;
    }

    public static boolean isPositiveOrZero(Long target) {
        return target.compareTo(0L) == 0 ||
                target.compareTo(0L) == 1;
    }

    public static boolean isNotPositiveOrZero(Long target) {
        return !NumberCompareUtils.isPositiveOrZero(target);
    }

    public static boolean isEqual(Long target1, Long target2) {
        return target1.compareTo(target2) == 0;
    }

    public static boolean isNotEqual(Long target1, Long target2) {
        return !NumberCompareUtils.isEqual(target1, target2);
    }

    public static boolean isLT(Long target1, Long target2) {
        return target1.compareTo(target2) == -1;
    }

    public static boolean isGT(Long target1, Long target2) {
        return target1.compareTo(target2) == 1;
    }

//    public static Compare compare(BigDecimal target1, BigDecimal target2) {
//        if (target1.compareTo(target2) == -1) {
//            return Compare.LT;
//        }
//
//        if (target1.compareTo(target2) == 0) {
//            return Compare.EQ;
//        }
//
//        if (target1.compareTo(target2) == 1) {
//            return Compare.GT;
//        }
//
//        return Compare.UNKNOWN;
//    }
//
//    public static Compare compare(Long target1, Long target2) {
//        if (target1.compareTo(target2) == -1) {
//            return Compare.LT;
//        }
//
//        if (target1.compareTo(target2) == 0) {
//            return Compare.EQ;
//        }
//
//        if (target1.compareTo(target2) == 1) {
//            return Compare.GT;
//        }
//
//        return Compare.UNKNOWN;
//    }
//
//    public static Compare compareToZero(BigDecimal target1) {
//        return compare(target1, BigDecimal.ZERO);
//    }
//
//    public static Compare compareToZero(Long target1) {
//        return compare(target1, 0L);
//    }
}
