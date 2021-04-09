package io.exchange.core.dto;

import javax.annotation.Nullable;

import io.exchange.domain.enums.LogTag;
import lombok.Getter;
import lombok.Setter;

public class ActionLogDto {

    @Getter
    @Setter
    public static class ResActionLog {
        transient Long id;
        UserDto.ResUser user;
        LogTag tag;
        String msg;
    }

    @Getter
    @Setter
    public static class ReqActionLogs extends CommonDto.ReqPage {
        @Nullable
        Long userId;
        @Nullable
        LogTag tag;
    }
}
