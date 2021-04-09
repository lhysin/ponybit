package io.exchange.core.hibernate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.exchange.domain.enums.UserTokenType;
import io.exchange.domain.hibernate.user.UserToken;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, UserToken.Pk> {

    UserToken findOneByToken(String token);

    /* inner join */
    @Query("SELECT ut FROM UserToken ut INNER JOIN ut.user user WHERE user.email = :eamil")
    Optional<UserToken> findByEmail(@Param("eamil") String email);
    @Query("SELECT ut FROM UserToken ut INNER JOIN ut.user user WHERE user.email = :eamil and ut.type = :type")
    Optional<UserToken> findByEmailAndType(@Param("eamil") String email,
            @Param("type") UserTokenType type);

    /* cross join */
    Optional<UserToken> findByUserEmail(String email);
    Optional<UserToken> findByUserEmailAndType(String email, UserTokenType type);

    Optional<UserToken> findByUserIdAndType(Long userId, UserTokenType type);
}
