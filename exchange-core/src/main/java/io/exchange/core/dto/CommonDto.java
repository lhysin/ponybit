package io.exchange.core.dto;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.lang.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CommonDto {

    @Getter
    @Setter
    public static class ReqPage {
        @NotNull
        @PositiveOrZero
        // page zero-based page index.
        Integer page;
        @NotNull
        @Positive
        // size the size of the page to be returned.
        Integer size;
        @Nullable
        String q;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ResPage<T> {
        List<T> list;
        Integer page;
        Integer size;
        Integer totalPages;
    }
}
