package io.exchange.core.hibernate.executor;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.data.repository.core.RepositoryMetadata;

@SuppressWarnings("deprecation")
public class CustomJpaFactory extends JpaRepositoryFactory {

    public CustomJpaFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if(QuerydslUtils.QUERY_DSL_PRESENT) {
            Class<?> repositoryInterface = metadata.getRepositoryInterface();
            if(CustomBaseExecutor.class.isAssignableFrom(repositoryInterface)) {
                return CustomBaseRepository.class;
            } else  if(QuerydslPredicateExecutor.class.isAssignableFrom(repositoryInterface)) {
                return QuerydslJpaRepository.class;
            }
        }
        return SimpleJpaRepository.class;
    }
}