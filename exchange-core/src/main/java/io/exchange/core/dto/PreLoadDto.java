package io.exchange.core.dto;

import java.util.List;

import io.exchange.core.dto.ServerStatusDto.ResServerStatus;
import io.exchange.core.dto.UserDto.ResUser;
import lombok.Getter;
import lombok.Setter;

public class PreLoadDto {

    @Getter
    @Setter
    public static class Home {
        ResUser user;
        Long myRefCnt;
        ResServerStatus serverStatus;
    }

    @Getter
    @Setter
    public static class Dashboard {
        ResUser user;
        List<CoinDto.ResCoinWalletAdminWallet> coinWallets;
    }
}
