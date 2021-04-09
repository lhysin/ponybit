package io.exchange.domain.hibernate.common;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QActionLog is a Querydsl query type for ActionLog
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QActionLog extends EntityPathBase<ActionLog> {

    private static final long serialVersionUID = -1160914162L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QActionLog actionLog = new QActionLog("actionLog");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<io.exchange.domain.enums.LogTag> logTag = createEnum("logTag", io.exchange.domain.enums.LogTag.class);

    public final StringPath msg = createString("msg");

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public final io.exchange.domain.hibernate.user.QUser user;

    public final StringPath userAgent = createString("userAgent");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QActionLog(String variable) {
        this(ActionLog.class, forVariable(variable), INITS);
    }

    public QActionLog(Path<? extends ActionLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QActionLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QActionLog(PathMetadata metadata, PathInits inits) {
        this(ActionLog.class, metadata, inits);
    }

    public QActionLog(Class<? extends ActionLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new io.exchange.domain.hibernate.user.QUser(forProperty("user")) : null;
    }

}

