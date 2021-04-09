package io.exchange.core.hibernate.repository.custom;

import java.util.List;

import io.exchange.core.dto.UserDto.ResUser;
import io.exchange.core.dto.UserDto.UserWithWallets;

public interface UserRepositoryCustom {

    List<UserWithWallets> getAllGroupByWallets();

    List<ResUser> getRefCntTop10Users();

}
