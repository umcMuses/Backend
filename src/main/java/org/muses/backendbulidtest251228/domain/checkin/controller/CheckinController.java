package org.muses.backendbulidtest251228.domain.checkin.controller;


import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.checkin.dto.CheckinConfirmReqDTO;
import org.muses.backendbulidtest251228.domain.checkin.dto.CheckinConfirmResDTO;
import org.muses.backendbulidtest251228.domain.checkin.generator.QrGenerator;
import org.muses.backendbulidtest251228.domain.checkin.service.CheckinLinkSRV;
import org.muses.backendbulidtest251228.domain.checkin.service.CheckinService;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.muses.backendbulidtest251228.domain.ticket.repository.TicketRepo;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checkin")
public class CheckinController {

    private final CheckinService checkinService;

    private final CheckinLinkSRV linkService;
    private final QrGenerator qrGenerator;

    private final TicketRepo ticketRepo;

    // 관리자(스태프)가 체크인 페이지에서 QR 을 스캔할때 그 티켓을 USED로 바꾸고
    // 예매자/리워드 정보를 응답으로 돌려주는 API
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
    @Value("${app.base-url}")
    private String baseUrl;
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


