package org.muses.backendbulidtest251228.domain.event.controller;

import org.muses.backendbulidtest251228.domain.event.dto.EventDT;
import org.muses.backendbulidtest251228.domain.event.service.AdminEventSV;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
@Tag(name = "Admin - Event/Notice", description = "관리자 이벤트 관리 API")
public class AdminEventController {

	private final AdminEventSV adminEventSV;

	@PostMapping
	@Operation(summary = "공지글 작성 및 예약 설정 API", description = "")
	public ApiResponse<Long> createEvent(@RequestBody EventDT.EventRequest request) {
		Long eventId = adminEventSV.createEvent(request);
		return ApiResponse.success(eventId);
	}

	@PatchMapping("/{eventId}")
	@Operation(summary = "공지글 수정 API", description = "기존 공지글의 내용을 수정합니다.")
	public ApiResponse<String> updateEvent(
			@PathVariable Long eventId,
			@RequestBody EventDT.EventRequest request) {

		adminEventSV.updateEvent(eventId, request);
		return ApiResponse.success("공지글이 수정되었습니다.");
	}

	@DeleteMapping("/{eventId}")
	@Operation(summary = "공지글 삭제 API", description = "공지글 영구 삭제")
	public ApiResponse<String> updateEvent(@PathVariable Long eventId) {
		adminEventSV.deleteEvent(eventId);
		return ApiResponse.success("공지글이 삭제되었습니다.");
	}

	@GetMapping("/{eventId}")
	@Operation(summary = "관리자 공지글 전체 조회", description = "관리자용 전체 조회입니다. (페이지네이션 적용)")
	public ApiResponse<Page<EventDT.EventResponse>> getAllEvents(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return ApiResponse.success(adminEventSV.getAllEvents(pageable));
	}

	@GetMapping("/{eventId}")
	@Operation(summary = "공지글 개별 상세 조회", description = "관리자용 상세 조회")
	public ApiResponse<EventDT.EventResponse> getEventDetail(@PathVariable Long eventId) {
		return ApiResponse.success(adminEventSV.getEventDetail(eventId));
	}

}
