package io.exchange.domain.hibernate.user;

import java.io.Serializable;
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

import io.exchange.domain.enums.Active;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@IdClass(FingerPrint.Pk.class)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class FingerPrint implements Serializable{

    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    @NoArgsConstructor(access=AccessLevel.PACKAGE)
    public static class Pk implements Serializable {
        private Long userId;
        private String hashKey;
    }

    @Id
    @Column(name="user_id", nullable = false)
    private Long userId;

    @Id
    @Column(nullable = false)
    private String hashKey;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", insertable=false, updatable=false, referencedColumnName="id")
    private User user;

    private LocalDateTime delDtm;

    @Column(nullable=false, columnDefinition="char(1)")
    @Enumerated(EnumType.STRING)
    private Active active;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements UserId, HashKey, ActiveEnum, RegIp, RegDtm {
        FingerPrintBuilder builder = FingerPrint.privateBuilder();

        @Override
        public HashKey userId(Long userId) {
            builder.userId(userId);
            return this;
        }

        @Override
        public ActiveEnum hashKey(String hashKey) {
            builder.hashKey(hashKey);
            return this;
        }

        @Override
        public RegIp active(Active active) {
            builder.active(active);
            return this;
        }

        @Override
        public RegDtm regIp(String regIp) {
            builder.regIp(regIp);
            return this;
        }

        @Override
        public FingerPrintBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static UserId builder() {
        return new SafeBuilder();
    }

    public interface UserId {
        HashKey userId(Long userId);
    }
    public interface HashKey {
        ActiveEnum hashKey(String hashKey);
    }
    public interface ActiveEnum {
        RegIp active(Active active);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        FingerPrintBuilder regDtm(LocalDateTime regDtm);
    }
}
