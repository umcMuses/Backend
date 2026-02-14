package org.muses.backendbulidtest251228.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 생성 결과 응답 DTO")
public class OrderCreateResDT {

    @Schema(
            description = "생성된 주문 ID",
            example = "1001"
    )
    private Long orderId;

    @Schema(
            description = "결제 사용자 식별 키 (billingKey와 연결됨)",
            example = "muses_550e8400-e29b-41d4-a716-446655440000"
    )
    private String customerKey;

    @Schema(
            description = "결제 성공 시 이동할 프론트엔드 URL",
            example = "https://umc-muses.netlify.app/billing/success"
    )
    private String successUrl;

    @Schema(
            description = "결제 실패 시 이동할 프론트엔드 URL",
            example = "https://umc-muses.netlify.app/billing/fail"
    )
    private String failUrl;
}
