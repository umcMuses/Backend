package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.muses.backendbulidtest251228.domain.project.enums.RewardType;

import java.math.BigDecimal;

@Schema(description = "리워드 생성 요청 DTO (MUS-017)")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RewardRequestDTO {

    @Schema(description = "리워드 이름", example = "VIP석")
    private String rewardName;

    @Schema(description = "가격", example = "50000")
    private BigDecimal price;

    @Schema(description = "리워드 설명", example = "최고의 좌석에서 공연을 감상하세요")
    private String description;

    @Schema(description = "제한 수량 (null이면 무제한)", example = "100")
    private Integer totalQuantity;

    @Schema(description = "QR 발급 여부", example = "TICKET")
    private RewardType type;
}
