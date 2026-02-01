package org.muses.backendbulidtest251228.domain.admin.entity;

import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.mypage.entity.CreatorApplicationENT;
import org.muses.backendbulidtest251228.domain.mypage.enums.ApplicationStatus;
import org.muses.backendbulidtest251228.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "creator_applcation_audit")
public class CreatorApplicationAuditENT extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "app_id", nullable = false)
	private CreatorApplicationENT application;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	private Member admin;

	@Enumerated(EnumType.STRING)
	@Column(name = "previous_status", nullable = false, length = 20)
	private ApplicationStatus previousStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "current_status", nullable = false, length = 20)
	private ApplicationStatus currentStatus;

	@Builder
	public CreatorApplicationAuditENT(
		CreatorApplicationENT application,
		Member admin,
		ApplicationStatus previousStatus,
		ApplicationStatus currentStatus
	) {
		this.application = application;
		this.admin = admin;
		this.previousStatus = previousStatus;
		this.currentStatus = currentStatus;
	}

	public static CreatorApplicationAuditENT create(
		CreatorApplicationENT application,
		Member admin,
		ApplicationStatus previousStatus,
		ApplicationStatus currentStatus
	) {
		return CreatorApplicationAuditENT.builder()
			.application(application)
			.admin(admin)
			.previousStatus(previousStatus)
			.currentStatus(currentStatus)
			.build();
	}
}
