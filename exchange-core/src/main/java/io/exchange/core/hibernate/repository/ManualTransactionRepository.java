package io.exchange.core.hibernate.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.exchange.core.hibernate.executor.CustomBaseExecutor;
import io.exchange.core.hibernate.repository.custom.ManualTransactionRepositoryCustom;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.Status;
import io.exchange.domain.hibernate.coin.ManualTransaction;

@Repository
public interface ManualTransactionRepository extends CustomBaseExecutor<ManualTransaction, ManualTransaction.Pk>, ManualTransactionRepositoryCustom {
    Optional<ManualTransaction> findByIdAndUserIdAndAndCoinName(Long id, Long userId, CoinName coinName);
    Optional<ManualTransaction> findByTxId(String txId);
    Optional<ManualTransaction> findByTxIdAndStatusNot(String txId, Status status);
    List<ManualTransaction> findByCoinNameAndTxIdInAndStatus(CoinName coinName, Collection<String> txIds, Status status);
    Optional<ManualTransaction> findByUserIdAndCoinNameAndFromRefUserId(Long userId, CoinName coinName, Long fromRefUserId);
}
