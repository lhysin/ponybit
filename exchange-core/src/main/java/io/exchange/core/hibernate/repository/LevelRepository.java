package io.exchange.core.hibernate.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.LevelEnum;
import io.exchange.domain.hibernate.user.Level;

@Repository
public interface LevelRepository extends JpaRepository<Level, Level.Pk> {
    Optional<Level> findByCoinNameAndLevelEnum(CoinName CoinName, LevelEnum levelEnum);
    Page<Level> findAll(Pageable pageable);
}
