package org.muses.backendbulidtest251228.domain.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.enums.RewardType;

import java.math.BigDecimal;

@Schema(description = "리워드 응답 DTO - 프로젝트의 후원 상품(리워드) 정보")
@Getter
@Builder
@AllArgsConstructor
public class RewardResponseDT {

    @Schema(description = "리워드 고유 ID", example = "1")
    private Long rewardId;

    @Schema(description = "리워드 이름 (VIP석, 일반석, 굿즈 패키지 등)", example = "VIP석")
    private String rewardName;

    @Schema(description = "리워드 가격 (원)", example = "50000")
    private BigDecimal price;

    @Schema(description = "리워드 상세 설명", example = "VIP 좌석에서 공연을 감상하실 수 있습니다.")
    private String description;

    @Schema(description = "총 판매 가능 수량 (null이면 무제한)", example = "100")
    private Integer totalQuantity;

    @Schema(description = "현재까지 판매된 수량", example = "45")
    private Integer soldQuantity;

    @Schema(description = "남은 수량 (totalQuantity - soldQuantity), 무제한이면 null", example = "55")
    private Integer remainingQuantity;

    @Schema(
            description = "리워드 타입 (TICKET: QR 티켓 발급, NONE: 티켓 없음/굿즈 등)",
            example = "TICKET",
            allowableValues = {"TICKET", "NONE"}
    )
    private RewardType type;

    /**
     * Entity -> DTO 변환 메서드
     */
    public static RewardResponseDT from(RewardENT entity) {
        return RewardResponseDT.builder()
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
