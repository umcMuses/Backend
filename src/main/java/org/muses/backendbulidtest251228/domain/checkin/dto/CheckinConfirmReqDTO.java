package org.muses.backendbulidtest251228.domain.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(
        name = "CheckinConfirmRequest",
        description = "체크인 확정을 위한 요청 DTO"
)
public class CheckinConfirmReqDTO {

    @NotBlank
    @Schema(
            description = "QR에서 추출한 티켓 토큰",
            example = "TT_9f3a2c8b1e"
    )
    private String ticketToken;
}
