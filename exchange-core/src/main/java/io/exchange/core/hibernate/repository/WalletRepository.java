package io.exchange.core.hibernate.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import io.exchange.core.hibernate.executor.CustomBaseExecutor;
import io.exchange.core.hibernate.repository.custom.WalletRepositoryCustom;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.hibernate.coin.Wallet;

public interface WalletRepository extends CustomBaseExecutor<Wallet, Wallet.Pk>, WalletRepositoryCustom {
    Optional<Wallet> findByUserIdAndCoinName(Long userId, CoinName coinName);
//    Optional<Wallet> findOne(Specification<Wallet> sepc);

//    Optional<Wallet> findOneByPkCoinAndDepositDvcd(Coin coin, String depositDvcd);
//    Optional<Wallet> findOneByPkCoinAndAddress(Coin coin, String address);
//    List<Wallet> findAllByPkUserId(Long userId);
//    List<Wallet> findAllByPkCoin(Coin coin);

    Page<Wallet> findAll(Specification<Wallet> sepc, Pageable pageable);
}
