package org.muses.backendbulidtest251228.domain.checkin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheckinConfirmReqDTO {

    //QR에서 추출한 티켓 토큰
    @NotNull
    private String ticketToken;
}
