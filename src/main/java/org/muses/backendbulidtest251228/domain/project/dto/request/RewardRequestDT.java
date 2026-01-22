package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.muses.backendbulidtest251228.domain.project.enums.RewardType;

import java.math.BigDecimal;

@Schema(description = "리워드(후원 상품) 생성 요청 DTO - 개별 리워드 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RewardRequestDT {

    @Schema(description = "리워드 이름 (예: VIP석, 일반석, 굿즈 패키지 등)", example = "VIP석")
    private String rewardName;

    @Schema(description = "리워드 가격 (원)", example = "50000")
    private BigDecimal price;

    @Schema(description = "리워드 상세 설명 (혜택, 구성품 등)", example = "최고의 좌석에서 공연을 감상하실 수 있습니다. 포토카드 포함.")
    private String description;

    @Schema(description = "판매 제한 수량 (null 또는 0이면 무제한)", example = "100")
    private Integer totalQuantity;

    @Schema(
            description = "리워드 타입 (TICKET: QR 티켓 발급 - 공연/행사 입장용, NONE: 티켓 없음 - 굿즈/후원 등)",
            example = "TICKET",
            allowableValues = {"TICKET", "NONE"}
    )
    private RewardType type;
}
