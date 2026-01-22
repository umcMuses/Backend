package org.muses.backendbulidtest251228.domain.project.repository;

import org.muses.backendbulidtest251228.domain.project.entity.ProjectLikeENT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
