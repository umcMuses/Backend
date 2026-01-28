package org.muses.backendbulidtest251228.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 생성 요청 DTO")
public class OrderCreateReqDT {

    @Schema(
            description = "주문 대상 프로젝트 ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "프로젝트 ID는 필수입니다.")
    @Positive(message = "프로젝트 ID는 1 이상이어야 합니다.")
    private Long projectId;

    @Schema(
            description = "주문 항목 리스트 (리워드 단위)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @NotEmpty(message = "주문 항목은 최소 1개 필요합니다.")
    @Valid
    private List<OrderItemReqDT> items;
}
