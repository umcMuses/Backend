package org.muses.backendbulidtest251228.domain.settlement.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.settlement.dto.SettlementListReqDTO;
import org.muses.backendbulidtest251228.domain.settlement.dto.SettlementListResDTO;
import org.muses.backendbulidtest251228.domain.settlement.service.SettlementSRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "정산 관리 (Admin)",
        description = "관리자 정산 조회 및 지급 처리 API"
)
@RestController
@RequestMapping("/api/admin/settlements")
@RequiredArgsConstructor
public class SettlementCTL {

    private final SettlementSRV settlementSRV;

    /**
     * 정산 목록 조회 (탭: 전체/대기중/처리중/완료됨)
     * - status 생략 or ALL => 전체
     */

    @Operation(
            summary = "정산 목록 조회",
            description = """
                    정산 목록을 조회합니다.
                    
                    - 전체 탭: status 파라미터를 보내지 않습니다.
                    - 대기중: status=WAITING
                    - 처리중: status=IN_PROGRESS
                    - 완료됨: status=COMPLETED
                    """
    )
    @GetMapping
    public ApiResponse<List<SettlementListResDTO>> list(SettlementListReqDTO req) {

        List<SettlementListResDTO> list = settlementSRV.list(req.getStatus());


        return ApiResponse.success(list);
    }


    @Operation(
            summary = "정산 지급 완료 처리",
            description = "settlementId에 해당하는 정산 상태를 COMPLETED(지급 완료)로 변경합니다."
    )
    @PostMapping("/payout")
    public ApiResponse<String> payout(
            @RequestParam("settlementId") Long id
    ) {
        settlementSRV.payout(id);


        return ApiResponse.success("OK");
    }


}
