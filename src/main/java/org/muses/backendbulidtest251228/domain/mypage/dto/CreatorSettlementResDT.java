package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "크리에이터 정산 응답")
public class CreatorSettlementResDT {

    @Schema(description = "총 모금액 (주문 취소, 결제 실패 상태 제외 합)", example = "1000000.00")
    private BigDecimal totalAmount;

    @Schema(description = "수수료 (7%)", example = "70000.00")
    private BigDecimal feeAmount;

    @Schema(description = "지급액 (총 모금액 - 수수료)", example = "930000.00")
    private BigDecimal payoutAmount;
}

