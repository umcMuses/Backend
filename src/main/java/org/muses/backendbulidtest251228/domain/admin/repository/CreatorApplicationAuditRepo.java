package org.muses.backendbulidtest251228.domain.admin.repository;

import java.util.List;

import org.muses.backendbulidtest251228.domain.admin.entity.CreatorApplicationAuditENT;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatorApplicationAuditRepo extends JpaRepository<CreatorApplicationAuditENT, Long> {
	// 특정 신청의 심사 이력 조회 (최신순)
	List<CreatorApplicationAuditENT> findByApplication_AppIdOrderByCreatedAtDesc(Long appId);
	// 특정 관리자의 심사 이력 조회
	List<CreatorApplicationAuditENT> findByAdmin_IdOrderByCreatedAtDesc(Long adminId);
}
