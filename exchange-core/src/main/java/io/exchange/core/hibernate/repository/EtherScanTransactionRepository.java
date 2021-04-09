package io.exchange.core.hibernate.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.exchange.core.hibernate.executor.CustomBaseExecutor;
import io.exchange.core.hibernate.repository.custom.EtherScanTransactionRepositoryCustom;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.hibernate.coin.EtherscanTransaction;

@Repository
public interface EtherScanTransactionRepository extends CustomBaseExecutor<EtherscanTransaction, EtherscanTransaction.Pk>, EtherScanTransactionRepositoryCustom {
    Optional<EtherscanTransaction> findByHashAndCoinName(String hash, CoinName coinName);
}
