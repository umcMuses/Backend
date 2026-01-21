package org.muses.backendbulidtest251228.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.enums.RewardType;

import java.math.BigDecimal;

@Schema(description = "리워드 응답 DTO")
@Getter
@Builder
@AllArgsConstructor
public class RewardResponseDTO {

    @Schema(description = "리워드 ID")
    private Long rewardId;

    @Schema(description = "리워드 이름")
    private String rewardName;

    @Schema(description = "가격")
    private BigDecimal price;

    @Schema(description = "리워드 설명")
    private String description;

    @Schema(description = "총 수량")
    private Integer totalQuantity;

    @Schema(description = "판매된 수량")
    private Integer soldQuantity;

    @Schema(description = "남은 수량")
    private Integer remainingQuantity;

    @Schema(description = "QR 발급 여부")
    private RewardType type;

    // Entity -> DTO 변환
    public static RewardResponseDTO from(RewardENT entity) {
        return RewardResponseDTO.builder()
                .rewardId(entity.getId())
                .rewardName(entity.getRewardName())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .totalQuantity(entity.getTotalQuantity())
                .soldQuantity(entity.getSoldQuantity())
                .remainingQuantity(entity.getRemainingQuantity())
                .type(entity.getType())
                .build();
    }
}
