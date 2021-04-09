package io.exchange.core.hibernate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.exchange.core.hibernate.executor.CustomBaseExecutor;
import io.exchange.domain.hibernate.common.Post;

@Repository
public interface PostRepository extends CustomBaseExecutor<Post, Post.Pk> {
    Page<Post> findAllByDelDtmIsNullOrderByRegDtmDesc(Pageable pageable);

    List<Post> findAllByUserId(Long userId);
    Optional<Post> findByUserId(Long userId);

}
