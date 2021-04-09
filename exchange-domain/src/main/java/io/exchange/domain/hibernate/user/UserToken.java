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

import io.exchange.domain.enums.UserTokenType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@IdClass(UserToken.Pk.class)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class UserToken implements Serializable {

    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    @NoArgsConstructor(access=AccessLevel.PACKAGE)
    public static class Pk implements Serializable {
        private Long userId;
        private UserTokenType type;
    }

    @Id
    @Column(name="user_id", nullable = false)
    private Long userId;

    @Id
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserTokenType type;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", insertable=false, updatable=false, referencedColumnName="id")
    private User user;

    @Column(nullable=false)
    private String token;

    private LocalDateTime sendDtm;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    public void changeToken(String token) {
        this.token = token;
    }

    public void setSendDtm(LocalDateTime sendDtm) {
        this.sendDtm = sendDtm;
    }

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements UserId, Type, RegIp, RegDtm {
        UserTokenBuilder builder = UserToken.privateBuilder();

        @Override
        public Type userId(Long userId) {
            builder.userId(userId);
            return this;
        }

        @Override
        public RegIp type(UserTokenType type) {
            builder.type(type);
            return this;
        }

        @Override
        public RegDtm regIp(String regIp) {
            builder.regIp(regIp);
            return this;
        }

        @Override
        public UserTokenBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static UserId builder() {
        return new SafeBuilder();
    }

    public interface UserId {
        Type userId(Long userId);
    }
    public interface Type {
        RegIp type(UserTokenType type);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        UserTokenBuilder regDtm(LocalDateTime regDtm);
    }
}
