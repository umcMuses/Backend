package org.muses.backendbulidtest251228.domain.admin.service;

import org.muses.backendbulidtest251228.domain.admin.dto.AdminProjectDT;
import org.muses.backendbulidtest251228.domain.admin.entity.ProjectAuditENT;
import org.muses.backendbulidtest251228.domain.admin.enums.ProjectAuditStatus;
import org.muses.backendbulidtest251228.domain.admin.repository.ProjectAuditRepo;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectDetailResponseDT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.muses.backendbulidtest251228.domain.project.service.ProjectSRV;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminProjectSRV {

	private final ProjectRepo projectRepo;
	private final ProjectSRV projectSRV;
	private final ProjectAuditRepo projectAuditRepo;
	private final MemberRepo memberRepo;

	// 심사 프로젝트 목록 조회
	@Transactional(readOnly = true)
	public Page<AdminProjectDT.ProjectAuditListResponse> getProjectAuditList(
			ProjectAuditStatus status, Pageable pageable
	) {
		Page<ProjectENT> projects;

		if (status == null) {
			projects = projectRepo.findAll(pageable);
		} else {
			projects = projectRepo.findByStatus(status.toString(), pageable);
		}

		return projects.map(AdminProjectDT.ProjectAuditListResponse::from);
	}

	// 프로젝트 상세 조회
	@Transactional(readOnly = true)
	public ProjectDetailResponseDT getProjectDetail(Long projectId) {
		return projectSRV.getProjectDetail(projectId);
	}

	// 프로젝트 심사 처리(승인/반려) + 이력 저장
	public void reviewProject(Long projectId, Long adminId, AdminProjectDT.AuditReviewRequest request) {

		ProjectENT project = projectRepo.findById(projectId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

		Member admin = memberRepo.findById(adminId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "관리자 정보를 찾을 수 없습니다."));

		ProjectAuditStatus oldStatus = ProjectAuditStatus.valueOf(project.getStatus());
		ProjectAuditStatus newStatus;
		try {
			newStatus = ProjectAuditStatus.valueOf(request.getStatus().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException(ErrorCode.BAD_REQUEST, "잘못된 상태 값입니다.");
		}

		if (newStatus == ProjectAuditStatus.APPROVED) {
			project.updateStatus(ProjectAuditStatus.APPROVED.toString());
			// PREPARING -> SCHEDULED(오픈예정) update
			project.updateFundingStatus(FundingStatus.SCHEDULED);
		}
		else if (newStatus == ProjectAuditStatus.REJECTED) {
			project.updateStatus(ProjectAuditStatus.REJECTED.toString());
		}
		else if (newStatus == ProjectAuditStatus.REVISION_REQUEST) {
			// 현재는 수정 요청을 고려하지 않음
		}

		ProjectAuditENT audit = ProjectAuditENT.builder()
			.project(project)
			.admin(admin)
			.previousStatus(oldStatus)
			.currentStatus(newStatus)
			.reason(request.getRejectReason())
			.build();

		projectAuditRepo.save(audit);
	}
}
