package org.muses.backendbulidtest251228.domain.project.repository;

import org.muses.backendbulidtest251228.domain.project.entity.ProjectLikeENT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectLikeRepo extends JpaRepository<ProjectLikeENT, Long> {

    // 특정 회원이 특정 프로젝트에 좋아요 했는지 확인
    boolean existsByMemberIdAndProjectId(Long memberId, Long projectId);

    // 특정 회원의 특정 프로젝트 좋아요 조회
    Optional<ProjectLikeENT> findByMemberIdAndProjectId(Long memberId, Long projectId);

    // 프로젝트의 좋아요 수 조회
    long countByProjectId(Long projectId);

    // 특정 회원의 특정 프로젝트 좋아요 삭제
    void deleteByMemberIdAndProjectId(Long memberId, Long projectId);

    // 마이페이지 내가 좋아요한 프로젝트
    @Query(
        value = """
        select pl
        from ProjectLikeENT pl
        join fetch pl.project p
        where pl.member.id = :memberId
        order by pl.createdAt desc
    """,
        countQuery = """
        select count(pl)
        from ProjectLikeENT pl
        where pl.member.id = :memberId
        """
    )
    Page<ProjectLikeENT> findAllByMemberIdWithProject(@Param("memberId") Long memberId, Pageable pageable);

    // 프로젝트에 좋아요 누른 멤버 ID 목록 조회 (알람 발송용)
    @Query("SELECT pl.member.id FROM ProjectLikeENT pl WHERE pl.project.id = :projectId")
    List<Long> findMemberIdsByProjectId(@Param("projectId") Long projectId);

}
