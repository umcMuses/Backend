package org.muses.backendbulidtest251228.domain.admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.muses.backendbulidtest251228.domain.admin.dto.AdminCreatorDT;
import org.muses.backendbulidtest251228.domain.admin.entity.CreatorApplicationAuditENT;
import org.muses.backendbulidtest251228.domain.admin.repository.CreatorApplicationAuditRepo;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.enums.Role;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.mypage.entity.CreatorApplicationDocENT;
import org.muses.backendbulidtest251228.domain.mypage.entity.CreatorApplicationENT;
import org.muses.backendbulidtest251228.domain.mypage.enums.ApplicationStatus;
import org.muses.backendbulidtest251228.domain.mypage.enums.CreatorType;
import org.muses.backendbulidtest251228.domain.mypage.enums.DocType;
import org.muses.backendbulidtest251228.domain.mypage.repository.CreatorApplicationDocRepo;
import org.muses.backendbulidtest251228.domain.mypage.repository.CreatorApplicationREP;
import org.muses.backendbulidtest251228.domain.storage.entity.AttachmentENT;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<CreatorApplicationENT> applicationPage;

		if (status == null) {
			applicationPage = applicationRepo.findAllWithMember(pageable);

		} else {
			applicationPage = applicationRepo.findByStatusWithMember(status, pageable);
		}
		long totalCount = applicationPage.getTotalElements();

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

	@Override
	public AdminCreatorDT.DocumentListResponse getApplicationDocuments(Long appId) {
		CreatorApplicationENT app = applicationRepo.findByIdWithMember(appId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "신청 정보를 찾을 수 없습니다."));

		Member member = app.getMember();
		CreatorType creatorType = app.getCreatorType();

		List<CreatorApplicationDocENT> docs = docRepo.findAllByApplication_AppId(appId);
		List<AdminCreatorDT.DocumentItem> documentItems = docs.stream()
			.map(this::toDocumentItem)
			.collect(Collectors.toList());

		Set<DocType> requiredDocs = getRequiredDocs(creatorType);

		List<String> requiredDocNames = requiredDocs.stream()
			.map(Enum::name)
			.toList();

		return AdminCreatorDT.DocumentListResponse.builder()
			.applicationId(appId)
			.memberName(member.getName())
			.creatorType(creatorType.name())
			.documents(documentItems)
			.requiredDocs(requiredDocNames)
			.build();
	}

	private AdminCreatorDT.DocumentItem toDocumentItem(CreatorApplicationDocENT doc) {
		AttachmentENT attachment = doc.getAttachment();
		DocType docType = doc.getDocType();

		return AdminCreatorDT.DocumentItem.builder()
			.docId(doc.getDocId())
			.docTypeDescription(docType.getDescription())
			.attachmentId(attachment.getId())
			.fileUrl(attachment.getFileUrl())
			.originalFilename(attachment.getOriginalFilename())
			.extension(attachment.getExtension())
			.build();
	}

	// ========== 개별 서류 조회 ==========
	@Override
	public AdminCreatorDT.SingleDocumentResponse getSingleDocument(Long appId, String docType) {

		if (!applicationRepo.existsById(appId)) {
			throw new BusinessException(ErrorCode.NOT_FOUND,"신청 정보를 찾을 수 없습니다.");
		}

		DocType type;
		try {
			type = DocType.valueOf(docType.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException(ErrorCode.BAD_REQUEST, "잘못된 서류입니다.");
		}

		// 해당 서류 조회
		CreatorApplicationDocENT doc = docRepo.findByApplication_AppIdAndDocType(appId, type)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "서류를 찾을 수 없습니다."));

		AttachmentENT attachment = doc.getAttachment();

		return AdminCreatorDT.SingleDocumentResponse.builder()
			.applicationId(appId)
			.docType(type.name())
			.docTypeDescription(type.getDescription())
			.fileUrl(attachment.getFileUrl())
			.originalFilename(attachment.getOriginalFilename())
			.extension(attachment.getExtension())
			.build();
	}

	// ========== 승인/반려 처리 ==========
	@Override
	@Transactional
	public AdminCreatorDT.ReviewResponse reviewApplication(
		Long adminId,
		AdminCreatorDT.ReviewRequest request
	) {
		Member admin = memberRepo.findById(adminId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "관리자 정보를 찾을 수 없습니다."));
		if (admin.getRole() != Role.ADMIN) {
			throw new BusinessException(ErrorCode.FORBIDDEN, "관리자 권한이 필요합니다.");
		}

		Long appId = request.getApplicationId();
		CreatorApplicationENT app = applicationRepo.findByIdWithMember(appId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "신청 정보를 찾을 수 없습니다."));

		ApplicationStatus previousStatus = app.getStatus();
		if (previousStatus != ApplicationStatus.PENDING) {
			throw new BusinessException(ErrorCode.BAD_REQUEST, "대기중(PENDING) 상태의 신청만 처리할 수 있습니다.");
		}

		ApplicationStatus newStatus = app.getStatus();
		try {
			newStatus = ApplicationStatus.valueOf(request.getStatus().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException(ErrorCode.BAD_REQUEST, "잘못된 상태값입니다.(APPROVED/REJECTED)");
		}

		// 상태 업데이트
		app.updateStatus(newStatus);
		// 승인 시 회원 Role 변경 (MAKER -> CREATOR)
		if (newStatus == ApplicationStatus.APPROVED) {
			Member applicant = app.getMember();
			upgradeToCreator(applicant);
			log.info("[Admin] 회원 '{}' 크리에이터 전환 완료", applicant.getName());
		}
		// 심사 이력 저장
		CreatorApplicationAuditENT audit = CreatorApplicationAuditENT.create(
			app, admin, previousStatus, newStatus
		);
		auditRepo.save(audit);
		log.info("[Admin] 신청 {} 처리 완료: {} -> {} (관리자: {})", appId, previousStatus, newStatus, adminId);

		return AdminCreatorDT.ReviewResponse.builder()
			.applicationId(appId)
			.memberId(app.getMember().getId())
			.status(newStatus.name())
			.statusDescription(getStatusDescription(newStatus))
			.processedAt(LocalDateTime.now())
			.adminId(adminId)
			.build();
	}

	private void upgradeToCreator(Member member) {
		member.upgradeToCreator();
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
