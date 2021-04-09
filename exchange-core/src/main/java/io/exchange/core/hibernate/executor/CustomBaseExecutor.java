package io.exchange.core.hibernate.executor;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

@NoRepositoryBean
public interface CustomBaseExecutor<T, ID extends Serializable>
        extends JpaRepository<T, ID>, QuerydslPredicateExecutor<T> {

    <R> Optional<R> findOne(FactoryExpression<R> factoryExpression, Predicate predicate);

    <R> Optional<R> findOneFirst(FactoryExpression<R> factoryExpression, Predicate predicate, OrderSpecifier<?>... orders);

    <R> List<R> findAll(FactoryExpression<R> factoryExpression);

    <R> List<R> findAll(FactoryExpression<R> factoryExpression, Predicate predicate);

    <R> List<R> findAll(FactoryExpression<R> factoryExpression, OrderSpecifier<?>... orders);

    <R> List<R> findAll(FactoryExpression<R> factoryExpression, Predicate predicate, OrderSpecifier<?>... orders);

    <R> Page<R> findAll(FactoryExpression<R> factoryExpression, Pageable pageable, OrderSpecifier<?>... orders);

    <R> Page<R> findAll(FactoryExpression<R> factoryExpression, Predicate predicate, Pageable pageable);

    <R> Page<R> findAll(FactoryExpression<R> factoryExpression, Predicate predicate, Pageable pageable, OrderSpecifier<?>... orders);
}