package io.exchange.domain.util;

import java.util.Arrays;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EnumUtils {

    public static <T extends Enum<T>> boolean isEqual(Enum<T> source, Enum<T> target) {
        return source.equals(target);
    }

    public static <T extends Enum<T>> boolean isNotEqual(Enum<T> source, Enum<T> target) {
        return !EnumUtils.isEqual(source, target);
    }

    // Type safety: Potential heap pollution via varargs parameter targets
    @SafeVarargs
    public static <T extends Enum<T>> boolean hasContain(Enum<T> source, final Enum<T>... targets) {
        return Arrays.stream(targets)
                .filter(target -> EnumUtils.isEqual(source, target))
                .findAny()
                .isPresent();
    }
}
