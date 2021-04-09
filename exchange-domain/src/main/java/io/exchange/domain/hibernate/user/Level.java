package io.exchange.domain.hibernate.user;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;

import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.LevelEnum;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@IdClass(Level.Pk.class)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class Level implements Serializable {

    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    @NoArgsConstructor(access=AccessLevel.PACKAGE)
    public static class Pk implements Serializable {
        private CoinName coinName;
        private LevelEnum levelEnum;
    }

    @Id
    @Enumerated(EnumType.STRING)
    private CoinName coinName;

    @Id
    @Enumerated(EnumType.STRING)
    private LevelEnum levelEnum;

    private BigDecimal onceAmount;
    private BigDecimal onedayAmount;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements CoinNameEnum, LevelEnumType, RegIp, RegDtm {
        LevelBuilder builder = Level.privateBuilder();

        @Override
        public LevelEnumType coinName(CoinName coinName) {
            builder.coinName(coinName);
            return this;
        }

        @Override
        public RegIp levelEnum(LevelEnum levelEnum) {
            builder.levelEnum(levelEnum);
            return this;
        }

        @Override
        public RegDtm regIp(String regIp) {
            builder.regIp(regIp);
            return this;
        }

        @Override
        public LevelBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static CoinNameEnum builder() {
        return new SafeBuilder();
    }

    public interface CoinNameEnum {
        LevelEnumType coinName(CoinName coinName);
    }
    public interface LevelEnumType {
        RegIp levelEnum(LevelEnum levelEnum);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        LevelBuilder regDtm(LocalDateTime regDtm);
    }
}
