package io.exchange.core.hibernate.executor;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.support.PageableExecutionUtils;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@SuppressWarnings("deprecation")
public class CustomBaseRepository<T, ID extends Serializable> extends QuerydslJpaRepository<T, ID>
        implements CustomBaseExecutor<T, ID> {

    // All instance variables are available in super, but they are private
    private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER = SimpleEntityPathResolver.INSTANCE;

    private final EntityPath<T> path;
    private final PathBuilder<T> builder;
    private final Querydsl querydsl;
    private final JPAQueryFactory query;

    public CustomBaseRepository(JpaEntityInformation<T, ID> entityInformation,
            EntityManager entityManager) {
        this(entityInformation, entityManager, DEFAULT_ENTITY_PATH_RESOLVER);
    }

    public CustomBaseRepository(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager,
            EntityPathResolver resolver) {

        super(entityInformation, entityManager, resolver);
        this.path = resolver.createPath(entityInformation.getJavaType());
        this.builder = new PathBuilder<T>(path.getType(), path.getMetadata());
        this.querydsl = new Querydsl(entityManager, builder);
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public <R> List<R> findAll(FactoryExpression<R> factoryExpression) {
        JPQLQuery<R> jpqlQuery = query
                .select(factoryExpression)
                .from(path);
        return jpqlQuery.fetch();
    }

    @Override
    public <R> List<R> findAll(FactoryExpression<R> factoryExpression, Predicate predicate) {
        JPQLQuery<R> jpqlQuery = query
                .select(factoryExpression)
                .from(path)
                .where(predicate);
        return jpqlQuery.fetch();
    }

    @Override
    public <R> List<R> findAll(FactoryExpression<R> factoryExpression, OrderSpecifier<?>... orders) {
        JPQLQuery<R> jpqlQuery = query
                .select(factoryExpression)
                .from(path)
                .orderBy(orders);
        return jpqlQuery.fetch();
    }

    @Override
    public <R> List<R> findAll(FactoryExpression<R> factoryExpression, Predicate predicate, OrderSpecifier<?>... orders) {
        JPQLQuery<R> jpqlQuery = query
                .select(factoryExpression)
                .from(path)
                .where(predicate)
                .orderBy(orders);
        return jpqlQuery.fetch();
    }

    @Override
    public <R> Page<R> findAll(FactoryExpression<R> factoryExpression, Pageable pageable, OrderSpecifier<?>... orders) {
        JPQLQuery<R> countQuery = query
                .select(factoryExpression)
                .from(path);

        JPQLQuery<R> jpqlQuery = querydsl.applyPagination(pageable, countQuery);

        return PageableExecutionUtils.getPage(jpqlQuery.fetch(), pageable, countQuery::fetchCount);
    }

    @Override
    public <R> Page<R> findAll(FactoryExpression<R> factoryExpression, Predicate predicate, Pageable pageable) {

        JPQLQuery<R> countQuery = query
                .select(factoryExpression)
                .from(path)
                .where(predicate);

        JPQLQuery<R> jpqlQuery = querydsl.applyPagination(pageable, countQuery);

        return PageableExecutionUtils.getPage(jpqlQuery.fetch(), pageable, countQuery::fetchCount);
    }

    @Override
    public <R> Page<R> findAll(FactoryExpression<R> factoryExpression, Predicate predicate, Pageable pageable, OrderSpecifier<?>... orders) {

        JPQLQuery<R> countQuery = query
                .select(factoryExpression)
                .from(path)
                .where(predicate)
                .orderBy(orders);

        JPQLQuery<R> jpqlQuery = querydsl.applyPagination(pageable, countQuery);

        return PageableExecutionUtils.getPage(jpqlQuery.fetch(), pageable, countQuery::fetchCount);
    }

    @Override
    public <R> Optional<R> findOne(FactoryExpression<R> factoryExpression, Predicate predicate) {
        JPQLQuery<R> jpqlQuery = query
                .select(factoryExpression)
                .from(path)
                .where(predicate);
        try {
            return Optional.ofNullable(jpqlQuery.fetchOne());
        } catch (NonUniqueResultException ex) {
            throw new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, ex);
        }
    }

    @Override
    public <R> Optional<R> findOneFirst(FactoryExpression<R> factoryExpression, Predicate predicate,
            OrderSpecifier<?>... orders) {
        JPQLQuery<R> jpqlQuery = query
                .select(factoryExpression)
                .from(path)
                .where(predicate)
                .orderBy(orders);
        try {
            return Optional.ofNullable(jpqlQuery.fetchFirst());
        } catch (NonUniqueResultException ex) {
            throw new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, ex);
        }
    }

}