package io.exchange.core.hibernate.repository.custom;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.JPQLQuery;

import io.exchange.core.dto.CoinDto;
import io.exchange.core.dto.WalletDto.ReqWallets;
import io.exchange.core.dto.WalletDto.ResWallet;
import io.exchange.core.hibernate.executor.CustomJpaSupport;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.hibernate.coin.QCoin;
import io.exchange.domain.hibernate.coin.QWallet;
import io.exchange.domain.hibernate.coin.Wallet;
import io.exchange.domain.hibernate.user.QUser;

public class WalletRepositoryImpl extends CustomJpaSupport implements WalletRepositoryCustom {

    public WalletRepositoryImpl(EntityManager em) {
        super(em, Wallet.class);
    }

    @Override
    public List<ResWallet> getAllWithCoinByUserIds(Collection<Long> userIds) {

        QBean<ResWallet> walletWithCoinBean = 
                Projections.bean(ResWallet.class, QWallet.wallet.userId, QWallet.wallet.coinName, QWallet.wallet.usingBalance, QWallet.wallet.regDtm,
                        Projections.bean(CoinDto.ResCoin.class, QCoin.coin.regDtm).as(QWallet.wallet.coin.getMetadata().getName()));

        List<ResWallet> resWallets = query()
                .select(walletWithCoinBean)
                .from(QWallet.wallet)
                .innerJoin(QWallet.wallet.coin, QCoin.coin)
                .where(QWallet.wallet.userId.in(userIds)).fetch();

        return resWallets;
    }

    @Override
    public Page<ResWallet> getAllDynamicSearch(ReqWallets req, Pageable pageable) {

        QBean<ResWallet> walletWithCoinBean = 
                Projections.bean(ResWallet.class, QWallet.wallet.userId, QWallet.wallet.coinName, QWallet.wallet.usingBalance, QWallet.wallet.regDtm,
                        Projections.bean(CoinDto.ResCoin.class, QCoin.coin.regDtm).as(QWallet.wallet.coin.getMetadata().getName()));

        BooleanBuilder builder = new BooleanBuilder();

        if(req.getCoinName() != null) {
            builder.and(QWallet.wallet.coinName.eq(req.getCoinName()));
        }
        JPQLQuery<ResWallet> countQuery = query()
                .select(walletWithCoinBean)
                .from(QWallet.wallet)
                .innerJoin(QWallet.wallet.coin, QCoin.coin);
        if(StringUtils.isNotEmpty(req.getEmail())) {
            builder.and(QWallet.wallet.user.email.eq(req.getEmail()));
            countQuery.innerJoin(QWallet.wallet.user, QUser.user);
        }

        countQuery.where(builder.getValue());

        JPQLQuery<ResWallet> query = getQuerydsl().applyPagination(pageable, countQuery);

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }

    @Override
    public BigDecimal getAllSoldPonyBalance() {

        Map<CoinName, BigDecimal> results = query()
                .from(QWallet.wallet)
                .transform(
                        GroupBy.groupBy(QWallet.wallet.coinName)
                        .as(GroupBy.max(QWallet.wallet.availableBalance)));

        BigDecimal allSoldPonyBalance = results.get(CoinName.PONYCOIN);

        return allSoldPonyBalance;
    }

}