package io.exchange.core.hibernate.repository.custom;

import java.math.BigDecimal;

import io.exchange.domain.hibernate.user.User;

public interface ManualTransactionRepositoryCustom {

    public void changePonyPromotionStatusIsApproval(User user);
    public BigDecimal getAllPonyPromotionBalanceByApproval(User user);
}
