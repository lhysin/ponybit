package io.exchange.core.hibernate.executor;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class CustomJpaSupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory query;

    public CustomJpaSupport(EntityManager em, Class<?> domainClass) {
        super(domainClass);
        this.query = new JPAQueryFactory(em);
    }

    public JPAQueryFactory query() {
        return this.query;
    }
}
