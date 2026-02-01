package org.muses.backendbulidtest251228.domain.mypage.repository;

import org.muses.backendbulidtest251228.domain.mypage.enums.ApplicationStatus;
import org.muses.backendbulidtest251228.domain.mypage.entity.CreatorApplicationENT;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CreatorApplicationREP
        extends JpaRepository<CreatorApplicationENT, Long> {

    boolean existsByMember_IdAndStatus(Long memberId, ApplicationStatus status);

    Optional<CreatorApplicationENT>
    findTopByMember_IdOrderByCreatedAtDesc(Long memberId);

    // === 관리자용 조회 메서드 ===

    // 전체 신청 목록 조회 (페이징, 최신순)
    @Query("SELECT ca FROM CreatorApplicationENT ca "
        + "JOIN FETCH ca.member "
        + "ORDER BY ca.createdAt DESC")
    Page<CreatorApplicationENT> findAllWithMember(Pageable pageable);

    // 상태별 신청 목록 조회 (페이징, 최신순)
    @Query("SELECT ca FROM CreatorApplicationENT ca "
        + "JOIN FETCH ca.member "
        + "WHERE ca.status = :status "
        + "ORDER BY ca.createdAt DESC ")
    Page<CreatorApplicationENT> findByStatusWithMember(
        @Param("status") ApplicationStatus status,
        Pageable pageable
    );

    // 신청 ID로 조회
    @Query("SELECT ca FROM CreatorApplicationENT ca "
        + "JOIN FETCH ca.member "
        + "WHERE ca.appId = :appId")
    Optional<CreatorApplicationENT> findByIdWithMember(@Param("appId") Long appId);

    // 전체 크리에이터 전환 신청 수
    long count();
    // 상태별 전환 신청 수
    long countByStatus(ApplicationStatus status);
}
