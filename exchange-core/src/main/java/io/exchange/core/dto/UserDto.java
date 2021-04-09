package io.exchange.core.dto;

import java.util.List;

import javax.annotation.MatchesPattern;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.ScriptAssert;

import io.exchange.core.annotation.LoginUserId;
import io.exchange.domain.enums.UserLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class UserDto {

    @Getter
    @Setter
    public static class ResUser {
        transient Long id;
        String email;
        String name;
        String myRefCd;
        Long refCnt;
        UserLevel userLevel;
    }

    @Getter
    @Setter
    public static class UserWithWallets extends ResUser {
        List<WalletDto.ResWallet> wallets;
    }

    @Data
    //@SpringExpressionAssert(value = "pwd.equals(confirmPwd)")
    @ScriptAssert(lang = "javascript", script = "_this.pwd === _this.confirmPwd")
    public static class ReqRegist {
        @NotNull
        @NotBlank
        @Email
        private String email;
        @NotNull
        @NotBlank
        @MatchesPattern(value = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,}")
        private String pwd;
        @NotNull
        @NotBlank
        private String confirmPwd;
        private String refCd;
    }

    @Data
    public static class ReqUptate {
        private String name;
    }

    @Data
    @ScriptAssert(lang = "javascript", script = "_this.pwd !== _this.newPwd")
    @ScriptAssert(lang = "javascript", script = "_this.newPwd === _this.newConfirmPwd")
    public static class ReqChangePassword {
        @LoginUserId
        Long userId;
        @NotNull
        @NotBlank
        private String pwd;
        @NotNull
        @NotBlank
        @MatchesPattern(value = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,}")
        private String newPwd;
        @NotNull
        @NotBlank
        private String newConfirmPwd;
    }

    @Data
    public static class ReqReleaseMember {

    }

    @Data
    public static class ResReleaseMember {

    }
}
