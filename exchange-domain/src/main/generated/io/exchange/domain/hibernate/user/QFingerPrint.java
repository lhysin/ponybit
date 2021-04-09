package io.exchange.domain.hibernate.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFingerPrint is a Querydsl query type for FingerPrint
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFingerPrint extends EntityPathBase<FingerPrint> {

    private static final long serialVersionUID = 1357633860L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFingerPrint fingerPrint = new QFingerPrint("fingerPrint");

    public final EnumPath<io.exchange.domain.enums.Active> active = createEnum("active", io.exchange.domain.enums.Active.class);

    public final DateTimePath<java.time.LocalDateTime> delDtm = createDateTime("delDtm", java.time.LocalDateTime.class);

    public final StringPath hashKey = createString("hashKey");

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public final QUser user;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QFingerPrint(String variable) {
        this(FingerPrint.class, forVariable(variable), INITS);
    }

    public QFingerPrint(Path<? extends FingerPrint> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFingerPrint(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFingerPrint(PathMetadata metadata, PathInits inits) {
        this(FingerPrint.class, metadata, inits);
    }

    public QFingerPrint(Class<? extends FingerPrint> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

