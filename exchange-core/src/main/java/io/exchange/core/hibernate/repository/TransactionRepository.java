package io.exchange.core.hibernate.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.exchange.core.hibernate.executor.CustomBaseExecutor;
import io.exchange.domain.enums.Category;
import io.exchange.domain.enums.Status;
import io.exchange.domain.hibernate.coin.Coin;
import io.exchange.domain.hibernate.coin.Transaction;

@Repository
public interface TransactionRepository extends CustomBaseExecutor<Transaction, Transaction.Pk> {
    Page<Transaction> findAllByCoinAndCategoryAndStatus(Coin coin, Category category, Status status, Pageable pageable);
    Page<Transaction> findAllByUserIdAndCoinAndCategoryOrderByRegDtmDesc(Long userId, Coin coin, Category category, Pageable pageable);
    Optional<Transaction> findByCoinAndTxIdAndStatus(Coin coin, String txId, Status Status);
    Optional<Transaction> findByCoinAndTxId(Coin coin, String txId);
}
