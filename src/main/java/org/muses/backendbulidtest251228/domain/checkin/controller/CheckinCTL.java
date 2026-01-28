package org.muses.backendbulidtest251228.domain.checkin.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.checkin.dto.CheckinConfirmReqDTO;
import org.muses.backendbulidtest251228.domain.checkin.dto.CheckinConfirmResDTO;
import org.muses.backendbulidtest251228.domain.checkin.generator.QrGenerator;
import org.muses.backendbulidtest251228.domain.checkin.service.CheckinLinkSRV;
import org.muses.backendbulidtest251228.domain.checkin.service.CheckinSRV;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.muses.backendbulidtest251228.domain.ticket.repository.TicketRepo;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(
        name = "Check-in",
        description = "행사 체크인(스태프 전용) 및 티켓 QR 생성"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checkin")
public class CheckinCTL {

    private final CheckinSRV checkinService;

    private final CheckinLinkSRV linkService;
    private final QrGenerator qrGenerator;

    private final TicketRepo ticketRepo;

    @Value("${app.base-url}")
    private String baseUrl;

    // 관리자(스태프)가 체크인 페이지에서 QR 을 스캔할때 그 티켓을 USED로 바꾸고
    // 예매자/리워드 정보를 응답으로 돌려주는 API
    @Operation(
            summary = "체크인 확정",
            description = "스태프가 QR 스캔으로 전달한 ticketToken을 사용해 체크인을 처리합니다."
    )
    @PostMapping("/{token}/confirm")
    public ApiResponse<CheckinConfirmResDTO> confirm(
            @PathVariable String token, // 해당 프로젝트의 체크인 링크 토큰
            @RequestBody CheckinConfirmReqDTO req // 티켓의 토큰
    ) {
        CheckinConfirmResDTO confirm = checkinService.confirm(token, req.getTicketToken());

        return ApiResponse.success(confirm);
    }

    /**
     * 관리자용 체크인 전용 URL을 조회한다
     * 프로젝트당 하나의 체크인 링크만 존재한다
     */
    @Operation(
            summary = "프로젝트 체크인 링크 생성/조회",
            description = "프로젝트별 체크인 전용 URL을 생성하거나 기존 링크를 반환합니다."
    )
    @PostMapping("/projects/{projectId}/link")
    public ApiResponse<Map<String, Object>> createCheckinLink(
            @PathVariable Long projectId
    ) {

        String checkinUrl = linkService.createOrGetLink(projectId, baseUrl);

        return ApiResponse.success(Map.of("checkinUrl", checkinUrl));
    }


    /**
     * 관람객 티켓용 QR 생성
     * - QR 안에는 ticketToken 문자열만 들어간다
     * - 스태프 체크인 페이지에서 스캔해서 사용
     */
    @Operation(
            summary = "티켓 QR 이미지 생성",
            description = "관람객 티켓의 QR 이미지를 PNG로 반환합니다. QR에는 ticketToken만 포함됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "QR PNG 이미지 반환"
            )
    })
    @GetMapping("/tickets/{ticketId}/qr.png")
    public ResponseEntity<byte[]> generateTicketQr(
            @PathVariable Long ticketId
    ) {
        TicketENT ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓"));

        String ticketToken = ticket.getTicketToken();

        byte[] png = qrGenerator.generatePng(ticketToken);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.noStore())
                .body(png);
    }


}


