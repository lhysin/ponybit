package io.exchange.core.hibernate.repository.custom;

import java.util.List;

import io.exchange.core.dto.CoinDto.ResCoinWalletAdminWallet;

public interface CoinRepositoryCustom {

    List<ResCoinWalletAdminWallet> getCreatedCoinAndUserWalletByUserId(Long userId);

}
