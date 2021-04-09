package io.exchange.domain.hibernate.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserToken is a Querydsl query type for UserToken
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUserToken extends EntityPathBase<UserToken> {

    private static final long serialVersionUID = 609191278L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserToken userToken = new QUserToken("userToken");

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public final DateTimePath<java.time.LocalDateTime> sendDtm = createDateTime("sendDtm", java.time.LocalDateTime.class);

    public final StringPath token = createString("token");

    public final EnumPath<io.exchange.domain.enums.UserTokenType> type = createEnum("type", io.exchange.domain.enums.UserTokenType.class);

    public final QUser user;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserToken(String variable) {
        this(UserToken.class, forVariable(variable), INITS);
    }

    public QUserToken(Path<? extends UserToken> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserToken(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserToken(PathMetadata metadata, PathInits inits) {
        this(UserToken.class, metadata, inits);
    }

    public QUserToken(Class<? extends UserToken> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

