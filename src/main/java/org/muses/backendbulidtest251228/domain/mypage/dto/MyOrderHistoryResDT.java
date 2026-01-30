package org.muses.backendbulidtest251228.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.muses.backendbulidtest251228.domain.order.enums.OrderStatus;
import org.muses.backendbulidtest251228.domain.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MyOrderHistoryResDT {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "주문내역 목록 아이템")
    public static class OrderHistoryItem {

        @Schema(description = "주문 ID", example = "101")
        private Long orderId;

        @Schema(description = "프로젝트 제목", example = "푸른 오렌지 재즈 콘서트")
        private String projectTitle;

        @Schema(description = "주문 상태", example = "PAID")
        private OrderStatus orderStatus;

        @Schema(description = "결제 상태", example = "SUCCESS")
        private PaymentStatus paymentStatus;

        @Schema(description = "결제 금액", example = "59000")
        private BigDecimal amount;

        @Schema(
                description = "리스트에 표시할 날짜(결제 승인일시 우선, 없으면 주문 생성일시)",
                example = "2026-01-30T15:00:00"
        )
        private LocalDateTime displayDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "주문내역 목록 응답")
    public static class OrderHistoryListResponse {
        @Schema(description = "주문 리스트")
        private List<OrderHistoryItem> items;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "주문내역 상세 응답")
    public static class OrderHistoryDetailResponse {

        @Schema(description = "주문 ID", example = "101")
        private Long orderId;

        @Schema(description = "프로젝트명", example = "푸른 오렌지 재즈 콘서트")
        private String projectTitle;

        @Schema(description = "공연/행사 시작 일시(projects.opening)", example = "2026-02-10T18:00:00")
        private LocalDateTime opening;

        @Schema(description = "장소 상세(project_contents.location_detail)", example = "홍대입구역 2번 출구 인근")
        private String locationDetail;

        @Schema(description = "옵션/리워드명", example = "VIP 티켓")
        private String optionTitle;

        @Schema(description = "옵션/리워드 설명", example = "앞좌석 + 굿즈 제공")
        private String optionDescription;

        @Schema(description = "수량", example = "2")
        private Integer quantity;

        @Schema(description = "결제 승인 일시(payments.approved_at)", example = "2026-01-30T15:00:00")
        private LocalDateTime paidAt;

        @Schema(description = "결제수단/PG", example = "TOSS_PAYMENTS")
        private String paymentProvider;

        @Schema(description = "결제금액", example = "118000")
        private BigDecimal amount;

        @Schema(description = "주문 상태", example = "PAID")
        private OrderStatus orderStatus;

        @Schema(description = "결제 상태", example = "SUCCESS")
        private PaymentStatus paymentStatus;
    }
}
