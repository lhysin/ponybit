package io.exchange.core.dto;

import java.time.LocalDateTime;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.exchange.core.annotation.LoginUserId;
import io.exchange.core.dto.UserDto.ResUser;
import io.exchange.domain.enums.PostType;
import lombok.Getter;
import lombok.Setter;

public class PostDto {

    @Getter
    @Setter
    public static class ReqPosts extends CommonDto.ReqPage {
        PostType postType;
    }

    @Getter
    @Setter
    public static class ResPost {
        ResUser user;
        Long id;
        String title;
        String content;
        LocalDateTime regDtm;
    }

    @Getter
    @Setter
    public static class ReqAdd {
        @LoginUserId
        private Long userId;
        @NotNull
        @NotBlank
        private String title;
        @NotNull
        @NotBlank
        private String content;
        @Nullable
        private PostType type;
    }

    @Getter
    @Setter
    public static class ReqEdit {
        @LoginUserId
        private Long userId;
        @NotNull
        private Long postId;
        @Nullable
        private String title;
        @Nullable
        private String content;
        @Nullable
        private PostType type;
    }
}
