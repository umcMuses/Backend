package org.muses.backendbulidtest251228.domain.settlement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.muses.backendbulidtest251228.domain.settlement.enums.SettlementStatus;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "정산 목록 조회 응답 DTO")
public class SettlementListResDTO {

    @Schema(description = "정산 ID", example = "1")
    private Long id;

    @Schema(description = "총 정산 금액", example = "100000.00")
    private BigDecimal totalAmount;

    @Schema(description = "수수료 금액", example = "10000.00")
    private BigDecimal feeAmount;

    @Schema(description = "지급 금액", example = "90000.00")
    private BigDecimal payoutAmount;

    @Schema(
            description = "정산 상태",
            example = "IN_PROGRESS",
            allowableValues = {"WAITING", "IN_PROGRESS", "COMPLETED"}
    )
    private SettlementStatus settlementStatus;

    @Schema(description = "프로젝트 제목", example = "뮤지컬 갈라 콘서트")
    private String title;
}
