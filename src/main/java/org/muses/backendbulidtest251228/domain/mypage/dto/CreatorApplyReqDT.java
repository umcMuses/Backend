package org.muses.backendbulidtest251228.domain.mypage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@NoArgsConstructor
public class CreatorApplyReqDT {
    @NotBlank(message = "creatorType은 필수입니다")
    @Schema(description = "크리에이터 유형 (INDIVIDUAL(개인) | SOLE_BIZ(개인사업자) | CORP_BIZ(법인사업자))",
            example = "INDIVIDUAL")
    private String creatorType;
}
