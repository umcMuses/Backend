package org.muses.backendbulidtest251228.domain.checkin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(
        name = "CheckinView",
        description = "체크인 화면에 표시할 예매/리워드 정보"
)
public class CheckinViewDTO {

    @Schema(
            description = "프로젝트 ID",
            example = "1"
    )
    private Long projectId;

    @Schema(
            description = "구매자 실명",
            example = "홍길동"
    )
    private String buyerName;

    @Schema(
            description = "구매자 닉네임",
            example = "푸른 고양이"
    )
    private String buyerNickname;

    @Schema(
            description = "리워드/티켓 이름",
            example = "VIP 티켓"
    )
    private String rewardTitle;

    @Schema(
            description = "수량",
            example = "2"
    )
    private Integer quantity;
}
