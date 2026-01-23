package org.muses.backendbulidtest251228.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateReqDT {

    @NotNull(message = "프로젝트 ID는 필수입니다.")
    private Long projectId;

    @NotNull(message = "리워드 ID는 필수입니다.")
    private Long rewardId;

    @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    private int quantity;

    @Min(value = 0, message = "금액은 0원 이상이어야 합니다.")
    private int unitPrice;
}
