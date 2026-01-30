package org.muses.backendbulidtest251228.domain.project.repository;

import org.muses.backendbulidtest251228.domain.project.entity.ProjectTagENT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTagRepo extends JpaRepository<ProjectTagENT, Long> {

    // 프로젝트 ID로 태그 목록 조회
    List<ProjectTagENT> findByProjectId(Long projectId);

    // 프로젝트 ID로 태그 전체 삭제
    @Modifying
    @Query("DELETE FROM ProjectTagENT t WHERE t.project.id = :projectId")
    void deleteByProjectId(Long projectId);
}
