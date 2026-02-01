package org.muses.backendbulidtest251228.domain.admin.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.muses.backendbulidtest251228.domain.admin.dto.AdminCreatorDT;
import org.muses.backendbulidtest251228.domain.admin.repository.CreatorApplicationAuditRepo;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.mypage.entity.CreatorApplicationENT;
import org.muses.backendbulidtest251228.domain.mypage.enums.ApplicationStatus;
import org.muses.backendbulidtest251228.domain.mypage.enums.CreatorType;
import org.muses.backendbulidtest251228.domain.mypage.enums.DocType;
import org.muses.backendbulidtest251228.domain.mypage.repository.CreatorApplicationDocRepo;
import org.muses.backendbulidtest251228.domain.mypage.repository.CreatorApplicationREP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCreatorSRVI implements AdminCreatorSRV {

	private final CreatorApplicationREP applicationRepo;
	private final CreatorApplicationDocRepo docRepo;
	private final CreatorApplicationAuditRepo auditRepo;
	private final MemberRepo memberRepo;

	@Override
	public AdminCreatorDT.ApplicationListResponse getApplicationList(
		ApplicationStatus status,
		int page,
		int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		Page<CreatorApplicationENT> applicationPage;
		long totalCount;

		if (status == null) {
			applicationPage = applicationRepo.findAllWithMember(pageable);
			totalCount = applicationRepo.count();
		} else {
			applicationPage = applicationRepo.findByStatusWithMember(status, pageable);
			totalCount = applicationRepo.countByStatus(status);
		}

		List<AdminCreatorDT.ApplicationListItem> items = applicationPage.getContent().stream()
			.map(this::toApplicationListItem)
			.collect(Collectors.toList());

		return AdminCreatorDT.ApplicationListResponse.builder()
			.items(items)
			.totalCount(totalCount)
			.page(page)
			.size(size)
			.build();
	}

	private AdminCreatorDT.ApplicationListItem toApplicationListItem(CreatorApplicationENT app) {
		Member member = app.getMember();
		CreatorType creatorType = app.getCreatorType();
		ApplicationStatus status = app.getStatus();

		// 서류 개수 조회
		long docCount = docRepo.countByApplication_AppId(app.getAppId());
		Set<DocType> requiredDocs = getRequiredDocs(creatorType);

		return AdminCreatorDT.ApplicationListItem.builder()
			.applicationId(app.getAppId())
			.memberId(member.getId())
			.name(member.getName())
			.creatorType(creatorType.name())
			.creatorTypeDescription(getCreatorTypeDescription(creatorType))
			.status(status.name())
			.statusDescription(getStatusDescription(status))
			.createdAt(app.getCreatedAt())
			.build();
	}

	// ========== 유틸리티 메서드 ==========

	/**
	 * 크리에이터 유형별 필수 서류
	 */
	private Set<DocType> getRequiredDocs(CreatorType type) {
		return switch (type) {
			case INDIVIDUAL -> Set.of(DocType.ID_CARD, DocType.BANKBOOK);
			case SOLE_BIZ -> Set.of(DocType.BRC, DocType.ID_CARD, DocType.BANKBOOK);
			case CORP_BIZ -> Set.of(DocType.BRC, DocType.BANKBOOK, DocType.COMP_SEAL, DocType.COMP_REGISTRY);
		};
	}

	/**
	 *  신청 상태, 크리에이터 유형 한글 설명
	 */
	private String getCreatorTypeDescription(CreatorType type) {
		return switch (type) {
			case INDIVIDUAL -> "개인";
			case SOLE_BIZ -> "개인사업자";
			case CORP_BIZ -> "법인사업자";
		};
	}

	private String getStatusDescription(ApplicationStatus status) {
		return switch (status) {
			case PENDING -> "대기중";
			case APPROVED -> "승인됨";
			case REJECTED -> "반려됨";
		};
	}
}
