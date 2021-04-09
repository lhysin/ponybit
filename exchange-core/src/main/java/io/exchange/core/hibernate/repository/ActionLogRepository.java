package io.exchange.core.hibernate.repository;

import org.springframework.stereotype.Repository;

import io.exchange.core.hibernate.executor.CustomBaseExecutor;
import io.exchange.core.hibernate.repository.custom.ActionLogRepositoryCustom;
import io.exchange.domain.hibernate.common.ActionLog;

@Repository
public interface ActionLogRepository extends CustomBaseExecutor<ActionLog, Long>, ActionLogRepositoryCustom {
}
