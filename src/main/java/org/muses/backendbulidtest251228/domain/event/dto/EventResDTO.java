package org.muses.backendbulidtest251228.domain.event.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.muses.backendbulidtest251228.domain.event.enums.EventCategory;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "이벤트 응답 DTO")
public class EventResDTO {

    @Schema(description = "이벤트 ID", example = "1")
    private Long eventId;

    @Schema(description = "이벤트 제목", example = "Muses 업데이트 안내")
    private String title;

    @Schema(description = "이벤트 요약 설명", example = "서비스 기능 개선 안내")
    private String description;

    @Schema(description = "이벤트 상세 내용", example = "이번 업데이트에서는 결제 플로우가 개선되었습니다.")
    private String content;

    @Schema(description = "이벤트 카테고리", example = "NOTICE")
    private EventCategory category;

    @Schema(description = "게시 일시", example = "2026-01-28T10:00:00")
    private LocalDateTime date;
}
