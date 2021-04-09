package io.exchange.domain.hibernate.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QKakaoUser is a Querydsl query type for KakaoUser
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QKakaoUser extends EntityPathBase<KakaoUser> {

    private static final long serialVersionUID = 765579182L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QKakaoUser kakaoUser = new QKakaoUser("kakaoUser");

    public final EnumPath<io.exchange.domain.enums.Active> isKakaoTalkUser = createEnum("isKakaoTalkUser", io.exchange.domain.enums.Active.class);

    public final StringPath kakaoCountryISO = createString("kakaoCountryISO");

    public final StringPath kakaoNickName = createString("kakaoNickName");

    public final StringPath kakaoProfileImageURL = createString("kakaoProfileImageURL");

    public final StringPath kakaoThumbnailURL = createString("kakaoThumbnailURL");

    public final StringPath kakaoUserId = createString("kakaoUserId");

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public final QUser user;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QKakaoUser(String variable) {
        this(KakaoUser.class, forVariable(variable), INITS);
    }

    public QKakaoUser(Path<? extends KakaoUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QKakaoUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QKakaoUser(PathMetadata metadata, PathInits inits) {
        this(KakaoUser.class, metadata, inits);
    }

    public QKakaoUser(Class<? extends KakaoUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

