package org.muses.backendbulidtest251228.domain.checkin.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.checkin.dto.CheckinConfirmReqDTO;
import org.muses.backendbulidtest251228.domain.checkin.dto.CheckinConfirmResDTO;
import org.muses.backendbulidtest251228.domain.checkin.generator.QrGenerator;
import org.muses.backendbulidtest251228.domain.checkin.service.CheckinLinkSRV;
import org.muses.backendbulidtest251228.domain.checkin.service.CheckinSRV;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.muses.backendbulidtest251228.domain.ticket.repository.TicketRepo;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    private final MemberRepo memberRepo;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${ticket.base-url}")
    private String ticketUrl;

    // QR 스캔으로 전달된 ticketId를 사용해 체크인을 확정하는 API
    @Operation(
            summary = "체크인 확정",
            description = "QR 스캔으로 전달된 ticketId를 사용해 체크인을 처리합니다."
    )
    @PostMapping ("/result")
    public ApiResponse<CheckinConfirmResDTO> confirm(
            @RequestParam("ticketId") Long ticketId,
            @RequestParam("name") String name,
            @RequestParam("nick") String nick,
            @RequestParam("qty") Integer qty,
            @RequestParam("reward") String reward
    ) {
        CheckinConfirmResDTO confirm = checkinService.confirm(ticketId, name, nick, qty, reward);

        return ApiResponse.success(confirm);
    }




    /**
     * 관람객 티켓용 QR 생성
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
            @PathVariable Long ticketId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {


        if (userDetails == null || userDetails.getUsername() == null || userDetails.getUsername().isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED);
        }
        Member member = memberRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID, "유효하지 않은 인증 정보입니다."));



        TicketENT ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓"));

        String ticketToken = ticket.getTicketToken();

        byte[] png = qrGenerator.generatePng(ticketToken, ticketUrl,ticketId, member);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.noStore())
                .body(png);
    }

    @Operation(
            summary = "티켓 토큰 조회",
            description = "티켓 ID를 통해 체크인에 사용되는 ticketToken을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "티켓 토큰 조회 성공",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                                            example = """
                                        {
                                          "success": true,
                                          "data": {
                                            "ticketToken": "abc123XYZ"
                                          }
                                        }
                                        """
                                    )
                            )
                    )
            }
    )
    @GetMapping("/tickets/{ticketId}")
    public ApiResponse<Map<String, Object>> getToken(
            @PathVariable Long ticketId
    ) {
        TicketENT ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓"));

        String ticketToken = ticket.getTicketToken();


        return ApiResponse.success(Map.of(
                "ticketToken", ticketToken
        ));
    }


    /**
     * 관리자용 체크인 전용 URL을 조회한다
     * 프로젝트당 하나의 체크인 링크만 존재한다
     */
    @Operation(
            summary = "프로젝트 체크인 링크 생성/조회",
            description = "프로젝트별 체크인 전용 URL을 생성하거나 이미 존재하면 기존 링크를 반환합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "체크인 링크 생성/조회 성공",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                                            example = """
                                        {
                                          "success": true,
                                          "data": {
                                            "checkinUrl": "https://mymuses.site/checkin/abc123XYZ"
                                          }
                                        }
                                        """
                                    )
                            )
                    )
            }
    )
    @PostMapping("/projects/{projectId}/link")
    public ApiResponse<Map<String, Object>> createCheckinLink(
            @PathVariable Long projectId
    ) {

        String checkinUrl = linkService.createOrGetLink(projectId, baseUrl);

        return ApiResponse.success(Map.of("checkinUrl", checkinUrl));
    }


}


