package io.exchange.core.hibernate.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.exchange.core.dto.ActionLogDto;

public interface ActionLogRepositoryCustom {

    Page<ActionLogDto.ResActionLog> findAllByUserIdOrderByRegDtmDesc(Long userId, Pageable pageable);

}
