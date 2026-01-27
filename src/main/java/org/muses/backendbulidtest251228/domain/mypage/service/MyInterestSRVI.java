package org.muses.backendbulidtest251228.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.mypage.repository.MyPageMemberREP;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectCardResponseDT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectLikeENT;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectLikeRepo;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyInterestSRVI implements MyInterestSRV {

    private final MyPageMemberREP myPageMemberREP;
    private final ProjectLikeRepo projectLikeRepo;

    @Override
    public List<ProjectCardResponseDT> getLikedProjects(UserDetails userDetails, int page, int size) {
        Member me = getMe(userDetails);

        var likesPage = projectLikeRepo.findAllByMemberIdWithProject(
                me.getId(),
                PageRequest.of(page, size)
        );

        return likesPage.getContent().stream()
                .map(ProjectLikeENT::getProject)
                .map(this::toCard)
                .toList();
    }

    private Member getMe(UserDetails userDetails) {
        if (userDetails == null) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED); // 401
        }

        return myPageMemberREP.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID)); // 401
    }

    private ProjectCardResponseDT toCard(ProjectENT p) {
        LocalDateTime now = LocalDateTime.now();
        Long dDay = (p.getDeadline() == null) ? null : Duration.between(now, p.getDeadline()).toDays();

        return ProjectCardResponseDT.builder()
                .projectId(p.getId())
                .thumbnailUrl(p.getThumbnailUrl())
                .title(p.getTitle())
                .achieveRate(p.getAchieveRate())
                .deadline(p.getDeadline())
                .dDay(dDay)
                .fundingStatus(p.getFundingStatus() == null ? null : p.getFundingStatus().name())
                .isScheduled(false) // 나중에 정확 매핑하면 됨
                .opening(p.getOpening())
                .build();
    }
}
