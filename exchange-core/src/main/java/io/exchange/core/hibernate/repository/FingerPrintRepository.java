package io.exchange.core.hibernate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.exchange.domain.enums.Active;
import io.exchange.domain.hibernate.user.FingerPrint;

@Repository
public interface FingerPrintRepository extends JpaRepository<FingerPrint, FingerPrint.Pk> {
   List<FingerPrint> findAllByUserIdAndDelDtmIsNullAndActive(Long userId, Active active);
}
