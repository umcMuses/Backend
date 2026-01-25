package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MyOrderHistoryResDT {

    // 목록
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderHistoryItem {
        private Long orderId;
        private String projectTitle;

        // 배지/상태
        private OrderStatus orderStatus;
        private PaymentStatus paymentStatus;

        // 금액
        private BigDecimal amount;

        // 리스트 우측 날짜: 결제 승인일시 우선, 없으면 주문 생성일시
        private LocalDateTime displayDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderHistoryListResponse {
        private List<OrderHistoryItem> items;
    }

    // 상세
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderHistoryDetailResponse {
        private Long orderId;

        // 상단 카드
        private String projectTitle;          // 공연명(프로젝트명)
        private LocalDateTime opening;        // 일시 (projects.opening)
        private String locationDetail;        // 장소 상세 (project_contents.location_detail)

        private String optionTitle;           // 옵션명(리워드명)
        private String optionDescription;     // 옵션 설명(리워드 설명)
        private Integer quantity;             // 수량

        // 결제 영역
        private LocalDateTime paidAt;         // 결제일시 (payments.approved_at)
        private String paymentProvider;       // 결제수단(토스페이먼츠 등)
        private BigDecimal amount;            // 결제금액

        // 상태
        private OrderStatus orderStatus;
        private PaymentStatus paymentStatus;
    }
}
