package org.muses.backendbulidtest251228.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class CreatorDashboardResDT {

    @Schema(description = "총 모금액(성공 결제 기준)")
    private BigDecimal totalFunding;

    @Schema(description = "참여 메이커 수(결제 성공한 distinct 회원 수)")
    private long participantCount;

    @Schema(description = "관심(좋아요) 수")
    private long likeCount;

    @Schema(description = "남은 기간(Day)")
    private Long dDay;

    @Schema(description = "리워드 판매 현황(리워드별 판매량/매출)")
    private List<RewardSales> rewardSales;

    @Schema(description = "성별 비율(%)")
    private Map<String, Integer> genderRatio;

    @Schema(description = "연령대 비율(%)")
    private Map<String, Integer> ageRatio;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RewardSales {
        private Long rewardId;
        private String rewardName;
        private long soldQuantity;
        private BigDecimal revenue;
    }
}
