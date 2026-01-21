package org.muses.backendbulidtest251228.domain.project.repository;

import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepo extends JpaRepository<ProjectENT, Long> {

    // 사용자 ID로 프로젝트 목록 조회
    List<ProjectENT> findByUserId(Long userId);

    // 상태별 프로젝트 목록 조회
    List<ProjectENT> findByStatus(String status);

    // 특정 상태 제외하고 프로젝트 목록 조회 (공개용)
    List<ProjectENT> findByStatusNotIn(List<String> statuses);
}
