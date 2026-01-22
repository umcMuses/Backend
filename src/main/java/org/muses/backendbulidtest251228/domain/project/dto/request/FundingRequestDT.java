package org.muses.backendbulidtest251228.domain.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "2단계: 펀딩 설정 요청 DTO - 목표 금액, 기간 설정")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FundingRequestDT {

    @Schema(description = "목표 펀딩 금액 (원)", example = "5000000")
    private BigDecimal targetAmount;

    @Schema(description = "펀딩 시작(오픈) 일시 (ISO 8601 형식)", example = "2025-02-01T00:00:00")
    private LocalDateTime opening;

    @Schema(description = "펀딩 마감 일시 (ISO 8601 형식)", example = "2025-03-01T23:59:59")
    private LocalDateTime deadline;
}
