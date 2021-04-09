package io.exchange.domain.hibernate.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 145267531L;

    public static final QUser user = new QUser("user");

    public final DateTimePath<java.time.LocalDateTime> delDtm = createDateTime("delDtm", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath myRefCd = createString("myRefCd");

    public final StringPath name = createString("name");

    public final StringPath otherRefCd = createString("otherRefCd");

    public final StringPath otpHash = createString("otpHash");

    public final StringPath pwd = createString("pwd");

    public final DateTimePath<java.time.LocalDateTime> regDtm = createDateTime("regDtm", java.time.LocalDateTime.class);

    public final StringPath regIp = createString("regIp");

    public final EnumPath<io.exchange.domain.enums.Role> role = createEnum("role", io.exchange.domain.enums.Role.class);

    public final EnumPath<io.exchange.domain.enums.UserLevel> userLevel = createEnum("userLevel", io.exchange.domain.enums.UserLevel.class);

    public final CollectionPath<io.exchange.domain.hibernate.coin.Wallet, io.exchange.domain.hibernate.coin.QWallet> wallets = this.<io.exchange.domain.hibernate.coin.Wallet, io.exchange.domain.hibernate.coin.QWallet>createCollection("wallets", io.exchange.domain.hibernate.coin.Wallet.class, io.exchange.domain.hibernate.coin.QWallet.class, PathInits.DIRECT2);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

