package io.exchange.core.hibernate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.exchange.domain.enums.Active;
import io.exchange.domain.hibernate.user.KakaoUser;

@Repository
public interface KakaoUserRepository extends JpaRepository<KakaoUser, KakaoUser.Pk> {
    Optional<KakaoUser> findByUserId(Long userId);
    Optional<KakaoUser> findByKakaoUserId(String kakaoUserId);
}
