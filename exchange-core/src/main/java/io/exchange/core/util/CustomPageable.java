package io.exchange.core.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomPageable {

    public static PageRequest of(int page, int size) {
        return PageRequest.of(page - 1, size, Sort.unsorted());
    }

    public static PageRequest of(int page, int size, Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }

    public static PageRequest of(int page, int size, Direction direction, String... properties) {
        return PageRequest.of(page - 1, size, Sort.by(direction, properties));
    }
}
