package org.muses.backendbulidtest251228.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.mypage.dto.*;
import org.muses.backendbulidtest251228.domain.mypage.entity.CreatorApplicationDocENT;
import org.muses.backendbulidtest251228.domain.mypage.entity.CreatorApplicationENT;
import org.muses.backendbulidtest251228.domain.mypage.enums.*;
import org.muses.backendbulidtest251228.domain.mypage.repository.CreatorApplicationDocRepo;
import org.muses.backendbulidtest251228.domain.mypage.repository.CreatorApplicationREP;
import org.muses.backendbulidtest251228.domain.storage.entity.AttachmentENT;
import org.muses.backendbulidtest251228.domain.storage.service.AttachmentSRV;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CreatorApplicationSRVI implements CreatorApplicationSRV {

    private final CreatorApplicationREP repo;
    private final CreatorApplicationDocRepo docRepo;
    private final MemberRepo memberRepo;
    private final AttachmentSRV attachmentSRV;

    private static final String TARGET_TYPE = "creator_app";
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp", "pdf");

    @Override
    public CreatorApplyResDT apply(UserDetails userDetails, CreatorApplyReqDT req) {
        Member member = resolveMember(userDetails);

        if (repo.existsByMember_IdAndStatus(member.getId(), ApplicationStatus.PENDING)) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "이미 심사중(PENDING)인 크리에이터 전환 신청이 존재합니다.",
                    Map.of("memberId", member.getId())
            );
        }

        CreatorType type;
        try {
            type = CreatorType.valueOf(req.getCreatorType());
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "creatorType 값이 올바르지 않습니다. (INDIVIDUAL | SOLE_BIZ | CORP_BIZ)",
                    Map.of("creatorType", req.getCreatorType())
            );
        }

        CreatorApplicationENT app;
        try {
            app = repo.save(CreatorApplicationENT.create(member, type));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "이미 심사중(PENDING)인 크리에이터 전환 신청이 존재합니다.",
                    Map.of("memberId", member.getId())
            );
        }

        return CreatorApplyResDT.builder()
                .applicationId(app.getAppId())
                .creatorType(app.getCreatorType().name())
                .status(app.getStatus().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CreatorApplyResDT getMyApplication(UserDetails userDetails) {
        Member member = resolveMember(userDetails);

        CreatorApplicationENT app = repo.findTopByMember_IdOrderByCreatedAtDesc(member.getId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "크리에이터 전환 신청 내역이 없습니다.",
                        Map.of("memberId", member.getId())
                ));

        return CreatorApplyResDT.builder()
                .applicationId(app.getAppId())
                .creatorType(app.getCreatorType().name())
                .status(app.getStatus().name())
                .build();
    }

    // 서류 업로드/조회/제출

    @Override
    public CreatorApplicationDocResDT uploadDoc(UserDetails userDetails, String docType, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "파일이 필요합니다.");
        }
        validateExtension(file);

        CreatorApplicationENT app = resolveLatestApp(userDetails);

        DocType dt;
        try {
            dt = DocType.valueOf(docType);
        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "docType 값이 올바르지 않습니다.",
                    Map.of("docType", docType, "allowed", Arrays.toString(DocType.values()))
            );
        }

        // (선택) 타입별 허용 docType 제한
        Set<DocType> required = requiredDocs(app.getCreatorType());
        if (!required.contains(dt)) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "해당 creatorType에서 요구하지 않는 서류입니다.",
                    Map.of("creatorType", app.getCreatorType().name(), "docType", dt.name(), "required", required)
            );
        }

        // 기존 docType 있으면 attachment까지 삭제 후 교체
        docRepo.findByApplication_AppIdAndDocType(app.getAppId(), dt).ifPresent(existing -> {
            attachmentSRV.delete(existing.getAttachment().getId());
            docRepo.delete(existing);
        });

        AttachmentENT saved = attachmentSRV.upload(TARGET_TYPE, app.getAppId(), file);
        CreatorApplicationDocENT doc = docRepo.save(CreatorApplicationDocENT.of(app, dt, saved));

        return CreatorApplicationDocResDT.builder()
                .docId(doc.getDocId())
                .docType(doc.getDocType().name())
                .attachmentId(saved.getId())
                .fileUrl(saved.getFileUrl())
                .originalFilename(saved.getOriginalFilename())
                .extension(saved.getExtension())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreatorApplicationDocResDT> getMyDocs(UserDetails userDetails) {
        CreatorApplicationENT app = resolveLatestApp(userDetails);

        return docRepo.findAllByApplication_AppId(app.getAppId()).stream()
                .map(d -> CreatorApplicationDocResDT.builder()
                        .docId(d.getDocId())
                        .docType(d.getDocType().name())
                        .attachmentId(d.getAttachment().getId())
                        .fileUrl(d.getAttachment().getFileUrl())
                        .originalFilename(d.getAttachment().getOriginalFilename())
                        .extension(d.getAttachment().getExtension())
                        .build()
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CreatorApplicationSubmitResDT submit(UserDetails userDetails) {
        CreatorApplicationENT app = resolveLatestApp(userDetails);

        Set<DocType> required = requiredDocs(app.getCreatorType());
        List<CreatorApplicationDocENT> docs = docRepo.findAllByApplication_AppId(app.getAppId());

        Set<DocType> uploaded = docs.stream().map(CreatorApplicationDocENT::getDocType).collect(Collectors.toSet());
        List<String> missing = required.stream()
                .filter(r -> !uploaded.contains(r))
                .map(Enum::name)
                .toList();

        boolean ok = missing.isEmpty();
        if (!ok) {
            // 프론트가 “제출하기” 눌렀을 때 누락 알려주기
            return CreatorApplicationSubmitResDT.builder()
                    .applicationId(app.getAppId())
                    .status(app.getStatus().name())
                    .submitted(false)
                    .required(required.stream().map(Enum::name).toList())
                    .uploaded(uploaded.stream().map(Enum::name).toList())
                    .missing(missing)
                    .build();
        }

        // status는 이미 PENDING이라 별도 변경 없이 OK 응답만
        return CreatorApplicationSubmitResDT.builder()
                .applicationId(app.getAppId())
                .status(app.getStatus().name())
                .submitted(true)
                .required(required.stream().map(Enum::name).toList())
                .uploaded(uploaded.stream().map(Enum::name).toList())
                .missing(List.of())
                .build();
    }

    // 내부 유틸
    private void validateExtension(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original == null || !original.contains(".")) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "파일 확장자가 필요합니다.");
        }
        String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException(
                    ErrorCode.BAD_REQUEST,
                    "허용되지 않는 확장자입니다.",
                    Map.of("allowed", ALLOWED_EXT, "extension", ext)
            );
        }
    }

    private Set<DocType> requiredDocs(CreatorType type) {
        return switch (type) {
            case INDIVIDUAL -> Set.of(DocType.ID_CARD, DocType.BANKBOOK);
            case SOLE_BIZ -> Set.of(DocType.BRC, DocType.ID_CARD, DocType.BANKBOOK);
            case CORP_BIZ -> Set.of(DocType.BRC, DocType.BANKBOOK, DocType.COMP_SEAL, DocType.COMP_REGISTRY);
        };
    }

    private CreatorApplicationENT resolveLatestApp(UserDetails userDetails) {
        Member member = resolveMember(userDetails);
        return repo.findTopByMember_IdOrderByCreatedAtDesc(member.getId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.NOT_FOUND,
                        "크리에이터 전환 신청 내역이 없습니다. 먼저 신청부터 하세요.",
                        Map.of("memberId", member.getId())
                ));
    }

    private Member resolveMember(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null || userDetails.getUsername().isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        }
        return memberRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID, "유효하지 않은 인증 정보입니다."));
    }
}
