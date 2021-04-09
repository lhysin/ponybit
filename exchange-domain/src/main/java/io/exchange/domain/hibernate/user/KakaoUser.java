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
import javax.persistence.OneToMany;

import io.exchange.domain.enums.Active;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@IdClass(KakaoUser.Pk.class)
@Builder(builderMethodName="privateBuilder")
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@SuppressWarnings("serial")
public class KakaoUser implements Serializable {

    @Builder
    @Getter
    @EqualsAndHashCode
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    @NoArgsConstructor(access=AccessLevel.PACKAGE)
    public static class Pk implements Serializable {
        private Long userId;
        private String kakaoUserId;
    }

    @Id
    @Column(name="user_id", nullable = false)
    private Long userId;

    @Id
    @Column(nullable = false)
    private String kakaoUserId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", insertable=false, updatable=false, referencedColumnName="id")
    private User user;

    @Column(columnDefinition="char(1)")
    @Enumerated(EnumType.STRING)
    private Active isKakaoTalkUser;

    private String kakaoNickName;
    private String kakaoProfileImageURL;
    private String kakaoThumbnailURL;
    private String kakaoCountryISO;

    @Column(nullable=false)
    private String regIp;

    @Column(nullable=false)
    private LocalDateTime regDtm;

    public void activeKakaoUser() {
        this.isKakaoTalkUser = Active.Y;
    }

    /**
     * builder pattern mandatory fields with Lombok.Builder
     */
    public static class SafeBuilder implements UserId, KakaoUserId, RegIp, RegDtm {
        KakaoUserBuilder builder = KakaoUser.privateBuilder();

        @Override
        public KakaoUserId userId(Long userId) {
            builder.userId(userId);
            return this;
        }

        @Override
        public RegIp kakaoUserId(String kakaoUserId) {
            builder.kakaoUserId(kakaoUserId);
            return this;
        }

        @Override
        public RegDtm regIp(String regIp) {
            builder.regIp(regIp);
            return this;
        }

        @Override
        public KakaoUserBuilder regDtm(LocalDateTime regDtm) {
            builder.regDtm(regDtm);
            return builder;
        }
    }

    public static UserId builder() {
        return new SafeBuilder();
    }

    public interface UserId {
        KakaoUserId userId(Long userId);
    }
    public interface KakaoUserId {
        RegIp kakaoUserId(String kakaoUserId);
    }
    public interface RegIp {
        RegDtm regIp(String regIp);
    }
    public interface RegDtm {
        KakaoUserBuilder regDtm(LocalDateTime regDtm);
    }
}
