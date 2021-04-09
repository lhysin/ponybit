package io.exchange.domain.hibernate.coin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.exchange.domain.enums.Category;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.Status;
import io.exchange.domain.hibernate.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@IdClass(Transaction.Pk.class)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class Transaction implements Serializable {

    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    @NoArgsConstructor(access=AccessLevel.PACKAGE)
    public static class Pk implements Serializable {
        private String txId;
        private Long userId;
        private CoinName coinName;
    }

    @Id
    @Column(name="tx_id", nullable = false)
    private String txId;

    @Id
    @Column(name="user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name="coin_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private CoinName coinName;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", insertable=false, updatable=false, referencedColumnName="id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="coin_name", insertable=false, updatable=false, referencedColumnName="name")
    private Coin coin;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private Status status;

    private String toAddress;
    private String fromAddress;
    private String tag;
    private String bankNm;
    private String recvNm;

    /**
     * Ether itself has 18 decimals, most ERC-20 tokens simply follow that standard.
     * 1 ETH is represented by 10^18 of its natural unit ( 1 Ether = 1,000,000,000,000,000,000 wei ).
     * <19>.<8>
     */
    @Column(precision=37, scale=18)
    private BigDecimal amount;

    private LocalDateTime completeDtm;
    private Long confirmation;
    private String reason;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements TxId, UserId, CoinNameEnum, CategoryEnum, StatusEnum, RegIp, RegDtm {
        TransactionBuilder builder = Transaction.privateBuilder();

        @Override
        public UserId txId(String txId) {
            builder.txId(txId);
            return this;
        }

        @Override
        public CoinNameEnum userId(Long userId) {
            builder.userId(userId);
            return this;
        }

        @Override
        public CategoryEnum coinName(CoinName coinName) {
            builder.coinName(coinName);
            return this;
        }

        @Override
        public StatusEnum category(Category category) {
            builder.category(category);
            return this;
        }

        @Override
        public RegIp status(Status status) {
            builder.status(status);
            return this;
        }

        @Override
        public RegDtm regIp(String regIp) {
            builder.regIp(regIp);
            return this;
        }

        @Override
        public TransactionBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static TxId builder() {
        return new SafeBuilder();
    }

    public interface TxId {
        UserId txId(String txId);
    }
    public interface UserId {
        CoinNameEnum userId(Long userId);
    }
    public interface CoinNameEnum {
        CategoryEnum coinName(CoinName coinName);
    }
    public interface CategoryEnum {
        StatusEnum category(Category category);
    }
    public interface StatusEnum {
        RegIp status(Status status);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        TransactionBuilder regDtm(LocalDateTime regDtm);
    }
}
