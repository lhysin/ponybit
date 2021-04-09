package io.exchange.domain.hibernate.coin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

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
@IdClass(ManualTransaction.Pk.class)
@TableGenerator(name = "manual_transaction_sequence_generator", 
                table = "sequence",
                pkColumnName = "sequence_name",
                pkColumnValue = "manual_transaction_sequence",
                valueColumnName = "next_val",
                allocationSize = 1,
                initialValue = 0)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class ManualTransaction implements Serializable {

    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    @NoArgsConstructor(access=AccessLevel.PACKAGE)
    public static class Pk implements Serializable {
        private Long id;
        private Long userId;
        private CoinName coinName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "manual_transaction_sequence_generator")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

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

    private String txId;
    private String toAddress;
    private String fromAddress;
    private String tag;
    private String bankNm;
    private String recvNm;

    public void changeStatus(Status status) {
        if(status == null) {
            throw new NullPointerException("ManualTransaction changeStatus() status is null.");
        }
        this.status = status;
    }

    public void injectTxId(String txId) {
        this.txId = txId;
    }

    /**
     * Ether itself has 18 decimals, most ERC-20 tokens simply follow that standard.
     * 1 ETH is represented by 10^18 of its natural unit ( 1 Ether = 1,000,000,000,000,000,000 wei ).
     * <19>.<8>
     * 
     * regexp
     * ^\d{1,18}(\.?\d{1,18})$
     */
    @Column(precision=37, scale=18)
    private BigDecimal reqAmount;

    @Column(precision=37, scale=18)
    private BigDecimal realAmount;

    private LocalDateTime completeDtm;
    private Long confirmation;
    private String reason;
    private Long fromRefUserId;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void injectRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements UserId, CoinNameEnum, CategoryEnum, StatusEnum, RegIp, RegDtm {
        ManualTransactionBuilder builder = ManualTransaction.privateBuilder();

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
        public ManualTransactionBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static UserId builder() {
        return new SafeBuilder();
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
        ManualTransactionBuilder regDtm(LocalDateTime regDtm);
    }
}
