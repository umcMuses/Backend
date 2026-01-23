package org.muses.backendbulidtest251228.domain.project.repository;

import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepo extends JpaRepository<RewardENT, Long> {

    // 프로젝트 ID로 리워드 목록 조회
    List<RewardENT> findByProjectId(Long projectId);

    // 프로젝트 ID로 리워드 전체 삭제
    @Modifying
    @Query("DELETE FROM RewardENT r WHERE r.project.id = :projectId")
    void deleteByProjectId(Long projectId);
}
