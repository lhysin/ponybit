package io.exchange.core.hibernate.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;

import io.exchange.core.dto.AdminWalletDto;
import io.exchange.core.dto.CoinDto;
import io.exchange.core.dto.CoinDto.ResCoinWalletAdminWallet;
import io.exchange.core.dto.WalletDto;
import io.exchange.core.hibernate.executor.CustomJpaSupport;
import io.exchange.domain.enums.Active;
import io.exchange.domain.enums.WalletType;
import io.exchange.domain.hibernate.coin.Coin;
import io.exchange.domain.hibernate.coin.QAdminWallet;
import io.exchange.domain.hibernate.coin.QCoin;
import io.exchange.domain.hibernate.coin.QWallet;

public class CoinRepositoryImpl extends CustomJpaSupport implements CoinRepositoryCustom {

    public CoinRepositoryImpl(EntityManager em) {
        super(em, Coin.class);
    }

    // https://stackoverflow.com/questions/42969292/left-join-and-group-of-inner-join
    // https://stackoverflow.com/questions/473607/sql-server-combining-outer-and-inner-joins/473703#473703
    @Override
    public List<ResCoinWalletAdminWallet> getCreatedCoinAndUserWalletByUserId(Long userId) {

        QBean<CoinDto.ResCoinWalletAdminWallet> coinWalletBean = Projections.bean(CoinDto.ResCoinWalletAdminWallet.class
                ,Projections.bean(CoinDto.ResCoin.class, 
                        QCoin.coin.name,
                        QCoin.coin.mark,
                        QCoin.coin.logoUrl).as("coin")
                ,Projections.bean(WalletDto.ResWallet.class,
                        QWallet.wallet.address,
                        QWallet.wallet.coinName,
                        QWallet.wallet.availableBalance).as("wallet")
                ,Projections.bean(AdminWalletDto.ResAdminWallet.class,
                        QAdminWallet.adminWallet.address,
                        QAdminWallet.adminWallet.coinName).as("adminWallet"));

        BooleanBuilder where = new BooleanBuilder();
        where.and(QCoin.coin.active.eq(Active.Y));

        List<CoinDto.ResCoinWalletAdminWallet> coinWallets = query()
                .select(coinWalletBean)
                .from(QCoin.coin)
                .leftJoin(QCoin.coin.wallets, QWallet.wallet)
                .on(QWallet.wallet.userId.eq(userId))
                .innerJoin(QCoin.coin.adminWallets, QAdminWallet.adminWallet)
                .on(QAdminWallet.adminWallet.type.eq(WalletType.HOT))
                .where(where.getValue())
                .orderBy(QCoin.coin.displayPriority.asc()).fetch();

        return coinWallets;
    }

}