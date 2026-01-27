package org.muses.backendbulidtest251228.domain.settlement.repository;

import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.settlement.entity.SettlementENT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettlementRepo extends JpaRepository<SettlementENT, Long> {

    // 프로젝트 객체로 기존 정산 정보를 조회
    Optional<SettlementENT> findByProject(ProjectENT project);
}
