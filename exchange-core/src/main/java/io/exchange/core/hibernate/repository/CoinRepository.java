package io.exchange.core.hibernate.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.exchange.core.hibernate.executor.CustomBaseExecutor;
import io.exchange.core.hibernate.repository.custom.CoinRepositoryCustom;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.hibernate.coin.Coin;

@Repository
public interface CoinRepository extends CustomBaseExecutor<Coin, CoinName>, CoinRepositoryCustom {
    List<Coin> findAllByOrderByDisplayPriorityAsc();
}
