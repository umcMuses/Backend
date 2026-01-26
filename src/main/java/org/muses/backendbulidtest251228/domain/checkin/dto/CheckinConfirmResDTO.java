package org.muses.backendbulidtest251228.domain.checkin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CheckinConfirmResDTO {

    // USED_NOW | ALREADY_USED
    private String result;

    private String buyerName;
    private String buyerNickname;
    private String rewardTitle;
    private Integer quantity;

    private LocalDateTime usedAt;

    // 이번 스캔으로 정상 사용 처리된 경우
    public static CheckinConfirmResDTO usedNow(
            String buyerName,
            String buyerNickname,
            String rewardTitle,
            Integer quantity,
            LocalDateTime usedAt
    ) {
        return new CheckinConfirmResDTO(
                "USED_NOW",
                buyerName,
                buyerNickname,
                rewardTitle,
                quantity,
                usedAt
        );
    }

    // 이미 사용된 티켓인 경우
    public static CheckinConfirmResDTO alreadyUsed(LocalDateTime usedAt) {
        return new CheckinConfirmResDTO(
                "ALREADY_USED",
                null,
                null,
                null,
                null,
                usedAt
        );
    }
}
