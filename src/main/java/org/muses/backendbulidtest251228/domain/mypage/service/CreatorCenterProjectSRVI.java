package org.muses.backendbulidtest251228.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectResDT;
import org.muses.backendbulidtest251228.domain.mypage.dto.CreatorCenterProjectReqDT;
import org.muses.backendbulidtest251228.domain.mypage.enums.QrStatus;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectTagENT;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.enums.FundingStatus;
import org.muses.backendbulidtest251228.domain.project.enums.RewardType;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectRepo;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectTagRepo;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.muses.backendbulidtest251228.domain.ticket.enums.TicketStatus;
import org.muses.backendbulidtest251228.domain.ticket.repository.TicketRepo;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
// import java.util.stream.Collectors;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreatorCenterProjectSRVI implements CreatorCenterProjectSRV {

    private final MemberRepo memberRepo;
    private final ProjectRepo projectRepo;
    private final ProjectTagRepo projectTagRepo;

    private final OrderREP orderREP;
    private final RewardRepo rewardRepo;

    private final TicketRepo ticketRepo;

    @Override
    public CreatorCenterProjectResDT.MyProjectListResponse getMyProjects(UserDetails userDetails) {
        Member me = resolveMember(userDetails);

        // 레포에 OrderBy 메서드 없을 수 있으니, 일단 가져와서 자바에서 정렬
        List<ProjectENT> projects = projectRepo.findByMemberId(me.getId()).stream()
                .sorted(Comparator.comparing(ProjectENT::getCreatedAt).reversed())
                .toList();

        List<CreatorCenterProjectResDT.MyProjectItem> items = projects.stream()
                .map(p -> {
                    int dDay = calcDDay(p.getDeadline());

                    // getTags() 같은 거 쓰지 말고 레포로 태그 조회
                    List<String> tags = projectTagRepo.findByProjectId(p.getId()).stream()
                            .map(ProjectTagENT::getTagName)
                            .filter(Objects::nonNull)
                            .toList();

                    Integer achieveRate = p.getAchieveRate();
                    BigDecimal raisedAmount = null;

                    if (p.getTargetAmount() != null && achieveRate != null) {
                        raisedAmount = p.getTargetAmount()
                                .multiply(BigDecimal.valueOf(achieveRate))
                                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                    }

                    return CreatorCenterProjectResDT.MyProjectItem.builder()
                            .projectId(p.getId())
                            .title(p.getTitle())
                            .fundingStatus(p.getFundingStatus())
                            .dDay(dDay)
                            .achieveRate(achieveRate)
                            .raisedAmount(raisedAmount)
                            .tags(tags)
                            .build();
                })
                .toList();

        return CreatorCenterProjectResDT.MyProjectListResponse.builder()
                .items(items)
                .build();
    }

    @Override
    public CreatorCenterProjectResDT.ProjectSettingsResponse getProjectSettings(UserDetails userDetails, Long projectId) {
        Member me = resolveMember(userDetails);

        ProjectENT project = projectRepo.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "프로젝트를 찾을 수 없습니다.", Map.of("projectId", projectId)));

        // 내 프로젝트 검증
        if (!Objects.equals(project.getMember().getId(), me.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "내 프로젝트만 조회할 수 있습니다.", Map.of("projectId", projectId));
        }

        List<String> tags = projectTagRepo.findByProjectId(projectId).stream()
                .map(ProjectTagENT::getTagName)
                .filter(Objects::nonNull)
                .toList();

        return CreatorCenterProjectResDT.ProjectSettingsResponse.builder()
                .projectId(project.getId())
                .description(project.getDescription())
                .tags(tags)
                .targetAmount(project.getTargetAmount())
                .deadline(project.getDeadline())
                .build();
    }

    @Override
    @Transactional
    public CreatorCenterProjectResDT.ProjectSettingsResponse updateProjectSettings(
            UserDetails userDetails,
            Long projectId,
            CreatorCenterProjectReqDT.UpdateProjectSettingsRequest request
    ) {
        Member me = resolveMember(userDetails);

        ProjectENT project = projectRepo.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "프로젝트를 찾을 수 없습니다.", Map.of("projectId", projectId)));

        if (!Objects.equals(project.getMember().getId(), me.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "내 프로젝트만 수정할 수 있습니다.", Map.of("projectId", projectId));
        }

        // description 업데이트
        if (request.getDescription() != null) {
            // project.updateDescription(request.getDescription());

            // 리플렉션으로 필드 세팅
            setFieldIfExists(project, "description", request.getDescription());
        }

        // tags 업데이트
        if (request.getTags() != null) {
            projectTagRepo.deleteByProjectId(projectId);

            List<ProjectTagENT> newTags = request.getTags().stream()
                    .filter(t -> t != null && !t.isBlank())
                    .distinct()
                    .map(tag -> ProjectTagENT.builder()
                            .project(project)
                            .tagName(tag)
                            .build())
                    .toList();

            projectTagRepo.saveAll(newTags);
        }

        projectRepo.save(project);

        return getProjectSettings(userDetails, projectId);
    }

    @Override
    public CreatorCenterProjectResDT.MakerListResponse getProjectMakers(UserDetails userDetails, Long projectId) {
        Member me = resolveMember(userDetails);

        ProjectENT project = projectRepo.findById(projectId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "프로젝트를 찾을 수 없습니다.", Map.of("projectId", projectId)));

        if (!Objects.equals(project.getMember().getId(), me.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "내 프로젝트만 조회할 수 있습니다.", Map.of("projectId", projectId));
        }

        // 이 메서드가 OrderREP에 없으면 레포에 JPQL로 추가
        List<OrderENT> orders = orderREP.findPaidOrdersWithItemsByProjectId(projectId);

        List<CreatorCenterProjectResDT.MakerRow> rows = new ArrayList<>();

        for (OrderENT o : orders) {
            Member maker = o.getMember();
            Long memberId = (maker != null) ? maker.getId() : null;

            String nickname = (maker != null) ? maker.getNickName() : null;
            String name     = (maker != null) ? maker.getName() : null;
            String phone    = (maker != null) ? maker.getPhoneNumber() : null;
            String email    = (maker != null) ? maker.getEmail() : null;

            List<OrderItemENT> items = (o.getOrderItems() == null) ? List.of() : o.getOrderItems();

            int totalQty = 0;

            java.util.Set<String> rewardNames = new java.util.LinkedHashSet<>();

            /*for (OrderItemENT it : items) {
                Long rewardId = it.getRewardId();
                RewardENT reward = (rewardId == null) ? null : rewardRepo.findById(rewardId).orElse(null);
                String rewardName = (reward == null) ? null : reward.getRewardName();

                rows.add(CreatorCenterProjectResDT.MakerRow.builder()
                        .memberId(memberId)
                        .nickname(nickname)
                        .name(name)
                        .phone(phone)
                        .email(email)
                        .quantity(it.getQuantity())
                        .rewardName(rewardName)
                        .qrStatus("NONE")
                        .build());
            }*/

            for (OrderItemENT it : items) {
                if (it == null) continue;

                Integer q = it.getQuantity();
                if (q != null) totalQty += q;

                Long rewardId = it.getRewardId();
                if (rewardId != null) {
                    RewardENT reward = rewardRepo.findById(rewardId).orElse(null);
                    if (reward != null && reward.getRewardName() != null) {
                        rewardNames.add(reward.getRewardName());
                    }
                }
            }

            String rewardNameJoined = rewardNames.isEmpty()
                    ? null
                    : String.join(", ", rewardNames);

            QrStatus qrStatus = resolveQrStatus(project, items);

            rows.add(CreatorCenterProjectResDT.MakerRow.builder()
                    .memberId(memberId)
                    .orderId(o.getId())  // orderId 추가
                    .nickname(nickname)
                    .name(name)
                    .phone(phone)
                    .email(email)
                    .quantity(totalQty)
                    .rewardName(rewardNameJoined)
                    .qrStatus(qrStatus)
                    .build());
        }

        return CreatorCenterProjectResDT.MakerListResponse.builder()
                .projectId(projectId)
                .items(rows)
                .build();


    }

    // 내부

    private QrStatus resolveQrStatus(ProjectENT project, List<OrderItemENT> items) {

        // 1) QR 티켓 리워드가 있는지
        List<OrderItemENT> ticketItems = new ArrayList<>();

        for (OrderItemENT it : items) {
            if (it.getRewardId() == null) continue;
            RewardENT reward = rewardRepo.findById(it.getRewardId()).orElse(null);
            if (reward != null && reward.getType() == RewardType.TICKET) {
                ticketItems.add(it);
            }
        }

        // QR 티켓 자체가 없음
        if (ticketItems.isEmpty()) {
            return QrStatus.NONE;
        }

        // 2) 펀딩 성공이 아닌 경우 → 무조건 ACTIVE
        if (project.getFundingStatus() != FundingStatus.SUCCESS) {
            return QrStatus.ACTIVE;
        }

        // 3) 펀딩 성공인 경우 → 티켓 상태 확인
        boolean hasActiveTicket = false;

        for (OrderItemENT it : ticketItems) {
            List<TicketENT> tickets = ticketRepo.findAllByOrderItem(it);

            for (TicketENT t : tickets) {
                if (t.getStatus() == TicketStatus.UNUSED) {
                    hasActiveTicket = true;
                    break;
                }
            }
            if (hasActiveTicket) break;
        }

        return hasActiveTicket ? QrStatus.ACTIVE : QrStatus.INACTIVE;
    }

    private Member resolveMember(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null || userDetails.getUsername().isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        }
        return memberRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID, "유효하지 않은 인증 정보입니다."));
    }

    private int calcDDay(LocalDateTime deadline) {
        if (deadline == null) return 0;
        LocalDate today = LocalDate.now();
        return (int) ChronoUnit.DAYS.between(today, deadline.toLocalDate());
    }

    private void setFieldIfExists(Object obj, String fieldName, Object value) {
        try {
            var f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception ignored) {}
    }

    private String extractString(Object obj, String methodName) {
        if (obj == null) return null;
        try {
            Method m = obj.getClass().getMethod(methodName);
            Object v = m.invoke(obj);
            return (v == null) ? null : String.valueOf(v);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Long extractLong(Object obj, String methodName) {
        if (obj == null) return null;
        try {
            Method m = obj.getClass().getMethod(methodName);
            Object v = m.invoke(obj);
            if (v instanceof Long l) return l;
            if (v instanceof Number n) return n.longValue();
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }
}

