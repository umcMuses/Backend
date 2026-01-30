package org.muses.backendbulidtest251228.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorDashboardResDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorSummaryResDT;
import org.muses.backendbulidtest251228.domain.mypage.repository.MyPageMemberREP;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.orderItem.repository.OrderItemREP;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectLikeRepo;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreatorCenterAnalyticsSRVI implements CreatorCenterAnalyticsSRV {

    private final MyPageMemberREP myPageMemberREP;
    private final ProjectRepo projectRepo;
    private final ProjectLikeRepo projectLikeRepo;
    private final OrderREP orderREP;
    private final OrderItemREP orderItemREP;
    private final RewardRepo rewardRepo;

    @Override
    public CreatorSummaryResDT getMySummary(UserDetails userDetails) {
        Member me = getMe(userDetails);

        List<ProjectENT> myProjects = projectRepo.findByMemberId(me.getId());

        BigDecimal total = BigDecimal.ZERO;
        long ongoing = 0;

        for (ProjectENT p : myProjects) {
            BigDecimal add = orderREP.sumPaidAmountByProject(p.getId());
            total = total.add(add == null ? BigDecimal.ZERO : add);

            if (p.getFundingStatus() != null && "FUNDING".equals(p.getFundingStatus().name())) {
                ongoing++;
            }
        }

        return CreatorSummaryResDT.builder()
                .totalFunding(total)
                .ongoingProjectCount(ongoing)
                .build();
    }

    @Override
    public CreatorDashboardResDT getProjectDashboard(UserDetails userDetails, Long projectId) {
        Member me = getMe(userDetails);

        ProjectENT project = projectRepo.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!Objects.equals(project.getMember().getId(), me.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        BigDecimal totalFunding = Optional.ofNullable(orderREP.sumPaidAmountByProject(projectId))
                .orElse(BigDecimal.ZERO);

        long participantCount = orderREP.countDistinctPaidMembersByProject(projectId);

        long likeCount = projectLikeRepo.countByProjectId(projectId);

        Long dDay = (project.getDeadline() == null)
                ? null
                : Duration.between(LocalDateTime.now(), project.getDeadline()).toDays();

        // reward 판매 집계
        List<Object[]> rows = orderItemREP.aggregateRewardSales(projectId);

        List<CreatorDashboardResDT.RewardSales> rewardSales = new ArrayList<>();
        for (Object[] r : rows) {
            Long rewardId = (Long) r[0];
            long soldQty = ((Number) r[1]).longValue();
            BigDecimal revenue = (BigDecimal) r[2];

            String rewardName = rewardRepo.findById(rewardId)
                    .map(rew -> rew.getRewardName())
                    .orElse("REWARD-" + rewardId);

            rewardSales.add(CreatorDashboardResDT.RewardSales.builder()
                    .rewardId(rewardId)
                    .rewardName(rewardName)
                    .soldQuantity(soldQty)
                    .revenue(revenue)
                    .build());
        }

        // PAID 참여자 기준 성별/연령대 계산
        List<Long> paidMemberIds = orderREP.findDistinctPaidMemberIdsByProject(projectId);
        List<Member> paidMembers = paidMemberIds.isEmpty()
                ? List.of()
                : myPageMemberREP.findAllByIdIn(paidMemberIds);

        Map<String, Integer> genderRatio = calcGenderRatio(paidMembers);
        Map<String, Integer> ageRatio = calcAgeRatio(paidMembers);

        return CreatorDashboardResDT.builder()
                .totalFunding(totalFunding)
                .participantCount(participantCount)
                .likeCount(likeCount)
                .dDay(dDay)
                .rewardSales(rewardSales)
                .genderRatio(genderRatio)
                .ageRatio(ageRatio)
                .build();
    }

    private Member getMe(UserDetails userDetails) {
        if (userDetails == null) throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        return myPageMemberREP.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID));
    }

    // 성별/연령대 계산 유틸

    private Map<String, Integer> calcGenderRatio(List<Member> members) {
        int total = members.size();
        if (total == 0) return Map.of("male", 0, "female", 0);

        // ERD 기준: gender INT (0=남, 1=여)
        long male = members.stream().filter(m -> m.getGender() != null && m.getGender() == 0).count();
        long female = members.stream().filter(m -> m.getGender() != null && m.getGender() == 1).count();

        int malePct = (int) Math.round(male * 100.0 / total);
        int femalePct = 100 - malePct;

        return Map.of("male", malePct, "female", femalePct);
    }

    private Map<String, Integer> calcAgeRatio(List<Member> members) {

        long s20 = members.stream().filter(m -> isAgeBetween(m, 20, 29)).count();
        long s30 = members.stream().filter(m -> isAgeBetween(m, 30, 39)).count();
        long s40 = members.stream().filter(m -> isAgeBetween(m, 40, 49)).count();
        long s50 = members.stream().filter(m -> isAgeAtLeast(m, 50)).count();
        long known = s20 + s30 + s40 + s50;
        if (known == 0) return Map.of("20s", 0, "30s", 0, "40s", 0, "50s+", 0);

        int p20 = (int) Math.round(s20 * 100.0 / known);
        int p30 = (int) Math.round(s30 * 100.0 / known);
        int p40 = (int) Math.round(s40 * 100.0 / known);
        int p50 = Math.max(0, 100 - (p20 + p30 + p40));

        return Map.of("20s", p20, "30s", p30, "40s", p40, "50s+", p50);
    }

    private boolean isAgeBetween(Member m, int min, int max) {
        Integer age = getAge(m);
        return age != null && age >= min && age <= max;
    }

    private boolean isAgeAtLeast(Member m, int min) {
        Integer age = getAge(m);
        return age != null && age >= min;
    }

    private Integer getAge(Member m) {
        if (m.getBirthday() == null) return null;
        try {
            LocalDate birth = LocalDate.parse(m.getBirthday());
            return Period.between(birth, LocalDate.now()).getYears();
        } catch (Exception e) {
            return null;
        }
    }
}
