package org.muses.backendbulidtest251228.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 항목(리워드) 요청 DTO")
public class OrderItemReqDT {

    @Schema(
            description = "리워드 ID",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private Long rewardId;

    @Schema(
            description = "주문 수량",
            example = "2",
            minimum = "1"
    )
    @Min(1)
    private int quantity;

    @Schema(
            description = "리워드 단가 (프론트 계산값, 검증용)",
            example = "55000",
            minimum = "0"
    )
    @NotNull
    @Min(value = 0, message = "금액은 0원 이상이어야 합니다.")
    private int unitPrice;
}
