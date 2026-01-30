package org.muses.backendbulidtest251228.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "내 티켓 조회 응답 DTO")
public class MyTicketResDT {

    @Schema(description = "티켓 ID", example = "15")
    private Long ticketId;

    @Schema(description = "프로젝트 제목", example = "푸른 오렌지 재즈 콘서트")
    private String projectTitle;

    @Schema(
            description = "공연/행사 시작 일시 (projects.opening)",
            example = "2026-02-10T18:00:00"
    )
    private LocalDateTime opening;

    @Schema(
            description = "구매한 옵션/리워드명",
            example = "VIP 티켓"
    )
    private String optionLabel;

    @Schema(
            description = "티켓 고유 토큰(QR 생성 및 입장 체크에 사용)",
            example = "TICKET-8f3a2c9b"
    )
    private String ticketToken;

    @Schema(
            description = """
                티켓 상태
                - UNUSED : 아직 사용되지 않음
                - USED   : 체크인 완료
                - EXPIRED: 사용 기한 만료
                """,
            example = "UNUSED"
    )
    private String status;
}
