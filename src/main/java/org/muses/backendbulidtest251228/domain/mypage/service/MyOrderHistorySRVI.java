package org.muses.backendbulidtest251228.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.billingAuth.entity.BillingAuthENT;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.mypage.dto.MyOrderHistoryResDT;
import org.muses.backendbulidtest251228.domain.order.entity.OrderENT;
import org.muses.backendbulidtest251228.domain.order.repository.OrderREP;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.payment.entity.PaymentENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectContentENT;
import org.muses.backendbulidtest251228.domain.project.entity.ProjectENT;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.repository.ProjectContentRepo;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyOrderHistorySRVI implements MyOrderHistorySRV {

    private final MemberRepo memberRepo;
    private final OrderREP orderREP;

    private final ProjectContentRepo projectContentRepo;
    private final RewardRepo rewardRepo;

    @Override
    public MyOrderHistoryResDT.OrderHistoryListResponse getMyOrders(UserDetails userDetails) {
        Member member = resolveMember(userDetails);

        List<OrderENT> orders = orderREP.findMyOrdersWithProjectAndPayment(member.getId());

        List<MyOrderHistoryResDT.OrderHistoryItem> items = orders.stream()
                .map(o -> {
                    ProjectENT p = o.getProject();
                    PaymentENT pay = o.getPayment();

                    LocalDateTime displayDate = (pay != null && pay.getApprovedAt() != null)
                            ? pay.getApprovedAt()
                            : o.getCreatedAt();

                    return MyOrderHistoryResDT.OrderHistoryItem.builder()
                            .orderId(o.getId())
                            .projectTitle(p != null ? p.getTitle() : null)
                            .orderStatus(o.getStatus())
                            .paymentStatus(pay != null ? pay.getStatus() : null)
                            .amount(o.getTotalAmount())
                            .displayDate(displayDate)
                            .build();
                })
                .collect(Collectors.toList());

        return MyOrderHistoryResDT.OrderHistoryListResponse.builder()
                .items(items)
                .build();
    }

    @Override
    public MyOrderHistoryResDT.OrderHistoryDetailResponse getMyOrderDetail(UserDetails userDetails, Long orderId) {
        Member member = resolveMember(userDetails);

        OrderENT order = orderREP.findMyOrderDetailForMe(member.getId(), orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "주문(후원) 내역을 찾을 수 없습니다.", Map.of("orderId", orderId)));

        ProjectENT project = order.getProject();
        PaymentENT payment = order.getPayment();
        BillingAuthENT billingAuth = order.getBillingAuth();

        // 장소 상세: project_contents 테이블(키가 project_id라고 가정 > findById(projectId))
        String locationDetail = null;
        if (project != null) {
            Optional<ProjectContentENT> contentOpt = projectContentRepo.findById(project.getId());
            locationDetail = contentOpt.map(this::safeLocationDetail).orElse(null);
        }

        // 옵션: 주문아이템 1개만 보여주는 UI
        OrderItemENT firstItem = (order.getOrderItems() == null || order.getOrderItems().isEmpty())
                ? null
                : order.getOrderItems().get(0);

        String optionTitle = null;
        String optionDesc = null;
        Integer quantity = null;

        if (firstItem != null) {
            quantity = firstItem.getQuantity();

            Long rewardId = firstItem.getRewardId();
            if (rewardId != null) {
                RewardENT reward = rewardRepo.findById(rewardId).orElse(null);
                optionTitle = extractRewardTitle(reward);
                optionDesc = extractRewardDescription(reward);
            }
        }

        String providerText = null;
        if (billingAuth != null && billingAuth.getProvider() != null) {
            // 프론트 표시용: TOSS > "토스페이먼츠"처럼 매핑하고 싶으면 여기서 바꾸면 됨.
            providerText = billingAuth.getProvider().name();
        }

        LocalDateTime paidAt = payment != null ? payment.getApprovedAt() : null;

        return MyOrderHistoryResDT.OrderHistoryDetailResponse.builder()
                .orderId(order.getId())

                .projectTitle(project != null ? project.getTitle() : null)
                .opening(project != null ? safeOpening(project) : null)
                .locationDetail(locationDetail)

                .optionTitle(optionTitle)
                .optionDescription(optionDesc)
                .quantity(quantity)

                .paidAt(paidAt)
                .paymentProvider(providerText)
                .amount(order.getTotalAmount())

                .orderStatus(order.getStatus())
                .paymentStatus(payment != null ? payment.getStatus() : null)
                .build();
    }

    // 내부

    private Member resolveMember(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null || userDetails.getUsername().isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        }
        return memberRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID, "유효하지 않은 인증 정보입니다."));
    }

    private LocalDateTime safeOpening(ProjectENT project) {
        // getter 명이 다르면 여기만 수정하면 됨.
        return project.getOpening();
    }

    private String safeLocationDetail(ProjectContentENT content) {
        // project_contents.location_detail
        return content.getLocationDetail();
    }

    private String extractRewardTitle(RewardENT r) {
        if (r == null) return null;
        try { return (String) r.getClass().getMethod("getTitle").invoke(r); } catch (Exception ignored) {}
        try { return (String) r.getClass().getMethod("getName").invoke(r); } catch (Exception ignored) {}
        try { return (String) r.getClass().getMethod("getRewardName").invoke(r); } catch (Exception ignored) {}
        return null;
    }

    private String extractRewardDescription(RewardENT r) {
        if (r == null) return null;
        try { return (String) r.getClass().getMethod("getDescription").invoke(r); } catch (Exception ignored) {}
        try { return (String) r.getClass().getMethod("getRewardDescription").invoke(r); } catch (Exception ignored) {}
        return null;
    }
}
