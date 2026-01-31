package org.muses.backendbulidtest251228.domain.event.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.event.dto.EventResDTO;
import org.muses.backendbulidtest251228.domain.event.service.EventSRV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event", description = "이벤트 API")
public class EventCTL {

    private final EventSRV eventSRV;


    // 카드 조회

    // =========================
    // 이벤트 목록 조회 (페이지 기반)
    // =========================
    @Operation(
            summary = "이벤트 목록 조회",
            description = "이벤트 목록을 페이지 기반으로 조회합니다. 제목 검색(keyword)을 지원합니다." +
                    "content에서의 첫 사진을 썸네일로 해주시면 됩니다. 만일 content 의 사진이 없다면 기본 이미지를 넣어주세요."
    )
    @GetMapping
    public ApiResponse<List<EventResDTO>> eventList(
            @Parameter(description = "검색 키워드 (이벤트 제목)", example = "업데이트")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "3")
            @RequestParam(defaultValue = "3") int size
    ) {

        return eventSRV.getAllEvents(keyword, page, size);
    }

    // 상세 조회
    @Operation(
            summary = "이벤트 상세 조회",
            description = "이벤트 ID를 이용해 이벤트 상세 정보를 조회합니다."
    )
    @GetMapping("/{eventId}")
    public ApiResponse<?> eventDetail(
            @Parameter(description = "이벤트 ID", example = "1")
            @PathVariable Long eventId
    ) {

        return eventSRV.getEventDetail(eventId);
    }
    



}
