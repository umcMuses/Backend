package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.muses.backendbulidtest251228.domain.project.enums.FundingType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "2단계: 펀딩 설정 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FundingRequestDTO {

    @Schema(description = "목표 금액", example = "1000000")
    private BigDecimal targetAmount;

    @Schema(description = "펀딩 시작일", example = "2025-02-01T00:00:00")
    private LocalDateTime opening;

    @Schema(description = "펀딩 마감일", example = "2025-03-01T23:59:59")
    private LocalDateTime deadline;

    @Schema(description = "펀딩 방식 (ALL_OR_NOTHING / KEEP_IT_ALL)", example = "ALL_OR_NOTHING")
    private FundingType fundingType;
}
