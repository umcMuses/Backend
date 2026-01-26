package org.muses.backendbulidtest251228.domain.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(
        name = "CheckinConfirmResponse",
        description = "체크인 처리 결과 응답"
)
public class CheckinConfirmResDTO {

    @Schema(
            description = "체크인 결과 상태",
            example = "USED_NOW",
            allowableValues = {"USED_NOW", "ALREADY_USED"}
    )
    private String result;

    @Schema(
            description = "구매자 실명 (이미 사용된 티켓인 경우 null)",
            example = "홍길동",
            nullable = true
    )
    private String buyerName;

    @Schema(
            description = "구매자 닉네임 (이미 사용된 티켓인 경우 null)",
            example = "길동이",
            nullable = true
    )
    private String buyerNickname;

    @Schema(
            description = "리워드/티켓 이름 (이미 사용된 티켓인 경우 null)",
            example = "VIP 티켓",
            nullable = true
    )
    private String rewardTitle;

    @Schema(
            description = "수량 (이미 사용된 티켓인 경우 null)",
            example = "2",
            nullable = true
    )
    private Integer quantity;

    @Schema(
            description = "티켓 사용 시각",
            example = "2026-01-27T14:30:00"
    )
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
