package io.exchange.core.hibernate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.WalletType;
import io.exchange.domain.hibernate.coin.AdminWallet;

@Repository
public interface AdminWalletRepository extends JpaRepository<AdminWallet, AdminWallet.Pk> {
    Optional<AdminWallet> findByCoinNameAndType(CoinName coinName, WalletType type);
}
