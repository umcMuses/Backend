package org.muses.backendbulidtest251228.domain.settlement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.muses.backendbulidtest251228.domain.settlement.enums.SettlementStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "정산 목록 조회 요청 DTO")
public class SettlementListReqDTO {


    @Schema(
            description = """
                    정산 상태 필터
                    - 미전송: 전체 조회
                    - WAITING: 대기중
                    - IN_PROGRESS: 처리중
                    - COMPLETED: 완료됨
                    """,
            example = "WAITING",
            allowableValues = {"WAITING", "IN_PROGRESS", "COMPLETED"}
    )
    private SettlementStatus status; // null이면 전체
}