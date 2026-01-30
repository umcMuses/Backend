package org.muses.backendbulidtest251228.domain.admin.entity;

import org.muses.backendbulidtest251228.domain.admin.enums.ProjectAuditStatus;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.global.entity.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project_audit")
@EntityListeners(AuditingEntityListener.class)
public class ProjectAuditENT extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private ProjectAuditStatus previousStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private ProjectAuditStatus currentStatus;

	@Column(columnDefinition = "TEXT")
	private String reason;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private ProjectENT project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	private Member admin;

	@Builder
	public ProjectAuditENT(ProjectENT project, Member admin, ProjectAuditStatus previousStatus, ProjectAuditStatus currentStatus, String reason) {
		this.project = project;
		this.admin = admin;
		this.previousStatus = previousStatus;
		this.currentStatus = currentStatus;
		this.reason = reason;
	}
}
