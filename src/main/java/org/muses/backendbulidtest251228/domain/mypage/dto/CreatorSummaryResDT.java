package org.muses.backendbulidtest251228.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class CreatorSummaryResDT {

    @Schema(description = "총 후원금(성공 결제 기준)", example = "2840000")
    private BigDecimal totalFunding;

    @Schema(description = "진행중 프로젝트 수(FUNDING)", example = "1")
    private long ongoingProjectCount;
}
