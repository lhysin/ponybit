package io.exchange.core.hibernate.repository.custom;

import java.math.BigDecimal;

import javax.persistence.EntityManager;

import com.querydsl.jpa.JPAExpressions;

import io.exchange.core.hibernate.executor.CustomJpaSupport;
import io.exchange.domain.enums.Category;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.Status;
import io.exchange.domain.enums.UserLevel;
import io.exchange.domain.hibernate.coin.ManualTransaction;
import io.exchange.domain.hibernate.coin.QManualTransaction;
import io.exchange.domain.hibernate.user.QUser;
import io.exchange.domain.hibernate.user.User;

public class ManualTransactionRepositoryImpl extends CustomJpaSupport implements ManualTransactionRepositoryCustom {

    public ManualTransactionRepositoryImpl(EntityManager em) {
        super(em, ManualTransaction.class);
    }

    @Override
    public void changePonyPromotionStatusIsApproval(User user) {

        QManualTransaction qmt = QManualTransaction.manualTransaction;
        QUser quser = QUser.user;

        query().update(qmt)
        .set(qmt.status, Status.APPROVAL)
        .where(qmt.userId.eq(user.getId())
                .and(qmt.status.eq(Status.PENDING))
                .and(qmt.coinName.eq(CoinName.PONYCOIN))
                .and(qmt.category.eq(Category.PROMOTION))
                .and(qmt.fromRefUserId.in(
                        JPAExpressions
                        .select(quser.id)
                        .from(quser)
                        .where(quser.otherRefCd.eq(user.getMyRefCd())
                                .and(quser.userLevel.eq(UserLevel.LEVEL2)))
                                         )
                        .or(qmt.fromRefUserId.isNull())
                    )
                ).execute();

    }

    @Override
    public BigDecimal getAllPonyPromotionBalanceByApproval(User user) {

        QManualTransaction qmt = QManualTransaction.manualTransaction;

        return query().select(qmt.realAmount.sum())
                .from(qmt)
                .where(qmt.userId.eq(user.getId())
                        .and(qmt.status.eq(Status.APPROVAL))
                        .and(qmt.coinName.eq(CoinName.PONYCOIN))
                        .and(qmt.category.eq(Category.PROMOTION)))
                .fetchOne();
    }

}