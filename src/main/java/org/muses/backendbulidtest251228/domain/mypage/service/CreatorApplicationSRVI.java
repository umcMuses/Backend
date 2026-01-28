package org.muses.backendbulidtest251228.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorApplyReqDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorApplyResDT;
import org.muses.backendbulidtest251228.domain.mypage.entity.*;
import org.muses.backendbulidtest251228.domain.mypage.enums.ApplicationStatus;
import org.muses.backendbulidtest251228.domain.mypage.enums.CreatorType;
import org.muses.backendbulidtest251228.domain.mypage.repository.CreatorApplicationREP;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CreatorApplicationSRVI implements CreatorApplicationSRV {

    private final CreatorApplicationREP repo;
    private final MemberRepo memberRepo;

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

    private Member resolveMember(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null || userDetails.getUsername().isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        }
        return memberRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID, "유효하지 않은 인증 정보입니다."));
    }
}
