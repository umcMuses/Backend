package org.muses.backendbulidtest251228.domain.event.dto;

import java.time.LocalDateTime;

import org.muses.backendbulidtest251228.domain.event.entity.Event;
import org.muses.backendbulidtest251228.domain.event.enums.EventCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EventDT {

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class EventRequest {
		@Schema(description = "이벤트 카테고리", example = "NOTICE or COLLABORATIVE")
		@NotNull
		private EventCategory category;

		@Schema(description = "제목", example = "서비스 점검 안내")
		@NotBlank
		private String title;

		@Schema(description = "한줄 설명/요약")
		@NotBlank
		private String description;

		@Schema(description = "본문 내용", example = "<p>상세 본문 내용...</p>")
		@NotBlank
		private String content;

		@Schema(description = "업로드 예약 시간 (null 이면 즉시 업로드, 게시일에 update_at 사용)", example = "2026-02-01T10:00:00")
		private LocalDateTime uploadDateTime;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class EventDetailResponse {
		private Long eventId;
		private EventCategory category;
		private String title;
		private String description;
		private String content;
		private LocalDateTime uploadDateTime;	// 예약 게시일
		private LocalDateTime createdAt;			// 즉시 게시일
		private LocalDateTime updatedAt;			// 수정일
		private String status;					// 예약됨, 게시됨

		public static EventDetailResponse from(Event event) {
			LocalDateTime now = LocalDateTime.now();
			String status = "게시됨";

			if (event.getUploadDateTime() != null && event.getUploadDateTime().isAfter(now)) {
				status = "예약됨";
			}
			return EventDetailResponse.builder()
				.eventId(event.getId())
				.category(event.getCategory())
				.title(event.getTitle())
				.description(event.getDescription())
				.content(event.getContent())
				.uploadDateTime(event.getUploadDateTime())
				.createdAt(event.getCreatedAt())
				.updatedAt(event.getUpdatedAt())
				.status(status)
				.build();
		}
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class EventListResponse {
		private Long eventId;
		private EventCategory category;
		private String title;
		private LocalDateTime uploadDateTime;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		private String status;

		public static EventListResponse from(Event event) {
			LocalDateTime now = LocalDateTime.now();
			String status = (event.getUploadDateTime() != null && event.getUploadDateTime().isAfter(now)) ? "예약됨": "게시됨";

			return EventListResponse.builder()
				.eventId(event.getId())
				.category(event.getCategory())
				.title(event.getTitle())
				.uploadDateTime(event.getUploadDateTime())
				.createdAt(event.getCreatedAt())
				.updatedAt(event.getUpdatedAt())
				.status(status)
				.build();
		}
	}
}
