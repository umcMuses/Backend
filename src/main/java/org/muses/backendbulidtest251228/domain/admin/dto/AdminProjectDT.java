package org.muses.backendbulidtest251228.domain.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AdminProjectDT {

	@Getter
	@Builder
	@AllArgsConstructor
	public static class ProjectAuditListResponse {
		private Long projectId;
		private String title;
		private String creatorName;
		private BigDecimal targetAmount;
		private String status;
		private LocalDateTime createdAt;

		public static ProjectAuditListResponse from(ProjectENT project) {
			return ProjectAuditListResponse.builder()
				.projectId(project.getId())
				.title(project.getTitle())
				.creatorName(project.getMember().getName())
				.targetAmount(project.getTargetAmount())
				.status(project.getStatus())
				.createdAt(project.getCreatedAt())
				.build();
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AuditReviewRequest {
		private String status;			// APPROVED or REJECTED
		private String rejectReason;	// 반려 사유 (확장성 고려)
	}
}
