package io.exchange.core.hibernate.repository.custom;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import com.google.common.collect.Lists;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.impl.JPAQuery;

import io.exchange.core.dto.UserDto.ResUser;
import io.exchange.core.dto.UserDto.UserWithWallets;
import io.exchange.core.dto.WalletDto;
import io.exchange.core.hibernate.executor.CustomJpaSupport;
import io.exchange.domain.hibernate.coin.QWallet;
import io.exchange.domain.hibernate.user.QUser;
import io.exchange.domain.hibernate.user.User;

public class UserRepositoryImpl extends CustomJpaSupport implements UserRepositoryCustom {

    public UserRepositoryImpl(EntityManager em) {
        super(em, User.class);
    }

    @Override
    public List<UserWithWallets> getAllGroupByWallets() {

        QUser quser = QUser.user;
        QWallet qwallet = QWallet.wallet;

        QBean<WalletDto.ResWallet> walletBean = Projections.bean(WalletDto.ResWallet.class, qwallet.coinName, qwallet.usingBalance, qwallet.regDtm);
        QBean<UserWithWallets> userBeanWithWallets = 
                Projections.bean(UserWithWallets.class, quser.id, quser.email, quser.name,
                        GroupBy.list(walletBean).as(quser.wallets.getMetadata().getName()));

        Map<Long, UserWithWallets> transform = query()
                .select(walletBean)
                .from(quser)
                .innerJoin(quser.wallets, qwallet)
                .transform(GroupBy.groupBy(quser.id).as(userBeanWithWallets));

        return Lists.newArrayList(transform.values());
    }

    @Override
    public List<ResUser> getRefCntTop10Users() {

        QUser quser = QUser.user;
        QUser qanotherUser = new QUser("qanotherUser");

        QBean<ResUser> userBean = 
                Projections.bean(ResUser.class, quser.id, quser.email, qanotherUser.id.count().as("refCnt"));

        JPAQuery<ResUser> query =query()
                .select(userBean)
                .from(quser)
                .join(qanotherUser)
                .on(qanotherUser.otherRefCd.eq(quser.myRefCd))
                .groupBy(quser.id, quser.email)
                .orderBy(qanotherUser.id.count().desc())
                .limit(10);

        List<ResUser> resUserList = query.fetch();

        return resUserList;
    }

}