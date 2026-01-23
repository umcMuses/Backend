package org.muses.backendbulidtest251228.domain.project.repository;

import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepo extends JpaRepository<ProjectENT, Long> {

    // 사용자 ID로 프로젝트 목록 조회
    List<ProjectENT> findByUserId(Long userId);

    // 상태별 프로젝트 목록 조회
    List<ProjectENT> findByStatus(String status);

    // 특정 상태 제외하고 프로젝트 목록 조회 (공개용)
    List<ProjectENT> findByStatusNotIn(List<String> statuses);

    // ==================== 결제/마감 관련 메서드 ====================

    // 마감된 프로젝트 조회 (FUNDING 상태이면서 deadline이 지난 프로젝트)
    @Query("SELECT p FROM ProjectENT p WHERE p.fundingStatus = 'FUNDING' AND p.deadline < :now")
    List<ProjectENT> findExpiredActiveProjects(@Param("now") LocalDateTime now, Pageable pageable);

    // 프로젝트 선점 (FUNDING -> CLOSING)
    @Modifying
    @Query("UPDATE ProjectENT p SET p.fundingStatus = 'CLOSING', p.updatedAt = :now " +
           "WHERE p.id = :projectId AND p.fundingStatus = 'FUNDING'")
    int tryAcquireClosing(@Param("projectId") Long projectId, @Param("now") LocalDateTime now);

    // 프로젝트 최종 상태 확정 (CLOSING -> SUCCESS/FAIL)
    @Modifying
    @Query("UPDATE ProjectENT p SET p.fundingStatus = :status, p.updatedAt = :now " +
           "WHERE p.id = :projectId AND p.fundingStatus = 'CLOSING'")
    int finalizeFromClosing(@Param("projectId") Long projectId, 
                            @Param("status") FundingStatus status, 
                            @Param("now") LocalDateTime now);

    // 오래 멈춘 CLOSING 프로젝트 조회 (재시도용)
    @Query("SELECT p FROM ProjectENT p WHERE p.fundingStatus = 'CLOSING' AND p.updatedAt < :threshold")
    List<ProjectENT> findStuckClosing(@Param("threshold") LocalDateTime threshold, Pageable pageable);
}
