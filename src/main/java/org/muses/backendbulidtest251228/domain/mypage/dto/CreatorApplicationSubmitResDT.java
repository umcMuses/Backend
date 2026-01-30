package org.muses.backendbulidtest251228.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CreatorApplicationSubmitResDT {

    @Schema(description = "신청 ID", example = "12")
    private Long applicationId;

    @Schema(description = "신청 상태(PENDING/APPROVED/REJECTED 등)", example = "PENDING")
    private String status;

    @Schema(
            description = """
                제출(검증) 통과 여부.
                - true: 해당 creatorType에 필요한 필수 서류가 모두 업로드됨
                - false: 누락 서류 존재
                """,
            example = "false"
    )

    private boolean submitted;      // 검증 통과 여부

    @Schema(description = "이 creatorType에서 요구하는 필수 서류 목록", example = "[\"ID_CARD\",\"BANKBOOK\"]")
    private List<String> required;  // 요구 서류

    @Schema(description = "현재 업로드된 서류 목록", example = "[\"ID_CARD\"]")
    private List<String> uploaded;  // 업로드된 서류

    @Schema(description = "누락된 서류 목록", example = "[\"BANKBOOK\"]")
    private List<String> missing;   // 누락 서류
}
