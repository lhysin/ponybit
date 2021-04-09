package io.exchange.core.hibernate.repository.custom;

import java.util.Optional;

import io.exchange.core.dto.ServerStatusDto;

public interface ServerStatusRepositoryCustom {

    Optional<ServerStatusDto.ResServerStatus> getTopOrderBySetupDtm();

}
