package io.exchange.core.hibernate.repository.custom;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.exchange.core.dto.WalletDto.ReqWallets;
import io.exchange.core.dto.WalletDto.ResWallet;

public interface WalletRepositoryCustom {

    List<ResWallet> getAllWithCoinByUserIds(Collection<Long> userIds);

    Page<ResWallet> getAllDynamicSearch(ReqWallets req, Pageable pageable);

    BigDecimal getAllSoldPonyBalance();

}
