package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
public class CreatorApplyResDT {

    @Schema(description = "신청 ID", example = "12")
    private Long applicationId;

    @Schema(description = "신청한 크리에이터 유형", example = "INDIVIDUAL")
    private String creatorType;

    @Schema(description = "신청 상태", example = "PENDING")
    private String status;
}
