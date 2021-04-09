package io.exchange.core.hibernate.repository;

import java.util.List;
import java.util.Optional;

import io.exchange.core.hibernate.executor.CustomBaseExecutor;
import io.exchange.core.hibernate.repository.custom.UserRepositoryCustom;
import io.exchange.domain.enums.UserLevel;
import io.exchange.domain.hibernate.user.User;

public interface UserRepository extends CustomBaseExecutor<User, Long>, UserRepositoryCustom {
    Optional<User> findByEmail(String email);
    Optional<User> findByMyRefCd(String refCd);
    Long countByOtherRefCdAndUserLevel(String myRefCd, UserLevel userLevel);
    List<User> findByOtherRefCd(String myRefCd);
}
