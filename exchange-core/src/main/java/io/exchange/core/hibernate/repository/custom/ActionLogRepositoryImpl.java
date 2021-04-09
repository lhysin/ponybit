package io.exchange.core.hibernate.repository.custom;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.jpa.JPQLQuery;

import io.exchange.core.dto.ActionLogDto;
import io.exchange.core.dto.ActionLogDto.ResActionLog;
import io.exchange.core.dto.UserDto;
import io.exchange.core.hibernate.executor.CustomJpaSupport;
import io.exchange.domain.hibernate.common.ActionLog;
import io.exchange.domain.hibernate.common.QActionLog;
import io.exchange.domain.hibernate.user.QUser;

public class ActionLogRepositoryImpl extends CustomJpaSupport implements ActionLogRepositoryCustom {

    public ActionLogRepositoryImpl(EntityManager em) {
        super(em, ActionLog.class);
    }

    @Override
    public Page<ResActionLog> findAllByUserIdOrderByRegDtmDesc(Long userId, Pageable pageable) {

        QBean<ActionLogDto.ResActionLog> actionLogWithUserBean = 
                Projections.bean(ActionLogDto.ResActionLog.class, 
                        QActionLog.actionLog.id,
                        QActionLog.actionLog.msg,
                        QActionLog.actionLog.logTag,
                        Projections.bean(UserDto.ResUser.class, 
                                QUser.user.id,
                                QUser.user.email,
                                QUser.user.name).as(QUser.user.getMetadata().getName()));

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QActionLog.actionLog.userId.eq(userId));

        JPQLQuery<ActionLogDto.ResActionLog> countQuery = query()
                .select(actionLogWithUserBean)
                .from(QActionLog.actionLog)
                .innerJoin(QActionLog.actionLog.user, QUser.user)
                .where(builder.getValue())
                .orderBy(QActionLog.actionLog.regDtm.desc());

        JPQLQuery<ActionLogDto.ResActionLog> query = getQuerydsl().applyPagination(pageable, countQuery);

        return PageableExecutionUtils.getPage(query.fetch(), pageable, query::fetchCount);
    }


}