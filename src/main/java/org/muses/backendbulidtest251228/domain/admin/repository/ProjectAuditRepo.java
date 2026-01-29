package org.muses.backendbulidtest251228.domain.admin.repository;

import java.util.List;

import org.muses.backendbulidtest251228.domain.admin.entity.ProjectAuditENT;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectAuditRepo extends JpaRepository<ProjectAuditENT, Long> {
	List<ProjectAuditENT> findByProjectIdOrderByCreatedAt(Long projectId);
}
