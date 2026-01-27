package org.muses.backendbulidtest251228.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
    private Long projectId;

    @Schema(
            description = "주문 항목 리스트 (리워드 단위)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private List<OrderItemReqDT> items;
}
